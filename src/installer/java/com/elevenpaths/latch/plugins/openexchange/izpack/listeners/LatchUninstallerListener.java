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

package com.elevenpaths.latch.plugins.openexchange.izpack.listeners;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.izforge.izpack.api.event.ProgressListener;
import com.izforge.izpack.api.event.UninstallerListener;
import com.izforge.izpack.api.handler.AbstractUIProgressHandler;

public class LatchUninstallerListener implements UninstallerListener {

    @Override
    public void afterDelete(File arg0) {
    }

    @Override
    public void afterDelete(List<File> arg0, ProgressListener arg1) {
    }

    @Override
    public void afterDelete(File arg0, AbstractUIProgressHandler arg1) throws Exception {
    }

    @Override
    public void afterDeletion(List arg0, AbstractUIProgressHandler arg1) throws Exception {
    }

    @Override
    public void beforeDelete(List<File> arg0) {

        try {
            Runtime.getRuntime().exec("/etc/init.d/open-xchange stop");
        }
        catch (IOException e) {
        }

    }

    @Override
    public void beforeDelete(File arg0) {
    }

    @Override
    public void beforeDelete(File arg0, AbstractUIProgressHandler arg1) throws Exception {
    }

    @Override
    public void beforeDeletion(List arg0, AbstractUIProgressHandler arg1) throws Exception {
    }

    @Override
    public void initialise() {
        System.out.println("initialise()");
    }

    @Override
    public boolean isFileListener() {
        return false;
    }

}