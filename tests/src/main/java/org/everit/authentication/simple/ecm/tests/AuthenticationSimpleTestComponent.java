/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.everit.authentication.simple.ecm.tests;

import org.everit.authentication.simple.SimpleSubject;
import org.everit.authentication.simple.SimpleSubjectManager;
import org.everit.authentication.simple.schema.qdsl.QSimpleSubject;
import org.everit.authenticator.Authenticator;
import org.everit.osgi.dev.testrunner.TestRunnerConstants;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Service;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.everit.persistence.querydsl.support.QuerydslSupport;
import org.everit.resource.ResourceService;
import org.everit.resource.resolver.ResourceIdResolver;
import org.everit.transaction.propagator.TransactionPropagator;
import org.junit.Assert;
import org.junit.Test;

import com.mysema.query.sql.dml.SQLDeleteClause;

import aQute.bnd.annotation.headers.ProvideCapability;

/**
 * Test for {@link SimpleSubjectManager}.
 */
@Component(componentId = "SimpleSubjectManagerTest",
    configurationPolicy = ConfigurationPolicy.OPTIONAL)
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
@StringAttributes({
    @StringAttribute(attributeId = TestRunnerConstants.SERVICE_PROPERTY_TESTRUNNER_ENGINE_TYPE,
        defaultValue = "junit4"),
    @StringAttribute(attributeId = TestRunnerConstants.SERVICE_PROPERTY_TEST_ID,
        defaultValue = "SimpleSubjectManagerTest") })
@Service(value = AuthenticationSimpleTestComponent.class)
public class AuthenticationSimpleTestComponent {

  private Authenticator authenticator;

  private QuerydslSupport querydslSupport;

  private ResourceIdResolver resourceIdResolver;

  private ResourceService resourceService;

  private SimpleSubjectManager simpleSubjectManager;

  private TransactionPropagator transactionPropagator;

  private SimpleSubject createWithResource(final String principal, final String plainCredential) {
    return transactionPropagator.required(() -> {
      long resourceId = resourceService.createResource();
      SimpleSubject simpleSubject =
          simpleSubjectManager.create(resourceId, principal, plainCredential);
      Assert.assertNotNull(simpleSubject);
      Assert.assertEquals(principal, simpleSubject.getPrincipal());
      Assert.assertEquals(resourceId, simpleSubject.getResourceId());
      return simpleSubject;
    });
  }

  private void deleteAllSimpleSubjects() {
    querydslSupport.execute((connection, configuration) -> {
      QSimpleSubject qSimpleSubject = QSimpleSubject.simpleSubject;
      new SQLDeleteClause(connection, configuration, qSimpleSubject).execute();
      return null;
    });
  }

  @ServiceRef(defaultValue = "")
  public void setAuthenticator(final Authenticator authenticator) {
    this.authenticator = authenticator;
  }

  @ServiceRef(defaultValue = "")
  public void setQuerydslSupport(final QuerydslSupport querydslSupport) {
    this.querydslSupport = querydslSupport;
  }

  @ServiceRef(defaultValue = "")
  public void setResourceIdResolver(final ResourceIdResolver resourceIdResolver) {
    this.resourceIdResolver = resourceIdResolver;
  }

  @ServiceRef(defaultValue = "")
  public void setResourceService(final ResourceService resourceService) {
    this.resourceService = resourceService;
  }

  @ServiceRef(defaultValue = "")
  public void setSimpleSubjectManager(final SimpleSubjectManager simpleSubjectManager) {
    this.simpleSubjectManager = simpleSubjectManager;
  }

  @ServiceRef(defaultValue = "")
  public void setTransactionPropagator(final TransactionPropagator transactionPropagator) {
    this.transactionPropagator = transactionPropagator;
  }

  @Test
  public void testAuthenticator() {
    deleteAllSimpleSubjects();
    String principal = "principal";
    String plainCredential = "credential";
    createWithResource(principal, plainCredential);

    Assert.assertEquals(principal, authenticator.authenticate(principal, plainCredential).get());
    Assert.assertFalse(
        authenticator.authenticate(principal, plainCredential + plainCredential).isPresent());
    Assert.assertFalse(authenticator.authenticate(principal, null).isPresent());

    String newPlainCredential = "credential_new";
    simpleSubjectManager.updateCredential(principal, newPlainCredential);

    Assert.assertEquals(principal, authenticator.authenticate(principal, newPlainCredential).get());
    Assert.assertFalse(authenticator.authenticate(principal, plainCredential).isPresent());

    Assert.assertFalse(
        authenticator.authenticate(principal + principal, newPlainCredential).isPresent());

  }

  @Test
  public void testManager() {
    deleteAllSimpleSubjects();
    String principal = "principal";
    String plainCredential = "credential";

    SimpleSubject originalSimpleSubject = createWithResource(principal, plainCredential);

    SimpleSubject simpleSubject = simpleSubjectManager.readSimpleSubjectByPrincipal(principal);
    Assert.assertEquals(originalSimpleSubject, simpleSubject);

    String encryptedCredential = simpleSubjectManager.readEncryptedCredential(principal);
    Assert.assertNotNull(encryptedCredential);

    String newPrincipal = "principal_new";
    Assert.assertTrue(simpleSubjectManager.updatePrincipal(principal, newPrincipal));
    Assert.assertFalse(simpleSubjectManager.updatePrincipal(principal, newPrincipal));
    Assert.assertNull(simpleSubjectManager.readSimpleSubjectByPrincipal(principal));
    Assert.assertEquals(encryptedCredential,
        simpleSubjectManager.readEncryptedCredential(newPrincipal));

    simpleSubject = simpleSubjectManager.readSimpleSubjectByPrincipal(newPrincipal);
    Assert.assertEquals(originalSimpleSubject.getSimpleSubjectId(),
        simpleSubject.getSimpleSubjectId());
    Assert.assertEquals(originalSimpleSubject.getResourceId(), simpleSubject.getResourceId());
    Assert.assertEquals(newPrincipal, simpleSubject.getPrincipal());

    String newPlainCredential = "credential_new";
    Assert.assertFalse(
        simpleSubjectManager.updateCredential(principal, plainCredential, newPlainCredential));
    Assert.assertTrue(
        simpleSubjectManager.updateCredential(newPrincipal, plainCredential, newPlainCredential));
    Assert.assertNotEquals(encryptedCredential,
        simpleSubjectManager.readEncryptedCredential(newPrincipal));
    Assert.assertFalse(
        simpleSubjectManager.updateCredential(newPrincipal, plainCredential, newPlainCredential));

    Assert.assertFalse(simpleSubjectManager.updateCredential(principal, newPlainCredential));
    Assert.assertTrue(simpleSubjectManager.updateCredential(newPrincipal, null));
    Assert.assertTrue(simpleSubjectManager.updateCredential(newPrincipal, null, null));
    Assert
        .assertTrue(simpleSubjectManager.updateCredential(newPrincipal, null, newPlainCredential));

    Assert.assertFalse(simpleSubjectManager.delete(principal));
    Assert.assertTrue(simpleSubjectManager.delete(newPrincipal));
    Assert.assertFalse(simpleSubjectManager.delete(newPrincipal));
  }

  @Test
  public void testResourceIdResolver() {
    deleteAllSimpleSubjects();
    String principal = "principal";
    String plainCredential = "credential";
    SimpleSubject simpleSubject = createWithResource(principal, plainCredential);

    Assert.assertEquals(simpleSubject.getResourceId(),
        resourceIdResolver.getResourceId(principal).get().longValue());
    Assert.assertFalse(resourceIdResolver.getResourceId(principal + principal).isPresent());
  }

}
