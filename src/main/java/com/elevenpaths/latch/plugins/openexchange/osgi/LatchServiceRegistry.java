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

package com.elevenpaths.latch.plugins.openexchange.osgi;

import com.openexchange.osgi.ServiceRegistry;

/**
 * Tracks needed services for other classes in the bundle.
 */
public class LatchServiceRegistry {

    /** The service registry */
    private static final ServiceRegistry REGISTRY = new ServiceRegistry();

    /**
     * Default constructor.
     */
    private LatchServiceRegistry() {
    }

    /**
     * Gets a reference to the service registry.
     * 
     * @return The service registry.
     */
    public static ServiceRegistry getServiceRegistry() {
        return REGISTRY;
    }

}