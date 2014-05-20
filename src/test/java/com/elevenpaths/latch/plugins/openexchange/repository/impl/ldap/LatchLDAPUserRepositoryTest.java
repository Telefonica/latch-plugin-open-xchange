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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.util.Properties;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.elevenpaths.latch.plugins.openexchange.repository.LatchUserRepository;
import com.elevenpaths.latch.plugins.openexchange.repository.LatchUserRepositoryFactory;
import com.openexchange.authentication.LoginInfo;
import com.openexchange.exception.OXException;

public class LatchLDAPUserRepositoryTest {

    /**
     * Test method that gets the accountId of a user that has his
     * latch paired from an LDAP repository.
     */
    @Test
    @Parameters({ "ldapURL", "ldapBindDN", "ldapBindPassword", "ldapSearchBaseDN", "ldapSearchFilter", "userAttribute", "username", "accountId" })
    public void getLatchAccountId(String ldapURL, @Optional String ldapBindDN, @Optional String ldapBindPassword, String ldapSearchBaseDN, String ldapSearchFilter, String userAttribute, String username, String accountId) {

        /* Prepare the parameters to instantiate the user repository factory */

        String factoryName = "ldap";
        Properties factoryProperties = new Properties();
        String factoryClassName = LatchLDAPUserRepositoryFactory.class.getName();
        ClassLoader factoryClassLoader = Thread.currentThread().getContextClassLoader();

        factoryProperties.setProperty("latch.userRepositoryFactory.ldap.java.naming.provider.url", ldapURL);
        factoryProperties.setProperty("latch.userRepositoryFactory.ldap.search.base", ldapSearchBaseDN);
        factoryProperties.setProperty("latch.userRepositoryFactory.ldap.search.filter", ldapSearchFilter);
        factoryProperties.setProperty("latch.userRepositoryFactory.ldap.userAttribute", userAttribute);

        if (ldapBindDN != null && ldapBindPassword != null) {
            factoryProperties.setProperty("latch.userRepositoryFactory.ldap.bind.dn", ldapBindDN);
            factoryProperties.setProperty("latch.userRepositoryFactory.ldap.bind.password", ldapBindPassword);
        }

        try {

            /* Instantiate the user repository factory and the user repository */

            LatchUserRepositoryFactory latchUserRepositoryFactory = LatchUserRepositoryFactory.newLatchUserRepositoryFactory(factoryName, factoryProperties, factoryClassName, factoryClassLoader);
            LatchUserRepository latchUserRepository = latchUserRepositoryFactory.newLatchUserRepository();

            /* Mock the loginInfo parameter needed by the getLatchAccountId() method */

            LoginInfo loginInfo = mock(LoginInfo.class);
            when(loginInfo.getUsername()).thenReturn(username);

            String latchAccountId = latchUserRepository.getLatchAccountId(loginInfo);

            assertEquals(accountId, latchAccountId);

        }
        catch (ClassNotFoundException e) {
            fail("Unexpected Exception (" + e.getClass().getName() + ")");
        }
        catch (IllegalAccessException e) {
            fail("Unexpected Exception (" + e.getClass().getName() + ")");
        }
        catch (InstantiationException e) {
            fail("Unexpected Exception (" + e.getClass().getName() + ")");
        }
        catch (OXException e) {
            fail("Unexpected Exception (" + e.getClass().getName() + ")");
        }

    }

    /**
     * Test method that removes the accountId of a user from an LDAP
     * repository.
     */
    @Test
    @Parameters({ "ldapURL", "ldapBindDN", "ldapBindPassword", "ldapSearchBaseDN", "ldapSearchFilter", "userAttribute", "username", "accountId" })
    public void setLatchAccountId(String ldapURL, @Optional String ldapBindDN, @Optional String ldapBindPassword, String ldapSearchBaseDN, String ldapSearchFilter, String userAttribute, String username, String accountId) {

        /* Prepare the parameters to instantiate the user repository factory */

        String factoryName = "ldap";
        Properties factoryProperties = new Properties();
        String factoryClassName = LatchLDAPUserRepositoryFactory.class.getName();
        ClassLoader factoryClassLoader = Thread.currentThread().getContextClassLoader();

        factoryProperties.setProperty("latch.userRepositoryFactory.ldap.java.naming.provider.url", ldapURL);
        factoryProperties.setProperty("latch.userRepositoryFactory.ldap.search.base", ldapSearchBaseDN);
        factoryProperties.setProperty("latch.userRepositoryFactory.ldap.search.filter", ldapSearchFilter);
        factoryProperties.setProperty("latch.userRepositoryFactory.ldap.userAttribute", userAttribute);

        if (ldapBindDN != null && ldapBindPassword != null) {
            factoryProperties.setProperty("latch.userRepositoryFactory.ldap.bind.dn", ldapBindDN);
            factoryProperties.setProperty("latch.userRepositoryFactory.ldap.bind.password", ldapBindPassword);
        }

        try {

            /* Instantiate the user repository factory and the user repository */

            LatchUserRepositoryFactory latchUserRepositoryFactory = LatchUserRepositoryFactory.newLatchUserRepositoryFactory(factoryName, factoryProperties, factoryClassName, factoryClassLoader);
            LatchUserRepository latchUserRepository = latchUserRepositoryFactory.newLatchUserRepository();

            /* Mock the loginInfo parameter needed by the getLatchAccountId() method */

            LoginInfo loginInfo = mock(LoginInfo.class);
            when(loginInfo.getUsername()).thenReturn(username);

            String latchAccountId = latchUserRepository.getLatchAccountId(loginInfo);

            assertEquals(accountId, latchAccountId);

            /* Try to remove the attribute */

            latchUserRepository.setLatchAccountId(loginInfo, null);

            /* Check if the attribute has been removed */

            assertNull(latchUserRepository.getLatchAccountId(loginInfo));

            /* Try to replace the attribute */

            latchUserRepository.setLatchAccountId(loginInfo, accountId);

            /* Check if the attribute has been replaced */

            assertEquals(accountId, latchUserRepository.getLatchAccountId(loginInfo));

        }
        catch (ClassNotFoundException e) {
            fail("Unexpected Exception (" + e.getClass().getName() + ")");
        }
        catch (IllegalAccessException e) {
            fail("Unexpected Exception (" + e.getClass().getName() + ")");
        }
        catch (InstantiationException e) {
            fail("Unexpected Exception (" + e.getClass().getName() + ")");
        }
        catch (OXException e) {
            fail("Unexpected Exception (" + e.getClass().getName() + ")");
        }

    }

}
