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

import com.elevenpaths.latch.LatchResponse;
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

@Action(method = RequestMethod.PUT, name = "pair", description = "Pair an account.", parameters = { @Parameter(name = "session", description = "A session ID previously obtained from the login module."), @Parameter(name = "token", description = "The pairing token.") }, responseDescription = "The boolean value \"true\" if PUT was successful; otherwise \"false\".")
public class LatchPairAction extends LatchAction {

    /** Static SLF4J logger */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LatchPairAction.class);

    /**
     * Initializes a new LatchPairAction.
     * 
     * @param services
     *        A service look-up to get references to needed services.
     * @param latchConfig
     *        The Latch configuration.
     */
    public LatchPairAction(ServiceLookup services, LatchConfig latchConfig) {
        super(services, latchConfig);
    }

    @Override
    public AJAXRequestResult perform(AJAXRequestData ajaxRequestData, ServerSession serverSession) throws OXException {

        String token = ajaxRequestData.getParameter("token");

        if (token == null) {
            LOG.error("The pairing token can't be null.");
            throw AjaxExceptionCodes.JSON_ERROR.create("The pairing token can't be null");
        }

        if (token.isEmpty()) {
            LOG.error("The pairing token can't be empty.");
            throw AjaxExceptionCodes.JSON_ERROR.create("The pairing token can't be empty");
        }

        AJAXRequestResult rv = null;
        LatchResponse response = null;

        try {

            /* Pair the account with the provided token */

            response = latchConfig.getLatchSDK().pair(token);

        }
        catch (Exception e) {
            LOG.error("An exception has been thrown while communicating with the Latch backend", e);
            throw AjaxExceptionCodes.JSON_ERROR.create("An exception has been thrown while communicating with the Latch backend", e);
        }

        if (response == null) {
            LOG.error("No response from the Latch backend.");
            throw AjaxExceptionCodes.JSON_ERROR.create("No response from the Latch backend");
        }

        if (response.getData() != null && response.getData().has("accountId")) {

            /* Store the accountId in the user repository */

            latchConfig.getUserRepository().setLatchAccountId(serverSession.getLogin(), response.getData().get("accountId").getAsString());

            rv = new AJAXRequestResult(Boolean.TRUE);

        }
        else {

            if (response.getError() != null) {
                LOG.error("The Latch backend returned an error: " + response.getError().toString());
                throw AjaxExceptionCodes.JSON_ERROR.create("The Latch backend returned an error: " + response.getError().toString());
            }
            else {
                LOG.error("Unexpected response from the Latch backend.");
                throw AjaxExceptionCodes.JSON_ERROR.create("Unexepcted response from the Latch backend");
            }

        }

        return rv;

    }

}
