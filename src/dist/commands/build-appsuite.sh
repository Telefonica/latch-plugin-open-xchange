#!/bin/bash

cd @@@BASEDIR@@@/dist/main/appsuite
echo latch | yo ox-ui-module
grunt dist:build