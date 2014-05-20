#!/bin/bash

PATH=${PATH}:@@@APPSUITE_UI_BINDIR@@@
echo $PATH

cd @@@BASEDIR@@@/src/main/appsuite
build-appsuite app skipLess=1 builddir=@@@BASEDIR@@@/dist/pairing/appsuite
