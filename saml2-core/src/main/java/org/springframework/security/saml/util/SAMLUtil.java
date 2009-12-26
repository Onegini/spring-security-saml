/* Copyright 2009 Vladimir Sch�fer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.saml.util;

import org.opensaml.saml2.metadata.*;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.saml.websso.WebSSOProfileOptions;

import java.util.List;

/**
 * Utility class for SAML entities
 *
 * @author Vladimir Sch�fer
 */
public class SAMLUtil {

    private final static Logger log = LoggerFactory.getLogger(SAMLUtil.class);

    /**
     * Returns assertion consumer service of the given SP for the given binding. If the specified binding
     * can't be found, default binding is returned. In case no binding is marked as default, first binding
     * for assertionConsumber endpoint is used. In case SP doesn't contain any assertionConsumber endpoind
     * exception is thrown.
     *
     * @param descriptor descriptor to search for binding in
     * @param binding    binding type
     * @return consumer service capable of handling the given binding
     * @throws MetadataProviderException in case there is not service capable of handling the binding
     */
    public static AssertionConsumerService getAssertionConsubmerForBinding(SPSSODescriptor descriptor, String binding) throws MetadataProviderException {
        List<AssertionConsumerService> services = descriptor.getAssertionConsumerServices();
        AssertionConsumerService foundService = null;
        for (AssertionConsumerService service : services) {
            if (binding.equals(service.getBinding())) {
                return service;
            } else if (foundService == null) {
                foundService = service;
            }
        }

        if (descriptor.getDefaultAssertionConsumerService() != null) {
            return descriptor.getDefaultAssertionConsumerService();
        } else if (foundService != null) {
            return foundService;
        }

        log.debug("No binding found for SP with binding " + binding);
        throw new MetadataProviderException("Binding " + binding + " is not supported for this SP and no other was found");
    }

    /**
     * Returns SSOService for given binding of the IDP.
     *
     * @param descriptor IDP to search for service in
     * @param binding    binding supported by the service
     * @return SSO service capable of handling the given binding
     * @throws MetadataProviderException if the service can't be determined
     */
    public static SingleSignOnService getSSOServiceForBinding(IDPSSODescriptor descriptor, String binding) throws MetadataProviderException {
        List<SingleSignOnService> services = descriptor.getSingleSignOnServices();
        for (SingleSignOnService service : services) {
            if (binding.equals(service.getBinding())) {
                return service;
            }
        }
        log.debug("No binding found for IDP with binding " + binding);
        throw new MetadataProviderException("Binding " + binding + " is not supported for this IDP");
    }

    /**
     * Returns Single logout service for given binding of the IDP.
     *
     * @param descriptor IDP to search for service in
     * @param binding    binding supported by the service
     * @return SSO service capable of handling the given binding
     * @throws MetadataProviderException if the service can't be determined
     */
    public static SingleLogoutService getLogoutServiceForBinding(SSODescriptor descriptor, String binding) throws MetadataProviderException {
        List<SingleLogoutService> services = descriptor.getSingleLogoutServices();
        for (SingleLogoutService service : services) {
            if (binding.equals(service.getBinding())) {
                return service;
            }
        }
        log.debug("No binding found for IDP with binding " + binding);
        throw new MetadataProviderException("Binding " + binding + " is not supported for this IDP");
    }

    public static String getLoginBinding(WebSSOProfileOptions options, IDPSSODescriptor idp, SPSSODescriptor sp) throws MetadataProviderException {

        String requiredBinding = options.getBinding();
        for (Endpoint idpEndpoint : idp.getSingleSignOnServices()) {
            if (idpEndpoint.getBinding().equals(requiredBinding)) {
                return requiredBinding;
            }
        }

        return SAMLUtil.getDefaultBinding(idp);

    }

    public static String getLogoutBinding(IDPSSODescriptor idp, SPSSODescriptor sp) throws MetadataProviderException {

        List<SingleLogoutService> logoutServices = idp.getSingleLogoutServices();
        if (logoutServices.size() == 0) {
            throw new MetadataProviderException("IDP doesn't contain any SingleLogout endpoints");
        }

        String binding = null;

        // Let's find first binding supported by both IDP and SP
        idp : for (SingleLogoutService idpService : logoutServices) {
            for (SingleLogoutService spService : sp.getSingleLogoutServices()) {
                if (idpService.getBinding().equals(spService.getBinding()) ) {
                    binding = idpService.getBinding();
                    break idp;
                }
            }
        }

        // In case there's no common endopoint let's use first available
        if (binding == null) {
            binding = idp.getSingleLogoutServices().iterator().next().getBinding();
        }

        return binding;

    }

    /**
     * Returns default binding supported by IDP.
     * @param descriptor descriptor to return binding for
     * @return first binding in the list of supported
     * @throws MetadataProviderException no binding found
     */
    public static String getDefaultBinding(IDPSSODescriptor descriptor) throws MetadataProviderException {
        for (SingleSignOnService service : descriptor.getSingleSignOnServices()) {
            return service.getBinding();
        }
        throw new MetadataProviderException("No SSO binding found for IDP");
    }
}