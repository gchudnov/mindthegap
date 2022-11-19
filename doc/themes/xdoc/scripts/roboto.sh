#!/usr/bin/env bash
set -exu
: "${XDOC_STATIC_DIR}"

# Roboto Font
# https://developers.google.com/fonts/docs/getting_started

trap "exit 1" HUP INT PIPE QUIT TERM
trap 'rm -rf "${ROBOTO_TMP_DIR}"' EXIT

ROBOTO_REGULAR_LATIN="https://fonts.googleapis.com/css?family=Roboto&subset=latin"
ROBOTO_ITALIC_LATIN="https://fonts.googleapis.com/css?family=Roboto:italic&subset=latin"
ROBOTO_BOLD_LATIN="https://fonts.googleapis.com/css?family=Roboto:bold&subset=latin"

# the header is needed to get a woff2 url
BROWSER_HEADER="user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36"

ROBOTO_TMP_DIR=$(mktemp -d)
if [[ ! "${ROBOTO_TMP_DIR}" || ! -d "${ROBOTO_TMP_DIR}" ]]; then
  echo "Could not create a temporaty directory"
  exit 1
fi

# downloads font to a file
function download_font() {
  local font_url=$1
  local font_kind=$2

  # only latin font
  curl -L "${font_url}" -H "${BROWSER_HEADER}" \
    | sed -e '1,/\/* latin /d' | sed -n "s/^.*url(\(\S*\)).*$/\1/p" \
    | xargs -I '{}' curl -L '{}' --output "${ROBOTO_TMP_DIR}/Roboto-${font_kind}.woff2"
}

download_font "${ROBOTO_REGULAR_LATIN}" "Regular"
download_font "${ROBOTO_ITALIC_LATIN}" "Italic"
download_font "${ROBOTO_BOLD_LATIN}" "Bold"

cp "${ROBOTO_TMP_DIR}"/Roboto-*.woff2 "${XDOC_STATIC_DIR}/fonts/"
