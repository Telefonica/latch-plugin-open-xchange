#!/bin/bash

MANIFEST=%{UserPathPanelVariable}/bundles/com.openexchange.server/META-INF/MANIFEST.MF

if [ -f $MANIFEST ]
then
    if ! grep -q 'com.openexchange.authentication.service,' $MANIFEST
    then
    	if grep -q 'com.openexchange.authorization,' $MANIFEST
    	then
        	cp ${MANIFEST} ${MANIFEST}.prelatch
        	line=$(grep -n 'com.openexchange.authorization,' $MANIFEST | grep -o '^[0-9]*')
        	sed -i ${line}'i\ com.openexchange.authentication.service,' $MANIFEST
        fi
    fi
fi

rm -rf %{UserPathPanelVariable}/osgi/config.ini %{UserPathPanelVariable}/osgi/org.eclipse.osgi