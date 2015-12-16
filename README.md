Spring SAML
====================

Onegini uses this library to test the SAML integration of the Onegini platform

Spring SAML Extension allows seamless inclusion of SAML 2.0 Service Provider capabilities in Spring applications. All products supporting SAML 2.0 in Identity Provider mode (e.g. ADFS 2.0, Shibboleth, OpenAM/OpenSSO, Ping Federate, Okta) can be used to connect with Spring SAML Extension.

Web:
http://projects.spring.io/spring-security-saml/

Sources: https://github.com/spring-projects/spring-security-saml
Documentation: http://docs.spring.io/spring-security-saml/docs/1.0.x-SNAPSHOT/reference/
Jira: https://jira.spring.io/browse/SES/component/10711/
CI: https://build.springsource.org/browse/SES

Releases:
Final: http://repo.spring.io/list/release/org/springframework/security/extensions/
Milestone: http://repo.spring.io/list/milestone/org/springframework/security/extensions/
Snapshot: http://repo.spring.io/list/snapshot/org/springframework/security/extensions/

Support:
Stackoverflow: http://stackoverflow.com/questions/tagged/spring-saml
Forum: http://forum.spring.io/forum/spring-projects/security/saml
Commercial: vladimir@v7security.com

Online demo: http://saml-federation.appspot.com/


IdP Metadata Configuration
==========================

The Sample module needs a running SAML Identity Provider (IdP) for the authentication. The default configuration uses the SAML metadata for a Onegini IdP that 
runs on http://itest.onegini.me:8989. 

The IdP metadata for http://dev.onegini.me:8990 (local docker) is loaded by running the sample with the `docker` profile:

    mvn tomcat7:run -P docker

A different configuration can be loaded via the system property `SAML_SAMPLE_IDP_METADATA_CLASSPATH_RESOURCE`. This resource must refer to a metadata file on 
the classpath.


Requests preparation
====================

Passive request param: `passive=true`

Example: http://localhost:8080/spring-security-saml2-sample/?passive=true

Passive request cookie based request params: `passive=true&authnContexts=urn:oasis:names:tc:SAML:2.0:ac:classes:PreviousSession`

Example: http://localhost:8080/spring-security-saml2-sample/?passive=true&authnContexts=urn:oasis:names:tc:SAML:2.0:ac:classes:PreviousSession

The `authnContexts` param can be used for other valid authentication context values too.