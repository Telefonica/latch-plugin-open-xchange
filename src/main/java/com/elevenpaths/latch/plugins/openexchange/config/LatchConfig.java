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

package com.elevenpaths.latch.plugins.openexchange.config;

import java.util.Iterator;
import java.util.Properties;

import com.elevenpaths.latch.plugins.openexchange.repository.LatchUserRepository;
import com.elevenpaths.latch.plugins.openexchange.repository.LatchUserRepositoryFactory;
import com.elevenpaths.latch.plugins.openexchange.util.Utils;
import com.elevenpaths.latch.sdk.impl.LatchSDKImpl;
import com.openexchange.exception.OXException;

public class LatchConfig {

    /** Static SLF4J logger */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LatchConfig.class);

    /** Latch SDK implementation */
    private LatchSDKImpl latchSDK = null;

    /** Latch application id */
    private String latchApplicationId = null;

    /** Latch application secret */
    private String latchApplicationSecret = null;

    /** Latch operation id */
    private String latchOperationId = null;

    /** What to do if something goes wrong checking a latch */
    private boolean stopOnError = false;

    /** Latch user repository */
    private LatchUserRepository userRepository = null;

    /**
     * Constructs a <code>LatchConfig</code> object with the
     * information provided in the provided <code>properties</code>.
     * 
     * @param properties
     *        The configuration properties.
     * 
     * @throws OXException
     *         If there are missing properties or if something goes
     *         wrong.
     */
    public LatchConfig(Properties properties) throws OXException {

        /* Check required parameters */

        Utils.checkMethodRequiredParameter(LOG, "properties", properties);

        /* Initialize the Latch SDK and the stopOnError parameter */

        initLatchSDK(properties);

        /* Initialize the Latch user repository */

        initUserRepository(properties);

    }

    /**
     * Get a reference to the Latch SDK implementation.
     * 
     * @return A reference to the Latch SDK implementation.
     */
    public LatchSDKImpl getLatchSDK() {
        return latchSDK;
    }

    /**
     * Set a reference to the Latch SDK implementation.
     * 
     * @param latchSDK
     *        A reference to the Latch SDK implementation.
     */
    public void setLatchSDK(LatchSDKImpl latchSDK) {
        this.latchSDK = latchSDK;
    }

    /**
     * Get the Latch application id.
     * 
     * @return The Latch application id.
     */
    public String getLatchApplicationId() {
        return latchApplicationId;
    }

    /**
     * Set the Latch application id.
     * 
     * @param latchApplicationId
     *        The Latch application id.
     */
    public void setLatchApplicationId(String latchApplicationId) {
        this.latchApplicationId = latchApplicationId;
    }

    /**
     * Get the Latch application secret.
     * 
     * @return The Latch application secret.
     */
    public String getLatchApplicationSecret() {
        return latchApplicationSecret;
    }

    /**
     * Set the Latch application secret.
     * 
     * @param latchApplicationSecret
     *        The Latch application secret.
     */
    public void setLatchApplicationSecret(String latchApplicationSecret) {
        this.latchApplicationSecret = latchApplicationSecret;
    }

    /**
     * Get the Latch operation id.
     * 
     * @return The Latch operation id.
     */
    public String getLatchOperationId() {
        return latchOperationId;
    }

    /**
     * Set the Latch operation id.
     * 
     * @param latchOperationId
     *        The Latch operation id.
     */
    public void setLatchOperationId(String latchOperationId) {
        this.latchOperationId = latchOperationId;
    }

    /**
     * Get the stopOnError parameter.
     * 
     * @return The stopOnError parameter.
     */
    public boolean stopOnError() {
        return stopOnError;
    }

    /**
     * Set the stopOnError parameter.
     * 
     * @param stopOnError
     *        The stopOnError parameter.
     */
    public void setStopOnError(boolean stopOnError) {
        this.stopOnError = stopOnError;
    }

    /**
     * Get a reference to the Latch user repository.
     * 
     * @return A reference to the Latch user repository.
     */
    public LatchUserRepository getUserRepository() {
        return userRepository;
    }

    /**
     * Set a reference to the Latch user repository.
     * 
     * @param userRepository
     *        A reference to the Latch user repository.
     */
    public void setUserRepository(LatchUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Filters the configuration properties needed by the Latch user
     * repository.
     * 
     * @throws OXException
     *         If something has gone wrong.
     */
    private Properties filterUserRepositoryProperties(Properties fullProperties, String factoryName) {

        Properties rv = new Properties();

        Iterator<String> iterator = fullProperties.stringPropertyNames().iterator();

        while (iterator.hasNext()) {

            String key = iterator.next();

            if (key.startsWith("latch.userRepositoryFactory." + factoryName + ".")) {
                rv.setProperty(key, fullProperties.getProperty(key));
            }

        }

        return rv;

    }

    /**
     * Tries to get a property value from the provided
     * <code>properties</code> and checks that it's not null or empty.
     * 
     * @param properties
     *        The configuration properties.
     * @param propertyName
     *        The property name.
     * 
     * @return The property value.
     * 
     * @throws OXException
     *         If the property is null or empty.
     */
    private String getRequiredProperty(Properties properties, String propertyName) throws OXException {

        String rv = properties.getProperty(propertyName);

        if (rv == null) {
            LOG.error("No " + rv + " property found.");
            throw OXException.general("No " + rv + " property found");
        }

        if (rv.isEmpty()) {
            LOG.error(rv + " property can't be empty.");
            throw OXException.general(rv + " property can't be empty");
        }

        return rv;

    }

    /**
     * Initializes the Latch SDK and the stopOnError parameter.
     * 
     * @throws OXException
     *         If something goes wrong.
     */
    private void initLatchSDK(Properties properties) throws OXException {

        /* Get the application id, application secret and operation id (optional) */

        latchApplicationId = getRequiredProperty(properties, "latch.applicationId");
        latchApplicationSecret = getRequiredProperty(properties, "latch.applicationSecret");
        latchOperationId = properties.getProperty("latch.operationId");

        /* Set the reference to the Latch SDK */

        latchSDK = new LatchSDKImpl(latchApplicationId, latchApplicationSecret);

        /* Initialize the stopOnError configuration parameter */

        if (properties.getProperty("latch.stopOnError") != null) {
            if (properties.getProperty("latch.stopOnError").equals("true")) {
                stopOnError = true;
            }
        }

    }

    /**
     * Initializes the Latch user repository.
     * 
     * @throws OXException
     *         If something has gone wrong.
     */
    private void initUserRepository(Properties properties) throws OXException {

        /* Get the required properties */

        String latchUserRepositoryFactoryName = getRequiredProperty(properties, "latch.userRepositoryFactoryName");
        String latchUserRepositoryFactoryClassName = getRequiredProperty(properties, "latch.userRepositoryFactoryClassName");

        try {

            /* Get the properties needed by the user repository */

            Properties latchUserRepositoryFactoryProperties = filterUserRepositoryProperties(properties, latchUserRepositoryFactoryName);

            /* Set the reference to the user repository */

            LatchUserRepositoryFactory userRepositoryFactory = LatchUserRepositoryFactory.newLatchUserRepositoryFactory(latchUserRepositoryFactoryName, latchUserRepositoryFactoryProperties, latchUserRepositoryFactoryClassName, Thread.currentThread().getContextClassLoader());
            userRepository = userRepositoryFactory.newLatchUserRepository();

        }
        catch (ClassNotFoundException e) {
            LOG.error("Unable to instantiate the user repository factory.", e);
            throw OXException.general("Unable to instantiate the user repository factory", e);
        }
        catch (InstantiationException e) {
            LOG.error("Unable to instantiate the user repository factory.", e);
            throw OXException.general("Unable to instantiate the user repository factory", e);
        }
        catch (IllegalAccessException e) {
            LOG.error("Unable to instantiate the user repository factory.", e);
            throw OXException.general("Unable to instantiate the user repository factory", e);
        }

    }

}