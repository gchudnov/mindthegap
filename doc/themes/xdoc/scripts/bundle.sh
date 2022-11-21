#!/usr/bin/env bash
set -exu
: "${XDOC_STATIC_DIR}"

# Bundle JS-Code
# /static/search-facade.js     - search-facade.js with sha-256 and sha-384 hashes.
# /static/theme-switcher.js    - handler to switch the theme (light / dark).
# /static/code-copy.js         - allow <code> to be copied to the clipboard.
# /static/aside-highlighter.js - highligh <aside> depending on the position of a scrollbar.
# /static/scroll-to-top.js     - scroll to top button behavior.

BUNDLE_NAME=xdoc-bundle.min.js

terser \
  "${XDOC_STATIC_DIR}/search-facade.js" \
  "${XDOC_STATIC_DIR}/theme-switcher.js" \
  "${XDOC_STATIC_DIR}/code-copy.js" \
  "${XDOC_STATIC_DIR}/aside-highlighter.js" \
  "${XDOC_STATIC_DIR}/aside-positioner.js" \
  "${XDOC_STATIC_DIR}/scroll-to-top.js" \
  -c -m -o "${XDOC_STATIC_DIR}/${BUNDLE_NAME}" \
  --source-map "url='${BUNDLE_NAME}.map'"

# post-process output from uglify to adjust sources values
sed -i "s|${XDOC_STATIC_DIR}||g" "${XDOC_STATIC_DIR}/${BUNDLE_NAME}.map"
