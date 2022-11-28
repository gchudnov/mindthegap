#!/usr/bin/env bash
set -eu

DIR_RES_SELF="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CONF_PATH="$(realpath "${DIR_RES_SELF}/svgo.config.js")"

if [[ ! -f "${1}" ]]; then
  echo "File '${1}' is not found."
  exit
fi

# run svgo
npx svgo --multipass --config="${CONF_PATH}" "${1}"
