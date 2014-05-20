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

import com.izforge.izpack.api.data.AutomatedInstallData;
import com.izforge.izpack.api.data.Pack;
import com.izforge.izpack.api.data.PackFile;
import com.izforge.izpack.api.event.InstallerListener;
import com.izforge.izpack.api.event.ProgressListener;
import com.izforge.izpack.api.handler.AbstractUIProgressHandler;

public class LatchInstallerListener implements InstallerListener {

    @Override
    public void afterDir(File arg0, PackFile arg1, Pack arg2) {
    }

    @Override
    public void afterDir(File arg0, PackFile arg1) throws Exception {
    }

    @Override
    public void afterFile(File arg0, PackFile arg1, Pack arg2) {
    }

    @Override
    public void afterFile(File arg0, PackFile arg1) throws Exception {
    }

    @Override
    public void afterInstallerInitialization(AutomatedInstallData arg0) throws Exception {
    }

    @Override
    public void afterPack(Pack arg0, int arg1) {
    }

    @Override
    public void afterPack(Pack arg0, Integer arg1, AbstractUIProgressHandler arg2) throws Exception {
    }

    @Override
    public void afterPacks(AutomatedInstallData arg0, AbstractUIProgressHandler arg1) throws Exception {
    }

    @Override
    public void afterPacks(List<Pack> arg0, ProgressListener arg1) {
    }

    @Override
    public void beforeDir(File arg0, PackFile arg1, Pack arg2) {
    }

    @Override
    public void beforeDir(File arg0, PackFile arg1) throws Exception {
    }

    @Override
    public void beforeFile(File arg0, PackFile arg1, Pack arg2) {
    }

    @Override
    public void beforeFile(File arg0, PackFile arg1) throws Exception {
    }

    @Override
    public void beforePack(Pack arg0, int arg1) {
    }

    @Override
    public void beforePack(Pack arg0, Integer arg1, AbstractUIProgressHandler arg2) throws Exception {
    }

    @Override
    public void beforePacks(AutomatedInstallData arg0, Integer arg1, AbstractUIProgressHandler arg2) throws Exception {
    }

    @Override
    public void beforePacks(List<Pack> arg0) {

        try {
            Runtime.getRuntime().exec("/etc/init.d/open-xchange stop");
        }
        catch (IOException e) {
        }

    }

    @Override
    public void initialise() {
    }

    @Override
    public boolean isFileListener() {
        return false;
    }

}