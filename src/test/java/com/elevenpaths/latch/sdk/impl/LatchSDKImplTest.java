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

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.elevenpaths.latch.LatchErrorException;

public class LatchSDKImplTest {

    /**
     * Test method that checks that with a valid applicationId, secret
     * and accountId the status of a latch (on or off) is obtained
     * from the Latch backend.
     */
    @Test
    @Parameters({ "applicationId", "secret", "accountId" })
    public void parsedStatus(String applicationId, String secret, String accountId) {

        /* Check that the parameters are neither null nor empty */

        assertNotNull(applicationId);
        assertFalse(applicationId.isEmpty());
        assertNotNull(secret);
        assertFalse(secret.isEmpty());
        assertNotNull(accountId);
        assertFalse(accountId.isEmpty());

        /* Check the status of the latch */

        try {

            LatchSDKImpl latch = new LatchSDKImpl(applicationId, secret);
            LatchSDKStatus status = latch.parsedStatus(accountId, null);

            assertTrue(status.equals(LatchSDKStatus.LOCKED) || status.equals(LatchSDKStatus.UNLOCKED));

        }
        catch (LatchErrorException e) {
            fail("Unexpected Exception (" + e.getClass().getName() + ")");
        }

    }

    /**
     * Test method that checks that with an invalid applicationId,
     * secret or accountId a LatchErrorException is thrown when trying
     * to obtain from the Latch backend the status of a latch (on or
     * off).
     */
    @Test
    @Parameters({ "applicationId", "secret", "accountId" })
    public void parsedStatusThrowsLatchErrorException(String applicationId, String secret, String accountId) {

        /* Check that the parameters are neither null nor empty */

        assertNotNull(applicationId);
        assertFalse(applicationId.isEmpty());
        assertNotNull(secret);
        assertFalse(secret.isEmpty());
        assertNotNull(accountId);
        assertFalse(accountId.isEmpty());

        /* Check the status of the latch */

        try {

            LatchSDKImpl latch = new LatchSDKImpl(applicationId, secret);
            LatchSDKStatus status = latch.parsedStatus(accountId, null);

            fail("Unexpected status (" + status.asString() + ")");

        }
        catch (LatchErrorException e) {
            assertTrue(true, "Expected Exception (" + e.getClass().getName() + ")");
        }

    }

    /**
     * Test method that checks that if an error takes place when
     * communicating with the backend the status of a latch being
     * checked is UNKNOWN.
     * 
     * The error is simulated mocking partially the Latch SDK
     * implementation and forcing the method HTTP_GET to return null.
     */
    @Test
    @Parameters({ "applicationId", "secret", "accountId" })
    @SuppressWarnings("unchecked")
    public void parsedStatusUnknown(String applicationId, String secret, String accountId) {

        /* Check that the parameters are neither null nor empty */

        assertNotNull(applicationId);
        assertFalse(applicationId.isEmpty());
        assertNotNull(secret);
        assertFalse(secret.isEmpty());
        assertNotNull(accountId);
        assertFalse(accountId.isEmpty());

        /* Prepare the mock object and the method */

        LatchSDKImpl latch = spy(new LatchSDKImpl(applicationId, secret));
        doReturn(null).when(latch).HTTP_GET(anyString(), anyMap());

        /* Check the status of the latch */

        try {

            LatchSDKStatus status = latch.parsedStatus(accountId, null);

            assertTrue(status.equals(LatchSDKStatus.UNKNOWN));

        }
        catch (LatchErrorException e) {
            fail("Unexpected Exception (" + e.getClass().getName() + ")");
        }

    }

    /**
     * Test method that checks that with a valid applicationId,
     * secret, operationId and accountId the status of a latch
     * associated with that operationId (on or off) is obtained from
     * the Latch backend.
     */
    @Test
    @Parameters({ "applicationId", "secret", "operationId", "accountId" })
    public void parsedStatusWithOperation(String applicationId, String secret, String operationId, String accountId) {

        /* Check that the parameters are neither null nor empty */

        assertNotNull(applicationId);
        assertFalse(applicationId.isEmpty());
        assertNotNull(secret);
        assertFalse(secret.isEmpty());
        assertNotNull(operationId);
        assertFalse(operationId.isEmpty());
        assertNotNull(accountId);
        assertFalse(accountId.isEmpty());

        /* Check the status of the latch */

        try {

            LatchSDKImpl latch = new LatchSDKImpl(applicationId, secret);
            LatchSDKStatus status = latch.parsedStatus(accountId, operationId);

            assertTrue(status.equals(LatchSDKStatus.LOCKED) || status.equals(LatchSDKStatus.UNLOCKED));

        }
        catch (LatchErrorException e) {
            fail("Unexpected Exception (" + e.getClass().getName() + ")");
        }

    }

}