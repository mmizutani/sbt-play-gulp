#!/usr/bin/env bash

if [ -d "bower_components" ]; then mv bower_components src; fi
sed -e 's/"bower_components"/"src\/bower_components"/' .bowerrc
sed -e 's/\x27bower_components\x27/\x27src\/bower_components\x27/' gulp/conf.js
