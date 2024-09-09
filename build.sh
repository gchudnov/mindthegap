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
export XDOC_CONFIG_DIR=$(realpath "${XDOC_ROOT_DIR}/config")

export DOC_ROOT_DIR=$(realpath "${XDOC_ROOT_DIR}/../..")
export DOC_BUILD_DIR=$(realpath "${DOC_ROOT_DIR}/public")

export RES_DIR="${DOC_ROOT_DIR}/../res"

# bundle icons
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
