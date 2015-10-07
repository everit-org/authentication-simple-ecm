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
package org.everit.authentication.simple.ecm;

/**
 * Constants of the Authentication Simple component.
 */
public final class AuthenticationSimpleConstants {

  public static final String DEFAULT_SERVICE_DESCRIPTION =
      "Default Authentication Simple Component";
  /**
   * The property name of the OSGi filter expression defining which CredentialEncryptor should be
   * used.
   */
  public static final String ATTR_CREDENTIAL_ENCRYPTOR = "credentialEncryptor.target";

  /**
   * The property name of the OSGi filter expression defining which CredentialMatcher should be
   * used.
   */
  public static final String ATTR_CREDENTIAL_MATCHER = "credentialMatcher.target";

  /**
   * The property name of the OSGi filter expression defining which LogService should be used.
   */
  public static final String PROP_LOG_SERVICE = "logService.target";

  /**
   * The property name of the OSGi filter expression defining which QuerydslSupport should be used.
   */
  public static final String ATTR_QUERYDSL_SUPPORT = "querydslSupport.target";

  /**
   * The service factory PID of the Authentication Simple component.
   */
  public static final String SERVICE_FACTORYPID_AUTHENTICATION_SIMPLE =
      "org.everit.osgi.authentication.simple.AuthenticationSimple";

  private AuthenticationSimpleConstants() {
  }

}
