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

package com.elevenpaths.latch.sdk.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import com.elevenpaths.latch.Latch;
import com.elevenpaths.latch.LatchErrorException;
import com.elevenpaths.latch.LatchResponse;
import com.elevenpaths.latch.plugins.openexchange.util.Utils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * A Latch SDK implementation.
 * <p>
 * The Java Latch SDK requires that developers choose the best way for
 * them to implement HTTP connections with the Latch backend and
 * overload the <code>HTTP_GET</code> method appropriately.
 * <p>
 * On this way, they can adapt the code to the environment and
 * conditions in which it will run. For example, if the code will run
 * in a container that offers some capabilities to manage HTTP
 * connection pools.
 * <p>
 * In this implementation the standard <code>HttpURLConnection</code>
 * class provided by the JVM will be used. This means that the CA that
 * signs the Latch backend server certificate should be as trusted CA
 * in the JVM trust store.
 * <p>
 * It also means that the standard way to set HTTP proxy host and port
 * (through JVM system properties) can be used if needed. At this
 * moment setting a proxy just for this HTTP connections is not
 * supported in the SDK.
 * <p>
 * Besides, an alternate implementation of the status method is also
 * provided. This implementation, instead of returning the bulk JSON
 * object returns an enumerated type <code>LatchSDKStatus</code>.
 * <p>
 * The enumerated type <code>LatchSDKStatus</code> has an additional
 * <code>UNKNOWN</code> value to represent that something has gone
 * wrong checking the status of the latch so the application using the
 * SDK can choose what to do.
 * 
 * TODO: Certificate Pinning.
 */
public class LatchSDKImpl extends Latch {

    /** Static SLF4J logger */
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LatchSDKImpl.class);

    /**
     * The application id the SDK will use when communicating with the
     * Latch backend. This is needed because the field in the parent
     * class is private.
     */
    protected String applicationId = null;

    /**
     * Constructs a <code>LatchSDKImpl</code> with the provided
     * application id and secret. These values are different for each
     * application and can be obtained from the developer area of the
     * Latch web site.
     * 
     * @param applicationId
     *        The application id that will be used when communicating
     *        with the Latch backend.
     * @param secret
     *        The secret that will be used when communicating with the
     *        Latch backend.
     */
    public LatchSDKImpl(String applicationId, String secret) {

        super(applicationId, secret);

        /* Internal reference for later use as parent field is private */

        this.applicationId = applicationId;

    }

    /**
     * Makes an HTTP GET request to the provided URL, adding the
     * provided headers. The output is parsed and returned as a JSON
     * element with Google GSON library.
     * <p>
     * If something goes wrong during the connection a null element
     * will be returned.
     * 
     * @param URL
     *        The URL to where the GET request should be make.
     * @param headers
     *        The headers that should be added to the GET request.
     * 
     * @return The JSON returned by the Latch backend server or a null
     *         element if something has gone wrong.
     */
    public JsonElement HTTP_GET(String URL, Map<String, String> headers) {

        Utils.checkMethodRequiredParameter(LOG, "URL", URL);
        Utils.checkMethodRequiredParameter(LOG, "headers", headers);

        JsonElement rv = null;
        InputStream is = null;
        InputStreamReader isr = null;

        try {

            URL theURL = new URL(URL);
            HttpURLConnection theConnection = (HttpURLConnection) theURL.openConnection();

            Iterator<String> iterator = headers.keySet().iterator();

            while (iterator.hasNext()) {
                String headerName = iterator.next();
                theConnection.setRequestProperty(headerName, headers.get(headerName));
            }

            JsonParser parser = new JsonParser();

            is = theConnection.getInputStream();
            isr = new InputStreamReader(is);

            rv = parser.parse(isr);

        }
        catch (MalformedURLException e) {
            LOG.error("The URL is malformed (" + URL + ")", e);
        }
        catch (IOException e) {
            LOG.error("And exception has been thrown when communicating with Latch backend", e);
        }
        finally {

            if (isr != null) {

                try {
                    isr.close();
                }
                catch (IOException e) {
                }

            }

            if (is != null) {

                try {
                    is.close();
                }
                catch (IOException e) {
                }

            }

        }

        return rv;

    }

    /**
     * Returns the status of the latch identified by the provided
     * account id and, if not null, the provided operation id.
     * <p>
     * If something has gone wrong trying to get the latch status an
     * special <code>LatchSDKStatus</code> value will be returned (
     * <code>UNKNOWN</code>), so the application can choose what to
     * do.
     * <p>
     * If the backend returns a JSON with an error, this error is
     * returned as a <code>LatchErrorException</code>. It's also
     * responsibility of the application using this method to choose
     * what to do.
     * 
     * @param accountId
     *        The account id that identifies the latch being checked.
     * @param operationId
     *        The operation id that identifies the latch being
     *        checked. If the application has no operations, it should
     *        be null.
     * 
     * @return The status of the latch.
     * 
     * @throws LatchErrorException
     *         If the backend returns a JSON with an error.
     */
    public LatchSDKStatus parsedStatus(String accountId, String operationId) throws LatchErrorException {

        Utils.checkMethodRequiredParameter(LOG, "accountId", accountId);

        /* The default status is UNKNOWN */

        LatchSDKStatus rv = LatchSDKStatus.UNKNOWN;

        /* Connect to the server and get the JSON response to the status request */

        LatchResponse latchResponse = null;

        try {

            if (operationId == null) {
                latchResponse = status(accountId);
            }
            else {
                latchResponse = operationStatus(accountId, operationId);
            }

        }
        catch (Exception e) {
            LOG.error("And exception has been thrown when communicating with Latch backend", e);
            return rv;
        }

        /* Check if a response has been received and parsed correctly */

        if (latchResponse != null) {

            if (latchResponse.getData() != null) {

                /* The backend has responded with data */

                JsonObject jsonObject = (JsonObject) latchResponse.getData();

                if (jsonObject.has("operations")) {

                    JsonObject operations = jsonObject.getAsJsonObject("operations");
                    JsonObject latch = null;

                    if (operationId == null) {
                        if (operations.has(applicationId)) {
                            latch = operations.getAsJsonObject(applicationId);
                        }
                    }
                    else {
                        if (operations.has(operationId)) {
                            latch = operations.getAsJsonObject(operationId);
                        }
                    }

                    if (latch != null && latch.has("status")) {
                        if ("off".equals(latch.get("status").getAsString())) {
                            rv = LatchSDKStatus.LOCKED;
                        }
                        else if ("on".equals(latch.get("status").getAsString())) {
                            rv = LatchSDKStatus.UNLOCKED;
                        }
                    }

                }

            }
            else if (latchResponse.getError() != null) {

                /* The backend has responded with an application error message */

                LOG.error("The backend has responded with an application error message: " + latchResponse.getError());
                throw new LatchErrorException(latchResponse.getError());

            }

        }

        return rv;

    }

}