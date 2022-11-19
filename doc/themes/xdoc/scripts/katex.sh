#!/usr/bin/env bash
set -exu
: "${XDOC_STATIC_DIR}"

# Bundles code for KaTeX -- a cross-browser JavaScript library that displays mathematical notation in web browsers.
# - /katex/katex.min.css                       - related css
# - /katex/fonts/*.woff2                       - fonts for katex
# - /katex/katex.min.js                        - main js-library
# - /katex/contrib/mathtex-script-type.min.js  - extension to automatically display code inside script tags with type=math/tex using KaTeX
# - /katex/contrib/auto-render.min.js          - automatically render all of the math inside of text.
# - /static/katex-auto-render-init.js          - initialize auto-render.min.js

trap "exit 1" HUP INT PIPE QUIT TERM
trap 'rm -rf "${KATEX_TMP_DIR}"' EXIT

KATEX_VERSION=v0.16.3
KATEX_TAR_GZ_URL="https://github.com/KaTeX/KaTeX/releases/download/${KATEX_VERSION}/katex.tar.gz"

KATEX_TMP_DIR=$(mktemp -d)
if [[ ! "${KATEX_TMP_DIR}" || ! -d "${KATEX_TMP_DIR}" ]]; then
  echo "Could not create a temporaty directory"
  exit 1
fi

curl -L "${KATEX_TAR_GZ_URL}" --output "${KATEX_TMP_DIR}/katex.tar.gz"
tar -zxvf "${KATEX_TMP_DIR}/katex.tar.gz" --directory="${KATEX_TMP_DIR}"

mkdir -p "${XDOC_STATIC_DIR}/fonts/"

BUNDLE_NAME=katex-bundle.min.js

# js
terser \
  "${KATEX_TMP_DIR}/katex/katex.min.js" \
  "${KATEX_TMP_DIR}/katex/contrib/mathtex-script-type.min.js" \
  "${KATEX_TMP_DIR}/katex/contrib/auto-render.min.js" \
  "${XDOC_STATIC_DIR}/katex-auto-render-init.js" \
  -c -m -o "${XDOC_STATIC_DIR}/${BUNDLE_NAME}" \
  --source-map "url='${BUNDLE_NAME}.map'"

# post-process output from uglify to adjust sources values
sed -i "s|${XDOC_STATIC_DIR}||g" "${XDOC_STATIC_DIR}/${BUNDLE_NAME}.map"
sed -i "s|${KATEX_TMP_DIR}||g" "${XDOC_STATIC_DIR}/${BUNDLE_NAME}.map"

# css
cp "${KATEX_TMP_DIR}/katex/katex.min.css" "${XDOC_STATIC_DIR}/katex.min.css"

# fonts
cp "${KATEX_TMP_DIR}"/katex/fonts/*.woff2 "${XDOC_STATIC_DIR}/fonts/"
