authentication-simple-ecm
=========================

ECM based components for [authentication-simple][1].

#Component
The module contains one ECM component. The component can be 
instantiated multiple times via Configuration Admin. The component uses the 
[credential-encryptor-api][2] and registers three OSGi services: 
 - **SimpleSubjectManager**: Managing the simple subject table (see below): 
 creating and reading simple subjects, updating their principals and 
 credentials. 
 - **Authenticator**: Authenticates the simple subjects based on their 
 principal and credential. The interface is provided by the 
 [authenticator-api][3].
 - **ResourceIdResolver**: Maps the principal of the simple subject to a 
 Resource ID. The interface is provided by the [resource-resolver-api][4].

[1]: https://github.com/everit-org/authentication-simple
[2]: https://github.com/everit-org/credential-encryptor-api
[3]: https://github.com/everit-org/authenticator-api
[4]: https://github.com/everit-org/resource-resolver-api
