#!/usr/bin/env bash
set -exu
: "${DOC_BUILD_DIR}"
: "${XDOC_STATIC_DIR}"

# Bundles theme initialization
# - theme-init.js - sets the current theme if it was not set

mkdir -p "${DOC_BUILD_DIR}"

BUNDLE_NAME=theme-init.min.js

terser "${XDOC_STATIC_DIR}/theme-init.js" \
  -c -m -o "${XDOC_STATIC_DIR}/${BUNDLE_NAME}" \
  --source-map "url='${BUNDLE_NAME}.map'"

# post-process output from uglify to adjust sources values
sed -i "s|${XDOC_STATIC_DIR}||g" "${XDOC_STATIC_DIR}/${BUNDLE_NAME}.map"
