#!/usr/bin/env bash
set -exu
: "${XDOC_STATIC_DIR}"
: "${XDOC_CONFIG_DIR}"
: "${DOC_ROOT_DIR}"

# Creates a Progressive Web Application, PWA manifest: `site.webmanifest`

DIR_SELF="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${DIR_SELF}/_shared.sh"

TOML_PATH="${DOC_ROOT_DIR}/config.toml"

VALUE_TITLE=$(toml_value ".title" "${TOML_PATH}" | str_quotes_remove)
VALUE_SHORT_NAME=$(toml_value "extra.short_name" "${TOML_PATH}" | str_quotes_remove)
VALUE_DESCRIPTION=$(toml_value ".description" "${TOML_PATH}" | str_quotes_remove)
VALUE_BACKGROUND_COLOR=$(toml_value "extra.theme.dark" "${TOML_PATH}" | str_quotes_remove)
VALUE_THEME_COLOR=$(toml_value "extra.theme.light" "${TOML_PATH}" | str_quotes_remove)
VALUE_HOME_PATH=$(toml_value "extra.bundle.home_path" "${TOML_PATH}" | str_quotes_remove | str_escape)


cat "${XDOC_CONFIG_DIR}/site.webmanifest" | \
  sed "s/{{title}}/${VALUE_TITLE}/" | \
  sed "s/{{short_name}}/${VALUE_SHORT_NAME}/" | \
  sed "s/{{description}}/${VALUE_DESCRIPTION}/" | \
  sed "s/{{background_color}}/${VALUE_BACKGROUND_COLOR}/" | \
  sed "s/{{theme_color}}/${VALUE_THEME_COLOR}/" | \
  sed "s/{{home_path}}/${VALUE_HOME_PATH}/" > "${XDOC_STATIC_DIR}/site.webmanifest"

cat "${XDOC_STATIC_DIR}/site.webmanifest"
