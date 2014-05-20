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

import javax.naming.AuthenticationException;
import javax.naming.AuthenticationNotSupportedException;
import javax.naming.CommunicationException;
import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.InvalidSearchFilterException;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.elevenpaths.latch.plugins.openexchange.repository.LatchUserRepository;
import com.elevenpaths.latch.plugins.openexchange.util.Utils;
import com.openexchange.exception.OXException;

/**
 * Implementation that searches if a user has a paired latch account
 * in an LDAP directory. If the search returns more than one result,
 * the first one will be used.
 * <p>
 * If the specified search attribute is multivalued one of its values
 * will be returned but with no particular order.
 * 
 * TODO: StartTLS TODO: SASL Authentication TODO: LDAPS Hostname
 * Verification TODO: Certificate Pinning
 */
public class LatchLDAPUserRepository extends LatchUserRepository {

    /** Static SLF4J logger */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LatchLDAPUserRepository.class);

    /** Environment for JNDI */
    private Hashtable<String, String> environment = null;

    /** LDAP Attribute used to store the account id */
    private String userAttribute = null;

    /** LDAP Search Base DN */
    private String searchBaseDN = null;

    /** LDAP Search Count Limit */
    private int searchCountLimit = 0;

    /** LDAP Search Filter */
    private String searchFilter = null;

    /** LDAP Search Scope */
    private int searchScope = SearchControls.SUBTREE_SCOPE;

    /** LDAP Search Time Limit */
    private int searchTimeLimit = 0;

    @Override
    public String getLatchAccountId(String loginInfo) throws OXException {

        LOG.debug("Trying to get latchAccountId...");

        Utils.checkMethodRequiredStringParameterNotEmpty(LOG, "loginInfo", loginInfo);

        String rv = null;

        /* loginInfo username is in the form contextId@userId. If not, throw an Exception */

        String[] splitted = split(loginInfo);

        if (splitted.length != 2) {
            LOG.error("Unexpected loginInfo (" + loginInfo + ")...");
            throw OXException.general("Unexpected loginInfo (" + loginInfo + ")");
        }

        LOG.debug("loginInfo: splitted[0] -> " + splitted[0]);
        LOG.debug("loginInfo: splitted[1] -> " + splitted[1]);

        SearchResult sr = readEntry(splitted[1], splitted[0]);

        if (sr != null) {

            /* Check if the specified attribute has values */

            if (sr.getAttributes().get(userAttribute) != null) {

                LOG.debug("The first answer has the attribute...");

                /* Get one of the values */

                try {
                    rv = (String) sr.getAttributes().get(userAttribute).get();
                }
                catch (NamingException e) {
                    LOG.error("Unable to get the attribute value from the entry.", e);
                    throw OXException.general("Unable to get the attribute value from the entry", e);
                }

            }
            else {

                /* The specified attribute is not present. Not paired of configuration error? */

                LOG.debug("The returned entry hasn't got the attribute " + userAttribute + ". User not paired or configuration error?");

            }

        }
        else {

            /* Log that there hasn't been results. Not paired or configuration error? */

            LOG.debug("No entries returned. User not paired or configuration error?");

        }

        LOG.debug("Returning latchAccountId " + rv + "...");

        return rv;

    }

    @Override
    public void setLatchAccountId(String loginInfo, String latchAccountId) throws OXException {

        LOG.debug("Trying to set latchAccountId...");

        Utils.checkMethodRequiredStringParameterNotEmpty(LOG, "loginInfo", loginInfo);

        if (userAttribute == null || userAttribute.isEmpty()) {
            LOG.error("No userAttribute configured.");
            throw OXException.general("No userAttribute configured");
        }

        /* loginInfo username is in the form contextId@userId. If not, throw an Exception */

        String[] splitted = split(loginInfo);

        if (splitted.length != 2) {
            LOG.error("Unexpected loginInfo (" + loginInfo + ")...");
            throw OXException.general("Unexpected loginInfo (" + loginInfo + ")");
        }

        LOG.debug("loginInfo: splitted[0] -> " + splitted[0]);
        LOG.debug("loginInfo: splitted[1] -> " + splitted[1]);

        SearchResult sr = readEntry(splitted[1], splitted[0]);

        if (sr != null) {

            if (latchAccountId == null) {

                Attribute attribute = new BasicAttribute(userAttribute);
                ModificationItem[] item = new ModificationItem[] { new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attribute) };

                modifyEntryAttributes(sr.getNameInNamespace(), item);

            }
            else {

                Attribute attribute = new BasicAttribute(userAttribute, latchAccountId);
                ModificationItem[] item = new ModificationItem[] { new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attribute) };

                modifyEntryAttributes(sr.getNameInNamespace(), item);

            }

        }
        else {
            LOG.error("Entry not found.");
            throw OXException.general("Entry not found");
        }

    }

    /**
     * Gets the environment used to instantiate the JNDI
     * <code>DirContext</code>.
     * 
     * @return The environment used to instantiate the JDNI
     *         <code>DirContext</code>
     */
    protected Hashtable<String, String> getEnvironment() {
        return environment;
    }

    /**
     * Gets the attribute where is stored the paired account id.
     * 
     * @return The attribute where is stored the paired account id.
     */
    protected String getUserAttribute() {
        return userAttribute;
    }

    /**
     * Gets the base DN that will be used in the search operations.
     * 
     * @return The base DN that will be used in the search operations.
     */
    protected String getSearchBaseDN() {
        return searchBaseDN;
    }

    /**
     * Gets the maximum number of answers that a search will produce.
     * 
     * @return The maximum number of answers that a search will
     *         produce.
     */
    protected int getSearchCountLimit() {
        return searchCountLimit;
    }

    /**
     * Gets the search filter that will be used in the search
     * operations.
     * 
     * @return The search filter that will be used in the search
     *         operations.
     */
    protected String getSearchFilter() {
        return searchFilter;
    }

    /**
     * Gets the scope that will be used in the search operations.
     * 
     * @return The scope that will be used in the search operations.
     */
    protected int getSearchScope() {
        return searchScope;
    }

    /**
     * Gets the maximum time to wait for a search operation.
     * 
     * @return The maximum time to wait for a search operation.
     */
    protected int getSearchTimeLimit() {
        return searchTimeLimit;
    }

    /**
     * Sets the environment used to instantiate the JNDI
     * <code>DirContext</code>.
     * 
     * @param environment
     *        The environment used to instantiate the JNDI
     *        <code>DirContext</code>
     */
    protected void setEnvironment(Hashtable<String, String> environment) {
        this.environment = environment;
    }

    /**
     * Sets the attribute where the paired latch account id is stored.
     * 
     * @param searchAttribute
     *        The attribute where the paired latch account id is
     *        stored.
     */
    protected void setUserAttribute(String userAttribute) {
        this.userAttribute = userAttribute;
    }

    /**
     * Sets the base DN that will be used in the search operations.
     * 
     * @param searchBaseDN
     *        The base DN that will be used in the search operation.
     */
    protected void setSearchBaseDN(String searchBaseDN) {
        this.searchBaseDN = searchBaseDN;
    }

    /**
     * Sets the maximum number of answers that a search will produce.
     * 
     * @param searchCountLimit
     *        The maximum number of answers that a search will
     *        produce.
     */
    protected void setSearchCountLimit(int searchCountLimit) {
        this.searchCountLimit = searchCountLimit;
    }

    /**
     * Sets the filter that will be used in the search operations.
     * 
     * @param searchFilter
     *        The filter that will be used in the search operations.
     */
    protected void setSearchFilter(String searchFilter) {
        this.searchFilter = searchFilter;
    }

    /**
     * Sets the scope that will be used in the search operations.
     * 
     * @param searchScope
     *        The scope that will be used in the search operations.
     */
    protected void setSearchScope(int searchScope) {
        this.searchScope = searchScope;
    }

    /**
     * Sets the maximum time to wait for a search operation.
     * 
     * @param searchTimeLimit
     *        The maximum time to wait for a search operation.
     */
    protected void setSearchTimeLimit(int searchTimeLimit) {
        this.searchTimeLimit = searchTimeLimit;
    }

    private DirContext getDirContext() throws OXException {

        try {

            return new InitialDirContext(getEnvironment());

        }
        catch (CommunicationException e) {
            LOG.error("Unable to instantiate DirContext. Please review communication details.", e);
            throw OXException.general("Unable to instantiate DirContext. Please review communication details", e);
        }
        catch (InvalidNameException e) {
            LOG.error("Unable to instantiate DirContext. Please review bind DN.", e);
            throw OXException.general("Unable to instantiate DirContext. Please review bind DN", e);
        }
        catch (AuthenticationException e) {
            LOG.error("Unable to instantiate DirContext. Please review bind DN and bind password.", e);
            throw OXException.general("Unable to instantiate DirContext. Please review bind DN and bind password", e);
        }
        catch (NamingException e) {
            LOG.error("Unable to instantiate DirContext.", e);
            throw OXException.general("Unable to instantiate DirContext", e);
        }

    }

    private void modifyEntryAttributes(String entryDN, ModificationItem[] item) throws OXException {

        DirContext ctx = getDirContext();

        try {

            ctx.modifyAttributes(entryDN, item);

        }
        catch (CommunicationException e) {
            LOG.error("Unable to modify entry's attributes. Please review communication details.", e);
            throw OXException.general("Unable to get latchAccountId from LDAP directory. Please review communication details", e);
        }
        catch (AuthenticationNotSupportedException e) {
            LOG.error("Unable to modify entry's attributes (authentication not supported). Please review bind DN and bind password.", e);
            throw OXException.general("Unable to modify entry's attributes (authentication not supported). Please review bind DN and bind password", e);
        }
        catch (NoInitialContextException e) {
            LOG.error("Unable to modify entry's attributes (insufficient access rights). Please review the configuration.", e);
            throw OXException.general("Unable to modify entry's attributes (insufficient access rights). Please review the configuration", e);
        }
        catch (NamingException e) {
            LOG.error("Unable to modify entry's attributes.", e);
            throw OXException.general("Unable to modify entry's attributes", e);
        }
        finally {

            try {
                ctx.close();
            }
            catch (NamingException e) {
            }

        }

    }

    private SearchResult readEntry(String username, String context) throws OXException {

        if (userAttribute == null || userAttribute.isEmpty()) {
            LOG.error("No searchAttribute configured.");
            throw OXException.general("No searchAttribute configured");
        }

        if (searchBaseDN == null || searchBaseDN.isEmpty()) {
            LOG.error("No searchBaseDN configured.");
            throw OXException.general("No searchBaseDN configured");
        }

        if (searchFilter == null || searchFilter.isEmpty()) {
            LOG.error("No searchFilter configured.");
            throw OXException.general("No searchFilter configured");
        }

        DirContext ctx = getDirContext();
        SearchResult rv = null;

        try {

            SearchControls ldapSearchControls = new SearchControls();

            ldapSearchControls.setReturningAttributes(new String[] { userAttribute });
            ldapSearchControls.setSearchScope(searchScope);
            ldapSearchControls.setCountLimit(searchCountLimit);
            ldapSearchControls.setTimeLimit(searchTimeLimit);
            ldapSearchControls.setReturningObjFlag(false);

            /* Perform the search operation */

            String finalSearchDN = searchBaseDN.replace("@@@CONTEXT@@@", context).replace("@@@USER@@@", username);
            String finalSearchFilter = searchFilter.replace("@@@CONTEXT@@@", escapeLDAPSearchFilterComponent(context)).replace("@@@USER@@@", escapeLDAPSearchFilterComponent(username));

            LOG.debug("Searching LDAP with search base DN " + finalSearchDN);
            LOG.debug("Searching LDAP with search filter " + finalSearchFilter);

            NamingEnumeration<SearchResult> answer = ctx.search(finalSearchDN, finalSearchFilter, ldapSearchControls);

            /* Check if it has returned results */

            if (answer.hasMore()) {

                LOG.debug("There has been answers...");

                /* Get the first one */

                rv = (SearchResult) answer.next();

            }

        }
        catch (CommunicationException e) {
            LOG.error("Unable to read an entry from LDAP directory. Please review communication details.", e);
            throw OXException.general("Unable to read an entry from LDAP directory. Please review communication details", e);
        }
        catch (InvalidSearchFilterException e) {
            LOG.error("Unable to read an entry from LDAP directory. Please review search filter.", e);
            throw OXException.general("Unable to read an entry from LDAP directory. Please review search filter", e);
        }
        catch (NameNotFoundException e) {
            LOG.error("Unable to read an entry from LDAP directory. Please review search base DN.", e);
            throw OXException.general("Unable to read an entry from LDAP directory. Please review search base DN", e);
        }
        catch (NamingException e) {
            LOG.error("Unable to read an entry from LDAP directory.", e);
            throw OXException.general("Unable to read an entry from LDAP directory", e);
        }
        finally {

            try {
                ctx.close();
            }
            catch (NamingException e) {
            }

        }

        return rv;

    }

    /**
     * Escape a DN component to prevent injection attacks.
     * 
     * @param name
     *        The DN component.
     * 
     * @return The modified DN component.
     */
    @SuppressWarnings("unused")
    private static String escapeLDAPDNComponent(String dnComponent) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < dnComponent.length(); i++) {

            char curChar = dnComponent.charAt(i);

            switch (curChar) {
                case ',':
                    sb.append("\\,");
                    break;
                case '=':
                    sb.append("\\=");
                    break;
                case '+':
                    sb.append("\\+");
                    break;
                case '<':
                    sb.append("\\<");
                    break;
                case '>':
                    sb.append("\\>");
                    break;
                case '#':
                    sb.append("\\#");
                    break;
                case ';':
                    sb.append("\\;");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                default:
                    sb.append(curChar);
            }

        }

        return sb.toString();

    }

    /**
     * Escape a search filter component to prevent injection attacks.
     * 
     * @param filterComponent
     *        The search filter component.
     * 
     * @return The modified search filter component.
     */
    private static final String escapeLDAPSearchFilterComponent(String filterComponent) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < filterComponent.length(); i++) {

            char curChar = filterComponent.charAt(i);

            switch (curChar) {
                case ')':
                    sb.append("\\28");
                    break;
                case '(':
                    sb.append("\\29");
                    break;
                case '*':
                    sb.append("\\2a");
                    break;
                case '\\':
                    sb.append("\\5c");
                    break;
                case '/':
                    sb.append("\\2f");
                    break;
                default:
                    sb.append(curChar);
            }

        }

        return sb.toString();

    }

}