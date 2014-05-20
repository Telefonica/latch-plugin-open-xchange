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

import com.elevenpaths.latch.plugins.openexchange.util.Utils;
import com.openexchange.authentication.LoginInfo;
import com.openexchange.exception.OXException;

/**
 * Models a user repository. The user repository must provide for each
 * user that has paired his account the accountId assigned in the
 * pairing process.
 */
public abstract class LatchUserRepository {

    /** Static SLF4J logger */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LatchUserRepository.class);

    /**
     * Default constructor.
     */
    protected LatchUserRepository() {
        LOG.debug("Constructor...");
    }

    /**
     * Get the accountId associated with a user.
     * 
     * @param loginInfo
     *        The login information.
     * 
     * @return The accountId if paired, null if not.
     * 
     * @throws OXException
     *         If something goes wrong.
     */
    public String getLatchAccountId(LoginInfo loginInfo) throws OXException {

        Utils.checkMethodRequiredParameter(LOG, "loginInfo", loginInfo);

        return getLatchAccountId(loginInfo.getUsername());

    }

    /**
     * Get the accountId associated with a user.
     * 
     * @param loginInfo
     *        The login information in the form user@context
     * 
     * @return The accountId if paired, null if not.
     * 
     * @throws OXException
     *         If something goes wrong.
     */
    public abstract String getLatchAccountId(String loginInfo) throws OXException;

    /**
     * Set the accountId associated with a user.
     * 
     * @param loginInfo
     *        The login information.
     * @param latchAccountId
     *        The accountId associated with the user.
     * 
     * @throws OXException
     *         If something goes wrong.
     */
    public void setLatchAccountId(LoginInfo loginInfo, String latchAccountId) throws OXException {

        Utils.checkMethodRequiredParameter(LOG, "loginInfo", loginInfo);

        setLatchAccountId(loginInfo.getUsername(), latchAccountId);

    }

    /**
     * Set the accountId associated with a user.
     * 
     * @param loginInfo
     *        The login information in the form user@context
     * @param latchAccountId
     *        The accountId associated with the user.
     * 
     * @throws OXException
     *         If something goes wrong.
     */
    public abstract void setLatchAccountId(String loginInfo, String latchAccountId) throws OXException;

    /**
     * Splits user name and context.
     * 
     * @param loginInfo
     *        Combined information separated by an @ sign.
     * 
     * @return A string array with context and user name (in this
     *         order).
     */
    protected String[] split(String loginInfo) {
        return split(loginInfo, '@');
    }

    /**
     * Splits user name and context.
     * 
     * @param loginInfo
     *        Combined information separated by an @ sign.
     * @param separator
     *        Character for splitting user name and context.
     * 
     * @return A string array with context and user name (in this
     *         order).
     */
    protected String[] split(String loginInfo, char separator) {

        Utils.checkMethodRequiredParameter(LOG, "loginInfo", loginInfo);

        String[] rv = new String[2];

        int pos = loginInfo.lastIndexOf(separator);

        if (pos == -1) {
            rv[0] = "defaultcontext";
            rv[1] = loginInfo;
        }
        else {
            rv[0] = loginInfo.substring(pos + 1);
            rv[1] = loginInfo.substring(0, pos);
        }

        return rv;

    }

}