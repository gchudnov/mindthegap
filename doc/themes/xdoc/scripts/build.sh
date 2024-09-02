#!/usr/bin/env bash
set -exu
: "${XDOC_APP_VERSION_PATH}"

# build zola project (1)
zola build

# replace version placeholder with actual version
npm run version
