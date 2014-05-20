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

package com.elevenpaths.latch.plugins.openexchange.json.actions;

import com.elevenpaths.latch.plugins.openexchange.config.LatchConfig;
import com.elevenpaths.latch.plugins.openexchange.util.Utils;
import com.openexchange.ajax.requesthandler.AJAXActionService;
import com.openexchange.server.ServiceLookup;

public abstract class LatchAction implements AJAXActionService {

    /** Static SLF4J logger */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LatchAction.class);

    /** A service look-up to get references to needed services */
    private final ServiceLookup services;

    /** Latch configuration */
    protected LatchConfig latchConfig = null;

    /**
     * Initializes a new abstract LatchAction.
     * 
     * @param services
     *        A service look-up to get references to needed services.
     * @param latchConfig
     *        The Latch configuration.
     */
    protected LatchAction(final ServiceLookup services, LatchConfig latchConfig) {

        super();

        Utils.checkMethodRequiredParameter(LOG, "services", services);
        Utils.checkMethodRequiredParameter(LOG, "latchConfig", latchConfig);

        this.services = services;
        this.latchConfig = latchConfig;

    }

    /**
     * Gets the service of specified type.
     * 
     * @param clazz
     *        The service's class
     * 
     * @return The service or <code>null</code> if absent.
     */
    protected <S> S getService(final Class<? extends S> clazz) {
        return services.getService(clazz);
    }

}