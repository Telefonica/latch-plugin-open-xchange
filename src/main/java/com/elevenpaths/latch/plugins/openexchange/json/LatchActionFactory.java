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

package com.elevenpaths.latch.plugins.openexchange.json;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.elevenpaths.latch.plugins.openexchange.config.LatchConfig;
import com.elevenpaths.latch.plugins.openexchange.json.actions.LatchAction;
import com.elevenpaths.latch.plugins.openexchange.json.actions.LatchPairAction;
import com.elevenpaths.latch.plugins.openexchange.json.actions.LatchStatusAction;
import com.elevenpaths.latch.plugins.openexchange.json.actions.LatchUnpairAction;
import com.elevenpaths.latch.plugins.openexchange.util.Utils;
import com.openexchange.ajax.requesthandler.AJAXActionService;
import com.openexchange.ajax.requesthandler.AJAXActionServiceFactory;
import com.openexchange.documentation.annotations.Module;
import com.openexchange.exception.OXException;
import com.openexchange.server.ServiceLookup;

@Module(name = "latch", description = "Provides access to Latch account operations (pair, status and unpair).")
public class LatchActionFactory implements AJAXActionServiceFactory {

    /** Static SLF4J logger */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LatchActionFactory.class);

    /** The actions */
    private final Map<String, LatchAction> actions = new ConcurrentHashMap<String, LatchAction>(1);

    /**
     * Initializes a new {@link TaskActionFactory}.
     * 
     * @param services
     *        A service look-up to get references to needed services.
     * @param latchConfig
     *        The Latch configuration.
     */
    public LatchActionFactory(final ServiceLookup serviceLookup, LatchConfig latchConfig) {

        super();

        Utils.checkMethodRequiredParameter(LOG, "serviceLookup", serviceLookup);
        Utils.checkMethodRequiredParameter(LOG, "latchConfig", latchConfig);

        actions.put("pair", new LatchPairAction(serviceLookup, latchConfig));
        actions.put("status", new LatchStatusAction(serviceLookup, latchConfig));
        actions.put("unpair", new LatchUnpairAction(serviceLookup, latchConfig));

    }

    @Override
    public AJAXActionService createActionService(final String action) throws OXException {
        return actions.get(action);
    }

    @Override
    public Collection<? extends AJAXActionService> getSupportedServices() {
        return java.util.Collections.unmodifiableCollection(actions.values());
    }

}