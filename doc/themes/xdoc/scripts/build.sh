#!/usr/bin/env bash
set -exu

# build zola project
zola build

# replace version placeholder with actual version
npm run version
