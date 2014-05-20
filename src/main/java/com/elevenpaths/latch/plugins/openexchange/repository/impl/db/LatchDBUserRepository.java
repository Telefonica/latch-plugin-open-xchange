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

package com.elevenpaths.latch.plugins.openexchange.repository.impl.db;

import com.elevenpaths.latch.plugins.openexchange.osgi.LatchServiceRegistry;
import com.elevenpaths.latch.plugins.openexchange.repository.LatchUserRepository;
import com.elevenpaths.latch.plugins.openexchange.util.Utils;
import com.openexchange.context.ContextService;
import com.openexchange.exception.OXException;
import com.openexchange.groupware.contexts.Context;
import com.openexchange.osgi.ServiceRegistry;
import com.openexchange.user.UserService;

/**
 * Implementation that uses the Open-Xchange local database to get the
 * accountId associated with a user. It requires that the accountId is
 * stored as a public user attribute whose name can be specified
 * through the factory property:
 * <p>
 * <code>latch.userRepositoryFactory.&lt;factoryName&gt;.userAttribute</code>
 * </p>
 * If not, the default value <code>latchAccountId</code> will be used.
 */
public class LatchDBUserRepository extends LatchUserRepository {

    /** Static SLF4J logger */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LatchDBUserRepository.class);

    /** Static service registry */
    private static ServiceRegistry registry = LatchServiceRegistry.getServiceRegistry();

    /** Attribute name that stores the accountId */
    private String attributeName = null;

    @Override
    public String getLatchAccountId(String loginInfo) throws OXException {

        LOG.debug("Trying to get latchAccountId...");

        Utils.checkMethodRequiredStringParameterNotEmpty(LOG, "loginInfo", loginInfo);

        String rv = null;

        /* Get a reference to the context service */

        ContextService contextService = registry.getService(ContextService.class);

        /* Get a reference to the user service */

        UserService userService = registry.getService(UserService.class);

        if (contextService != null && userService != null) {

            /* loginInfo username should be in the form context@user. If not, throw an Exception */

            String[] splitted = split(loginInfo);

            if (splitted.length != 2) {
                LOG.error("Unexpected loginInfo (" + loginInfo + ")...");
                throw OXException.general("Unexpected loginInfo (" + loginInfo + ")");
            }

            LOG.debug("loginInfo: splitted[0] -> " + splitted[0]);
            LOG.debug("loginInfo: splitted[1] -> " + splitted[1]);

            Context context = contextService.getContext(contextService.getContextId(splitted[0]));

            int userId = userService.getUserId(splitted[1], context);

            rv = userService.getUserAttribute(attributeName, userId, context);

            LOG.debug("latchAccountId: " + rv);

        }

        return rv;

    }

    @Override
    public void setLatchAccountId(String loginInfo, String latchAccountId) throws OXException {

        LOG.debug("Trying to set latchAccountId...");

        Utils.checkMethodRequiredStringParameterNotEmpty(LOG, "loginInfo", loginInfo);

        /* Get a reference to the context service */

        ContextService contextService = registry.getService(ContextService.class);

        /* Get a reference to the user service */

        UserService userService = registry.getService(UserService.class);

        if (contextService != null && userService != null) {

            /* loginInfo username should be in the form context@user. If not, throw an Exception */

            String[] splitted = split(loginInfo);

            if (splitted.length != 2) {
                LOG.error("Unexpected loginInfo (" + loginInfo + ")...");
                throw OXException.general("Unexpected loginInfo (" + loginInfo + ")");
            }

            LOG.debug("loginInfo: splitted[0] -> " + splitted[0]);
            LOG.debug("loginInfo: splitted[1] -> " + splitted[1]);

            Context context = contextService.getContext(contextService.getContextId(splitted[0]));

            int userId = userService.getUserId(splitted[1], context);

            userService.setUserAttribute(attributeName, latchAccountId, userId, context);

        }

    }

    protected String getAttributeName() {
        return attributeName;
    }

    protected void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

}