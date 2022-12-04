#!/usr/bin/env bash
set -exu
: "${XDOC_BUILD_DIR}"
: "${XDOC_CONFIG_DIR}"
: "${XDOC_STATIC_DIR}"

# Bundles code for search
# - /public/search_index.en.js - default search setup, see https://www.getzola.org/documentation/content/search/
# - /public/elasticlunr.min.js - lightweight full-text search engine in Javascript
# - static/search-handler.js   - search-support code

BUNDLE_NAME=search-bundle.min.js

terser \
  "${XDOC_BUILD_DIR}/search_index.en.js" \
  "${XDOC_BUILD_DIR}/elasticlunr.min.js" \
  "${XDOC_STATIC_DIR}/search-handler.js" \
  -c -m -o "${XDOC_STATIC_DIR}/${BUNDLE_NAME}" \
  --source-map "url='${BUNDLE_NAME}.map'"

# post-process output from uglify to adjust `sources` values
sed -i "s|${XDOC_STATIC_DIR}||g" "${XDOC_STATIC_DIR}/${BUNDLE_NAME}.map"
sed -i "s|${XDOC_BUILD_DIR}||g" "${XDOC_STATIC_DIR}/${BUNDLE_NAME}.map"

# update sha-256 and sha-384 in search-facade
SEARCH_SHA256=$(cat "${XDOC_STATIC_DIR}/search-bundle.min.js" | openssl dgst -sha256 -binary | openssl base64 -A)
SEARCH_SHA384=$(cat "${XDOC_STATIC_DIR}/search-bundle.min.js" | openssl dgst -sha384 -binary | openssl base64 -A)

sed "s|{{SHA256}}|${SEARCH_SHA256}|g" "${XDOC_CONFIG_DIR}/search-facade.js" |
  sed "s|{{SHA384}}|${SEARCH_SHA384}|g" >"${XDOC_STATIC_DIR}/search-facade.js"
