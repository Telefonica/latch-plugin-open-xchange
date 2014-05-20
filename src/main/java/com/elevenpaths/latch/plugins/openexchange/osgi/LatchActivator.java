/*
 * Latch Open-Xchange Plugin
 * Copyright (C) 2014 Eleven Paths
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License, version 2.1 as published by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 */

package com.elevenpaths.latch.plugins.openexchange.osgi;

import com.elevenpaths.latch.plugins.openexchange.impl.LatchAuthenticationService;
import com.elevenpaths.latch.plugins.openexchange.util.Utils;
import com.openexchange.authentication.AuthenticationService;
import com.openexchange.authentication.service.Authentication;
import com.openexchange.config.ConfigurationService;
import com.openexchange.context.ContextService;
import com.openexchange.exception.OXException;
import com.openexchange.osgi.DeferredActivator;
import com.openexchange.osgi.ServiceRegistry;
import com.openexchange.user.UserService;

/**
 * The activator for the Latch bundle for Open-Xchange. It checks if
 * all needed services are already registered and then changes the
 * reference to the authentication service in the server core to a
 * proxy service that will validate users' latches if authentication
 * is correct.
 */
public class LatchActivator extends DeferredActivator {

    /** Static SLF4J logger */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LatchActivator.class);

    /** Static service registry */
    private static final ServiceRegistry registry = LatchServiceRegistry.getServiceRegistry();

    /** The authentication service proxy */
    private AuthenticationService latchAuthenticationService = null;

    /** The authentication service */
    private AuthenticationService proxiedAuthenticationService = null;

    @Override
    protected Class<?>[] getNeededServices() {
        return new Class<?>[] { AuthenticationService.class, ConfigurationService.class, ContextService.class, UserService.class };
    }

    @Override
    protected void handleAvailability(Class<?> arg0) {
        if (arg0 != null) {
            LOG.debug("Handling availability of class " + arg0.getName() + "...");
        }
    }

    @Override
    protected void handleUnavailability(Class<?> arg0) {
        if (arg0 != null) {
            LOG.debug("Handling unavailability of class " + arg0.getName() + "...");
        }
    }

    @Override
    protected void startBundle() throws Exception {

        LOG.debug("Starting bundle...");

        /* Put references to needed services in the service registry */

        registry.clearRegistry();

        for (int i = 0; i < getNeededServices().length; i++) {
            if (getService(getNeededServices()[i]) != null) {
                registry.addService(getNeededServices()[i], getService(getNeededServices()[i]));
                LOG.debug("Adding service " + getNeededServices()[i].getName() + " to service registry...");
            }
        }

        /* Get a lock to avoid race conditions checking and setting the authentication service */

        synchronized (Authentication.class) {

            /* Check if the authentication service has already been set */

            if ((proxiedAuthenticationService = Authentication.getService()) != null) {

                LOG.debug("Authentication service has been set...");

                /* Instantiate the proxy authentication service */

                latchAuthenticationService = getNewLatchAuthenticationService(proxiedAuthenticationService);

                /* Change references to proxy the authentication requests */

                Authentication.dropService(proxiedAuthenticationService);
                Authentication.setService(latchAuthenticationService);

            }
            else {

                LOG.debug("Authentication service has not been set...");

                /* Get a reference to the authentication service */

                proxiedAuthenticationService = registry.getService(AuthenticationService.class);

                /* Instantiate the proxy authentication service */

                latchAuthenticationService = getNewLatchAuthenticationService(proxiedAuthenticationService);

                /* Set reference to proxy the authentication requests. When the server tries to set its reference an error will be logged */

                Authentication.setService(latchAuthenticationService);

            }

            LOG.debug("Proxy authentication service has been set...");

        }

    }

    @Override
    protected void stopBundle() throws Exception {
        LOG.debug("Stopping bundle...");
    }

    /**
     * Returns a new authentication service that will proxy request to
     * the provided authentication service and, if authentication is
     * correct, it will check the status of the user's latch.
     * 
     * @param proxiedAuthenticationService
     *        The authentication service to be used.
     * 
     * @return The new authentication service.
     * 
     * @throws OXException
     *         If something has gone wrong.
     */
    private AuthenticationService getNewLatchAuthenticationService(AuthenticationService proxiedAuthenticationService) throws OXException {

        Utils.checkMethodRequiredParameter(LOG, "proxiedAuthenticationService", proxiedAuthenticationService);

        return new LatchAuthenticationService(proxiedAuthenticationService);

    }

}