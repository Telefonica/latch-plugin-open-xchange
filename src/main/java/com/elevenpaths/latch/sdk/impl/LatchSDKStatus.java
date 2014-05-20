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

/**
 * The status of a latch. It can have three values:
 * <ul>
 * <li><code>LOCKED</code> if the lath is locked (off).</li>
 * <li><code>UNLOCKED</code> if the lath is unlocked (on).</li>
 * <li><code>UNKNOWN</code> if the latch couldn't be checked.</li>
 * </ul>
 * If the status is <code>UNKNOW</code> an unexpected error has
 * happened when checking the latch (for example if the Latch backend
 * is down).
 */
public enum LatchSDKStatus {

    /** The latch is in LOCKED state (off). */
    LOCKED("off"),

    /** The latch is in UNLOCKED state (on). */
    UNLOCKED("on"),

    /** The latch is in UNKNOWN state (unknown). */
    UNKNOWN("unknown");

    /** The string representation of the status. */
    private String status;

    /**
     * Constructs a <code>LatchSDKStatus</code> from the string
     * representation of the status.
     * 
     * @param status
     *        The string representation of the status.
     */
    private LatchSDKStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the string representation of this
     * <code>LatchSDKStatus</code>.
     * 
     * @return The string representation of this
     *         <code>LatchSDKStatus</code>.
     */
    public String asString() {
        return status;
    }

}