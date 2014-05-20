#!/bin/bash

MANIFEST=%{UserPathPanelVariable}/bundles/com.openexchange.server/META-INF/MANIFEST.MF

if [ -f $MANIFEST ]
then
	if [ -f $MANIFEST.prelatch ]
	then
       	mv ${MANIFEST}.prelatch ${MANIFEST}
    fi
fi

rm -rf %{UserPathPanelVariable}/osgi/config.ini %{UserPathPanelVariable}/osgi/org.eclipse.osgi