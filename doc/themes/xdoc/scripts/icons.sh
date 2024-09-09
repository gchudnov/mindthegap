#!/usr/bin/env bash
set -exu
: "${XDOC_CONFIG_DIR}"
: "${XDOC_STATIC_DIR}"
: "${RES_DIR}"

# Generates all of the required icons given an input image
# - https://evilmartians.com/chronicles/how-to-favicon-in-2021-six-files-that-fit-most-needs
#
# Dependencies:
# - [inkscape](https://inkscape.org/)
# - [svgo](https://github.com/svg/svgo)

ICON_SVG_PATH="${RES_DIR}/logo.svg"

# make icon SVG
inkscape "${ICON_SVG_PATH}" --export-type=svg --export-filename="${XDOC_STATIC_DIR}/icon.svg"
npx svgo --multipass --config="${XDOC_CONFIG_DIR}/svgo.config.js" "${XDOC_STATIC_DIR}/icon.svg"

# make icon PNGs
inkscape "${ICON_SVG_PATH}" --export-width=512 --export-filename="${XDOC_STATIC_DIR}/icon-512.png"
inkscape "${ICON_SVG_PATH}" --export-width=192 --export-filename="${XDOC_STATIC_DIR}/icon-192.png"
