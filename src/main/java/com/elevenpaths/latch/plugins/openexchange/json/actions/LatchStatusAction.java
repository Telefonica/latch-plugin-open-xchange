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

import org.json.JSONException;
import org.json.JSONObject;

import com.elevenpaths.latch.plugins.openexchange.config.LatchConfig;
import com.openexchange.ajax.requesthandler.AJAXRequestData;
import com.openexchange.ajax.requesthandler.AJAXRequestResult;
import com.openexchange.documentation.RequestMethod;
import com.openexchange.documentation.annotations.Action;
import com.openexchange.documentation.annotations.Parameter;
import com.openexchange.exception.OXException;
import com.openexchange.server.ServiceLookup;
import com.openexchange.tools.servlet.AjaxExceptionCodes;
import com.openexchange.tools.session.ServerSession;

@Action(method = RequestMethod.GET, name = "status", description = "Get the status of an account.", parameters = { @Parameter(name = "session", description = "A session ID previously obtained from the login module.") }, responseDescription = "The status of the account (paired or unpaired).")
public class LatchStatusAction extends LatchAction {

    /** Static SLF4J logger */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LatchStatusAction.class);

    /**
     * Initializes a new LatchStatusAction.
     * 
     * @param services
     *        A service look-up to get references to needed services.
     * @param latchConfig
     *        The Latch configuration.
     */
    public LatchStatusAction(ServiceLookup services, LatchConfig latchConfig) {
        super(services, latchConfig);
    }

    @Override
    public AJAXRequestResult perform(AJAXRequestData ajaxRequestData, ServerSession serverSession) throws OXException {

        try {

            JSONObject rv = new JSONObject();

            if (latchConfig.getUserRepository().getLatchAccountId(serverSession.getLogin()) == null) {
                rv.put("status", "unpaired");
            }
            else {
                rv.put("status", "paired");
            }

            return new AJAXRequestResult(rv);

        }
        catch (JSONException e) {
            LOG.error("Unexpected JSON exception creating response object.", e);
            throw AjaxExceptionCodes.JSON_ERROR.create("Unexpected JSON exception creating response object", e);
        }

    }

}