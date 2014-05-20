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

package com.elevenpaths.latch.plugins.openexchange.impl;

import java.util.Properties;

import com.elevenpaths.latch.LatchErrorException;
import com.elevenpaths.latch.plugins.openexchange.config.LatchConfig;
import com.elevenpaths.latch.plugins.openexchange.osgi.LatchServiceRegistry;
import com.elevenpaths.latch.plugins.openexchange.util.Utils;
import com.elevenpaths.latch.sdk.impl.LatchSDKStatus;
import com.openexchange.authentication.Authenticated;
import com.openexchange.authentication.AuthenticationService;
import com.openexchange.authentication.LoginExceptionCodes;
import com.openexchange.authentication.LoginInfo;
import com.openexchange.config.ConfigurationService;
import com.openexchange.exception.OXException;
import com.openexchange.osgi.ServiceRegistry;

/**
 * An authentication service for Open-Xchange that integrates Latch
 * validation in the authentication process.
 * <p>
 * It does so proxying authentication requests to the authentication
 * service specified in the constructor and if the credentials are
 * valid, it then checks if the user has a paired account and what is
 * its status.
 * <p>
 * If something goes wrong checking if the user has a paired account
 * or checking its status, the default behavior is to allow access.
 * <p>
 * If needed, this behavior can be changed setting the property
 * <code>latch.stopOnError</code> to <code>true</code> in the config
 * file <code>latch.properties</code>.
 */
public class LatchAuthenticationService implements AuthenticationService {

    /** Static SLF4J logger */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LatchAuthenticationService.class);

    /** Static service registry */
    private static ServiceRegistry registry = LatchServiceRegistry.getServiceRegistry();

    /** The original authentication service */
    private AuthenticationService proxiedAuthenticationService = null;

    /** The Latch configuration */
    private LatchConfig latchConfig = null;

    /**
     * Default constructor. It shouldn't be used.
     */
    @SuppressWarnings("unused")
    private LatchAuthenticationService() {
    }

    /**
     * Constructs a LatchAuthenticationService and sets the provided
     * authentication service as the one that will be used to
     * authenticate users.
     * <p>
     * It also initialize the Latch SDK implementation and the Latch
     * user repository with the properties specified in the config
     * file <code>latch.properties</code>.
     * 
     * @param proxiedAuthenticationService
     *        The authentication service to be used.
     * 
     * @throws OXException
     *         If something has gone wrong.
     */
    public LatchAuthenticationService(AuthenticationService proxiedAuthenticationService) throws OXException {

        Utils.checkMethodRequiredParameter(LOG, "proxiedAuthenticationService", proxiedAuthenticationService);

        /* Set the reference to the actual authentication service */

        this.proxiedAuthenticationService = proxiedAuthenticationService;

        /* Get a reference to the Configuration service */

        ConfigurationService config = registry.getService(ConfigurationService.class);

        /* Get a reference to the bundle properties */

        Properties properties = config.getFile("latch.properties");

        /* If the file doesn't exist throw an exception */

        if (properties == null) {
            LOG.error("File latch.properties not found in expected location.");
            throw OXException.general("File latch.properties not found in expected location");
        }

        /* Initialize the Latch configuration */

        latchConfig = new LatchConfig(properties);

    }

    @Override
    public Authenticated handleAutoLoginInfo(LoginInfo loginInfo) throws OXException {

        LOG.debug("Handling AutoLogin Info...");

        Utils.checkMethodRequiredParameter(LOG, "loginInfo", loginInfo);

        if (proxiedAuthenticationService != null) {

            /* Authenticate the user using the original authentication service */

            Authenticated rv = proxiedAuthenticationService.handleAutoLoginInfo(loginInfo);

            /* Check user's latch. If it's off a LoginException is thrown from the checkLatch method */

            if (rv != null) {
                checkLatch(rv, loginInfo);
            }

            return rv;

        }
        else {
            LOG.error("Unable to authenticate the user. Proxied authentication service not set.");
            throw OXException.general("Unable to authenticate the user. Proxied authentication service not set");
        }

    }

    @Override
    public Authenticated handleLoginInfo(LoginInfo loginInfo) throws OXException {

        LOG.debug("Handling Login Info...");

        Utils.checkMethodRequiredParameter(LOG, "loginInfo", loginInfo);

        if (proxiedAuthenticationService != null) {

            /* Authenticate the user using the original authentication service */

            Authenticated rv = proxiedAuthenticationService.handleLoginInfo(loginInfo);

            /* Check user's latch. If it's off a LoginException is thrown from the checkLatch method */

            if (rv != null) {
                checkLatch(rv, loginInfo);
            }

            return rv;

        }
        else {
            LOG.error("Unable to authenticate the user. Proxied authentication service not set.");
            throw OXException.general("Unable to authenticate the user. Proxied authentication service not set");
        }

    }

    /**
     * Checks if the user has a paired account and what is its status.
     * The latch is paired if the user repository method
     * <code>getLatchAccountId</code> returns a not null value.
     * <p>
     * If an exception is thrown checking if the user has a paired
     * account the default behavior is to allow access (as if the user
     * hadn't had a paired account).
     * <p>
     * If the latch is in <code>UNKNOWN<code> status or an application
     * exception is thrown while checking the latch the default
     * behavior is to allow access (as if the user had had his latch
     * unlocked).
     * <p>
     * This default behavior can be changed setting the configuration
     * parameter <code>latch.stopOnError</code> to <code>true<code>.
     * Then, if an error happens getting the user's <code>latchAccountId</code>
     * or checking the user's latch status an exception will be thrown
     * and the user won't be able to access the system.
     * 
     * @param authenticated
     *        The result of the authentication service.
     * @param loginInfo
     *        The login information introduced by the user.
     * 
     * @throws OXException
     *         If something has gone wrong.
     */
    private void checkLatch(Authenticated authenticated, LoginInfo loginInfo) throws OXException {

        LOG.debug("Checking latch for user " + authenticated.getUserInfo() + " of context " + authenticated.getContextInfo() + "...");

        if (latchConfig.getLatchSDK() == null) {
            LOG.error("Unable to check the user's latch. Latch SDK not set");
            throw OXException.general("Unable to check the user's latch. Latch SDK not set");
        }

        if (latchConfig.getUserRepository() == null) {
            LOG.error("Unable to check the user's latch. User repository not set");
            throw OXException.general("Unable to check the user's latch. User repository not set");
        }

        String latchAccountId = null;

        try {

            /* Check if the user has a paired account */

            latchAccountId = latchConfig.getUserRepository().getLatchAccountId(loginInfo);

        }
        catch (OXException e) {

            if (latchConfig.stopOnError()) {
                LOG.error("Error checking if the user has a paired latch");
                throw LoginExceptionCodes.UNKNOWN.create("Error checking if the user has a paired latch", e);
            }

        }

        /* The user has a paired account. Check the latch status. */

        if (latchAccountId != null) {

            try {

                LatchSDKStatus latchStatus = latchConfig.getLatchSDK().parsedStatus(latchAccountId, latchConfig.getLatchOperationId());

                if (latchStatus.equals(LatchSDKStatus.LOCKED)) {
                    LOG.debug("User " + authenticated.getUserInfo() + " has his latch locked.");
                    throw LoginExceptionCodes.INVALID_CREDENTIALS.create();
                }
                else if (latchStatus.equals(LatchSDKStatus.UNKNOWN)) {
                    if (latchConfig.stopOnError()) {
                        LOG.error("The user's latch is in UNKNOWN status");
                        throw LoginExceptionCodes.UNKNOWN.create("The user's latch is in UNKNOWN status");
                    }
                }

            }
            catch (LatchErrorException e) {

                LOG.error("Error checking latch.", e);

                if (latchConfig.stopOnError()) {
                    LOG.error("Error checking the status of the latch");
                    throw LoginExceptionCodes.UNKNOWN.create("Error checking the status of the latch", e);
                }

            }

        }

    }

}