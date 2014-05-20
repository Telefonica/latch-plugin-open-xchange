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

package com.elevenpaths.latch.plugins.openexchange.json.osgi;

import java.util.Properties;

import com.elevenpaths.latch.plugins.openexchange.config.LatchConfig;
import com.elevenpaths.latch.plugins.openexchange.json.LatchActionFactory;
import com.openexchange.ajax.requesthandler.osgiservice.AJAXModuleActivator;
import com.openexchange.config.ConfigurationService;
import com.openexchange.context.ContextService;
import com.openexchange.exception.OXException;
import com.openexchange.user.UserService;

public class LatchJsonActivator extends AJAXModuleActivator {

    /** Static SLF4J logger */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LatchJsonActivator.class);

    @Override
    protected Class<?>[] getNeededServices() {
        return new Class<?>[] { ConfigurationService.class, ContextService.class, UserService.class };
    }

    @Override
    protected void startBundle() throws Exception {

        LOG.debug("Starting bundle...");

        /* Get a reference to the Configuration service */

        ConfigurationService config = getService(ConfigurationService.class);

        /* Get a reference to the bundle properties */

        Properties properties = config.getFile("latch.properties");

        /* If the file doesn't exist throw an exception */

        if (properties == null) {
            LOG.error("File latch.properties not found in expected location.");
            throw OXException.general("File latch.properties not found in expected location");
        }

        /* Initialize the Latch configuration */

        LatchConfig latchConfig = new LatchConfig(properties);

        /* Register the module */

        registerModule(new LatchActionFactory(this, latchConfig), "latch");

    }

}
