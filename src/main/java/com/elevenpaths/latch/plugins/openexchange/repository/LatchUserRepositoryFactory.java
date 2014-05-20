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

package com.elevenpaths.latch.plugins.openexchange.repository;

import java.util.Properties;

import com.elevenpaths.latch.plugins.openexchange.util.Utils;

/**
 * Allows the creation of {@link LatchUserRepository} objects. The
 * factory properties are loaded from the config file
 * <code>latch.properties</code> and must follow the notation:
 * <p>
 * <code>latch.userRepositoryFactory.&lt;factoryName&gt;.&lt;propertyName&gt;=&lt;propertyValue&gt;</code>
 */
public abstract class LatchUserRepositoryFactory {

    /** Static SLF4J logger */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LatchUserRepositoryFactory.class);

    /** Factory name */
    protected String name = null;

    /** Factory properties */
    protected Properties properties = null;

    /**
     * Default constructor.
     */
    protected LatchUserRepositoryFactory() {
    }

    /**
     * Get a new user repository factory based on the specified class.
     * 
     * @param factoryName
     *        The factory name.
     * @param factoryProperties
     *        The factory properties.
     * @param factoryClassName
     *        The class to be used.
     * @param classLoader
     *        The class loader to be used.
     * 
     * @return The specified factory.
     * 
     * @throws ClassNotFoundException
     *         If the specified class is not found.
     * @throws IllegalAccessException
     *         If access to the constructor is denied.
     * @throws InstantiationException
     *         If an object of the specified class can't be
     *         instantiated.
     */
    public static LatchUserRepositoryFactory newLatchUserRepositoryFactory(String factoryName, Properties factoryProperties, String factoryClassName, ClassLoader factoryClassLoader) throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        Utils.checkMethodRequiredStringParameterNotEmpty(LOG, "factoryName", factoryName);
        Utils.checkMethodRequiredParameter(LOG, "factoryProperties", factoryProperties);
        Utils.checkMethodRequiredStringParameterNotEmpty(LOG, "factoryClassName", factoryClassName);
        Utils.checkMethodRequiredParameter(LOG, "factoryClassLoader", factoryClassLoader);

        LOG.debug("Instantiating factory " + factoryName + " (" + factoryClassName + ")...");

        /* Instantiate the factory through reflection */

        LatchUserRepositoryFactory rv = (LatchUserRepositoryFactory) Class.forName(factoryClassName, true, factoryClassLoader).newInstance();

        /* Set the factory name */

        rv.setName(factoryName);

        /* Set the factory properties */

        rv.setProperties(factoryProperties);

        return rv;

    }

    /**
     * Get a new user repository. Each factory must implement this
     * method.
     * 
     * @return the user repository.
     */
    public abstract LatchUserRepository newLatchUserRepository();

    /**
     * Gets the factory name.
     * 
     * @return The factory name.
     */
    protected String getName() {
        return name;
    }

    /**
     * Gets the factory properties.
     * 
     * @return The factory properties.
     */
    protected Properties getProperties() {
        return properties;
    }

    /**
     * Sets the factory name.
     * 
     * @param name
     *        The factory name.
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the factory properties.
     * 
     * @param properties
     *        The factory properties.
     */
    protected void setProperties(Properties properties) {
        this.properties = properties;
    }

}