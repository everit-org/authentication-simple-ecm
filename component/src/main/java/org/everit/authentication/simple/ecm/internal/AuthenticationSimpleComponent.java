/*
 * Copyright (C) 2011 Everit Kft. (http://www.everit.biz)
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
package org.everit.authentication.simple.ecm.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import org.everit.authentication.simple.AuthenticationSimpleImpl;
import org.everit.authentication.simple.SimpleSubjectManager;
import org.everit.authentication.simple.ecm.AuthenticationSimpleConstants;
import org.everit.authenticator.Authenticator;
import org.everit.credential.encryptor.CredentialEncryptor;
import org.everit.credential.encryptor.CredentialMatcher;
import org.everit.osgi.ecm.annotation.Activate;
import org.everit.osgi.ecm.annotation.Component;
import org.everit.osgi.ecm.annotation.ConfigurationPolicy;
import org.everit.osgi.ecm.annotation.Deactivate;
import org.everit.osgi.ecm.annotation.ManualService;
import org.everit.osgi.ecm.annotation.ServiceRef;
import org.everit.osgi.ecm.annotation.attribute.StringAttribute;
import org.everit.osgi.ecm.annotation.attribute.StringAttributes;
import org.everit.osgi.ecm.component.ComponentContext;
import org.everit.osgi.ecm.extender.ECMExtenderConstants;
import org.everit.persistence.querydsl.support.QuerydslSupport;
import org.everit.resource.resolver.ResourceIdResolver;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import aQute.bnd.annotation.headers.ProvideCapability;

/**
 * ECM component for {@link SimpleSubjectManager}, {@link Authenticator} and
 * {@link ResourceIdResolver} interface based on {@link AuthenticationSimpleImpl}.
 */
@Component(componentId = AuthenticationSimpleConstants.SERVICE_FACTORYPID_AUTHENTICATION_SIMPLE,
    configurationPolicy = ConfigurationPolicy.FACTORY, label = "Everit Authentication Simple",
    description = "The component of the Authentication Simple.")
@ProvideCapability(ns = ECMExtenderConstants.CAPABILITY_NS_COMPONENT,
    value = ECMExtenderConstants.CAPABILITY_ATTR_CLASS + "=${@class}")
@StringAttributes({
    @StringAttribute(attributeId = Constants.SERVICE_DESCRIPTION,
        defaultValue = AuthenticationSimpleConstants.DEFAULT_SERVICE_DESCRIPTION,
        priority = AuthenticationSimpleComponent.P1_SERVICE_DESCRIPTION,
        label = "Service Description",
        description = "The description of this component configuration. It is used to easily "
            + "identify the service registered by this component.") })
@ManualService({ SimpleSubjectManager.class, Authenticator.class, ResourceIdResolver.class })
public class AuthenticationSimpleComponent {

  public static final int P1_SERVICE_DESCRIPTION = 1;

  public static final int P2_QUERYDSL_SUPPORT = 2;

  public static final int P3_CREDENTIAL_ENCRYPTOR = 3;

  public static final int P4_CREDENTIAL_MATCHER = 4;

  private CredentialEncryptor credentialEncryptor;

  private CredentialMatcher credentialMatcher;

  private QuerydslSupport querydslSupport;

  private ServiceRegistration<?> serviceRegistration;

  /**
   * Activate method of component.
   */
  @Activate
  public void activate(final ComponentContext<AuthenticationSimpleComponent> componentContext) {
    AuthenticationSimpleImpl authenticationSimple =
        new AuthenticationSimpleImpl(credentialEncryptor, credentialMatcher, querydslSupport);

    Dictionary<String, Object> serviceProperties =
        new Hashtable<>(componentContext.getProperties());
    serviceRegistration = componentContext
        .registerService(new String[] { SimpleSubjectManager.class.getName(),
            Authenticator.class.getName(), ResourceIdResolver.class.getName() },
            authenticationSimple,
            serviceProperties);
  }

  /**
   * Component deactivate method.
   */
  @Deactivate
  public void deactivate() {
    if (serviceRegistration != null) {
      serviceRegistration.unregister();
    }
  }

  @ServiceRef(attributeId = AuthenticationSimpleConstants.ATTR_CREDENTIAL_ENCRYPTOR,
      defaultValue = "", attributePriority = P3_CREDENTIAL_ENCRYPTOR,
      label = "CredentialEncryptor filter",
      description = "OSGi Service filter expression for CredentialEncryptor instance.")
  public void setCredentialEncryptor(final CredentialEncryptor credentialEncryptor) {
    this.credentialEncryptor = credentialEncryptor;
  }

  @ServiceRef(attributeId = AuthenticationSimpleConstants.ATTR_CREDENTIAL_MATCHER,
      defaultValue = "", attributePriority = P4_CREDENTIAL_MATCHER,
      label = "CredentialMatcher filter",
      description = "OSGi Service filter expression for CredentialMatcher instance.")
  public void setCredentialMatcher(final CredentialMatcher credentialMatcher) {
    this.credentialMatcher = credentialMatcher;
  }

  @ServiceRef(attributeId = AuthenticationSimpleConstants.ATTR_QUERYDSL_SUPPORT, defaultValue = "",
      attributePriority = P2_QUERYDSL_SUPPORT, label = "Querydsl Support OSGi filter",
      description = "OSGi Service filter expression for QueryDSLSupport instance.")
  public void setQuerydslSupport(final QuerydslSupport querydslSupport) {
    this.querydslSupport = querydslSupport;
  }

}
