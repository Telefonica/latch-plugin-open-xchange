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

package com.elevenpaths.latch.plugins.openexchange.repository.impl.ldap;

import java.util.Hashtable;
import java.util.Iterator;

import com.elevenpaths.latch.plugins.openexchange.repository.LatchUserRepository;
import com.elevenpaths.latch.plugins.openexchange.repository.LatchUserRepositoryFactory;

/**
 * Implementation that uses an LDAP directory to get the accountId
 * associated with a user. The communication with the LDAP directory
 * is done through JNDI. All the user repository properties that
 * begins with:
 * <ul>
 * <li>
 * <code>latch.userRepositoryFactory.&lt;factoryName&gt;.java.naming.*</code>
 * </li>
 * <li>
 * <code>latch.userRepositoryFactory.&lt;factoryName&gt;.com.sun.jndi.ldap.*</code>
 * </li>
 * </ul>
 * will be passed to the environment used to instantiate the
 * <code>DirContext</code> removing the
 * <code>latch.userRepositoryFactory.&lt;factoryName&gt;.</code>
 * prefix. If not specified, there are two properties that will be set
 * to default values:
 * <ul>
 * <li>
 * <code>java.naming.factory.initial</code> with default value
 * <code>com.sun.jndi.ldap.LdapCtxFactory</code></li>
 * <li>
 * <code>java.naming.provider.url</code> with default value
 * <code>ldap://localhost:389</code></li>
 * </ul>
 * Besides, the following properties can or must also be specified:
 * <ul>
 * <li>
 * <code>latch.userRepositoryFactory.&lt;factoryName&gt;.bind.dn</code>
 * (optional, if not set anonymous searches will be done).</li>
 * <li>
 * <code>latch.userRepositoryFactory.&lt;factoryName&gt;.bind.password</code>
 * (optional, if not set anonymous searches will be done).</li>
 * <li>
 * <code>latch.userRepositoryFactory.&lt;factoryName&gt;.search.attribute</code>
 * (required).</li>
 * <li>
 * <code>latch.userRepositoryFactory.&lt;factoryName&gt;.search.base</code>
 * (required).</li>
 * <li>
 * <code>latch.userRepositoryFactory.&lt;factoryName&gt;.search.count.limit</code>
 * (optional, if not set no count limit will be used).</li>
 * <li>
 * <code>latch.userRepositoryFactory.&lt;factoryName&gt;.search.filter</code>
 * (required).</li>
 * <li>
 * <code>latch.userRepositoryFactory.&lt;factoryName&gt;.search.scope</code>
 * (optional, if not set SUBTREE scope will be used).</li>
 * <li>
 * <code>latch.userRepositoryFactory.&lt;factoryName&gt;.search.time.limit</code>
 * (optional, if not set no time limit will be used).</li>
 * </ul>
 * The strings <code>@@@USER@@@</code> and <code>@@@CONTEXT@@@</code>
 * will be replaced by the actual user and context.
 */
public class LatchLDAPUserRepositoryFactory extends LatchUserRepositoryFactory {

    /** Static SLF4J logger */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LatchLDAPUserRepositoryFactory.class);

    @Override
    public LatchUserRepository newLatchUserRepository() {

        LOG.debug("Instantiating user repository...");

        /* Instantiate the LDAP User Repository */

        LatchLDAPUserRepository rv = new LatchLDAPUserRepository();

        /* Check all properties and copy the appropriate ones to the environment */

        Hashtable<String, String> environment = new Hashtable<String, String>();
        Iterator<String> iterator = getProperties().stringPropertyNames().iterator();

        LOG.debug("Iterating through properties...");

        while (iterator.hasNext()) {

            String key = iterator.next();

            LOG.debug("Iterating through properties (" + key + ")...");

            if (key.startsWith("latch.userRepositoryFactory." + getName() + ".java.naming.") || key.startsWith("latch.userRepositoryFactory." + getName() + ".com.sun.jndi.ldap.")) {
                environment.put(key.substring(("latch.userRepositoryFactory." + getName()).length() + 1), getProperties().getProperty(key));
            }

        }

        LOG.debug("Finished iterating through properties...");

        /* Check if basic JNDI properties have been specified. If not, set to default value */

        if (environment.get("java.naming.factory.initial") == null) {
            environment.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        }

        if (environment.get("java.naming.provider.url") == null) {
            environment.put("java.naming.provider.url", "ldap://localhost:389");
        }

        /* If a bind DN and password have been set, set the appropriate properties in the environment */

        if (getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".bind.dn") != null && !getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".bind.dn").isEmpty()) {
            if (getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".bind.password") != null && !getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".bind.password").isEmpty()) {
                environment.put("java.naming.security.authentication", "simple");
                environment.put("java.naming.security.principal", getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".bind.dn"));
                environment.put("java.naming.security.credentials", getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".bind.password"));
            }
        }

        /* Set the environment that will use the LDAP user repository instance */

        rv.setEnvironment(environment);

        /* Check if a user attribute has been specified. If it has, set the appropriate field in the LDAP user repository */

        if (getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".userAttribute") != null && !getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".userAttribute").isEmpty()) {
            rv.setUserAttribute(getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".userAttribute"));
        }

        /* Check if a search base DN has been specified. If it has, set the appropriate field in the LDAP user repository */

        if (getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".search.base") != null && !getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".search.base").isEmpty()) {
            rv.setSearchBaseDN(getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".search.base"));
        }

        /* Check if a search count limit has been specified. If it has, set the appropriate field in the LDAP user repository */

        if (getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".search.count.limit") != null && !getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".search.count.limit").isEmpty()) {
            rv.setSearchCountLimit(Integer.parseInt(getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".search.count.limit")));
        }

        /* Check if a search filter has been specified. If it has, set the appropriate field in the LDAP user repository */

        if (getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".search.filter") != null && !getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".search.filter").isEmpty()) {
            rv.setSearchFilter(getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".search.filter"));
        }

        /* Check if a search scope has been specified. If it has, set the appropriate field in the LDAP user repository */

        if (getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".search.scope") != null && !getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".search.scope").isEmpty()) {
            rv.setSearchCountLimit(Integer.parseInt(getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".search.scope")));
        }

        /* Check if a search time limit has been specified. If it has, set the appropriate field in the LDAP user repository */

        if (getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".search.time.limit") != null && !getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".search.time.limit").isEmpty()) {
            rv.setSearchCountLimit(Integer.parseInt(getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".search.time.limit")));
        }

        LOG.debug("Returning...");

        return rv;

    }

}