#!/usr/bin/env bash
set -exu

# current directory
DIR_SELF="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cd "${DIR_SELF}/doc/themes/xdoc"

# install xdoc node.js dependencies
npm install

# set env variables
export XDOC_ROOT_DIR=$(readlink -f .)
export XDOC_STATIC_DIR=$(realpath "${XDOC_ROOT_DIR}/static")
export XDOC_CONFIG_DIR=$(realpath "${XDOC_STATIC_DIR}/../config")
export XDOC_PROJECT_DIR=$(realpath "${XDOC_ROOT_DIR}/../..")
export XDOC_BUILD_DIR=$(realpath "${XDOC_PROJECT_DIR}/public")
export XDOC_APP_VERSION_PATH="${DIR_SELF}/version.sbt"

# bundle icons
export XDOC_ICON_SVG_PATH="${DIR_SELF}/res/logo.svg"
npm run icons

# build Manifest
npm run manifest

# bundle theme
npm run theme

# bundle KaTeX
npm run katex

# bundle Roboto-Font
npm run roboto

# stub on first build to avoid `Error: Reason: `get_url`: Could not find or open file xdoc-bundle.min.js`
touch "${XDOC_STATIC_DIR}/xdoc-bundle.min.js"

# build zola project (1)
npm run build

# bundle search
npm run search

# bundle application
npm run bundle

# build zola project (2)
npm run build
