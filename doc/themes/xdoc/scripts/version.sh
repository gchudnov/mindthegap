#!/usr/bin/env bash
set -exu
: "${XDOC_PROJECT_DIR}"

# Replaces version placeholders in the documentation with the actual version

DIR_SELF="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${DIR_SELF}/_shared.sh"

TOML_PATH="${XDOC_PROJECT_DIR}/config.toml"

VALUE_APP_VERSION=$(toml_value "extra.bundle.app_version" "${TOML_PATH}" | str_quotes_remove | str_escape)

find "${XDOC_BUILD_DIR}" -type f -exec sed -i "s|{{APP_VERSION}}|${VALUE_APP_VERSION}|g" {} +
