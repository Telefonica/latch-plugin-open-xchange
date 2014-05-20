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

import com.elevenpaths.latch.plugins.openexchange.repository.LatchUserRepository;
import com.elevenpaths.latch.plugins.openexchange.repository.LatchUserRepositoryFactory;

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
public class LatchDBUserRepositoryFactory extends LatchUserRepositoryFactory {

    @Override
    public LatchUserRepository newLatchUserRepository() {

        LatchDBUserRepository rv = new LatchDBUserRepository();

        if (getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".userAttribute") != null && !getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".userAttribute").isEmpty()) {
            rv.setAttributeName(getProperties().getProperty("latch.userRepositoryFactory." + getName() + ".userAttribute"));
        }
        else {
            rv.setAttributeName("latchAccountId");
        }

        return rv;

    }

}