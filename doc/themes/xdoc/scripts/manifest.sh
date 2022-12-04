#!/usr/bin/env bash
set -exu
: "${XDOC_STATIC_DIR}"
: "${XDOC_CONFIG_DIR}"
: "${XDOC_PROJECT_DIR}"

# Creates a Progressive Web Application, PWA manifest - `site.webmanifest`

function toml_flatten() {
  local toml_path=$1

  awk -F ' *= *' '{ if ($1 ~ /^\[/) { gsub(/^[ \t]+/,"",$1); gsub(/[ \t]+$/,"",$1); gsub(/^\[|\]$/,"",$1); section=$1;} else if ($1 !~ /^$/) { gsub(/[^"]#.*$/,"",$2); gsub(/^[ \t]+/,"",$2);gsub(/[ \t]+$/,"",$2); print section "." $1 "=" $2 } }' "${toml_path}"
}

# given a path of a key, `section.key` gets a value out of it (NOTE: the value might have quotes)
function toml_value() {
  local key=$1
  local toml_path=$2

  toml_flatten "${toml_path}" | \
    sed -n -e "s/^\s*${key}\s*=\s*//p"
}

# removes quotes from a string if they are present
function str_quotes_remove() {
  set -- "${1:-$(</dev/stdin)}" "${@:2}"
  sed -e 's/^"//' -e 's/"$//' <<<"${1}"
}

VALUE_TITLE=$(toml_value ".title" "${XDOC_PROJECT_DIR}/config.toml" | str_quotes_remove)
VALUE_SHORT_NAME=$(toml_value "extra.short_name" "${XDOC_PROJECT_DIR}/config.toml" | str_quotes_remove)
VALUE_DESCRIPTION=$(toml_value ".description" "${XDOC_PROJECT_DIR}/config.toml" | str_quotes_remove)
VALUE_BACKGROUND_COLOR=$(toml_value "extra.theme.dark" "${XDOC_PROJECT_DIR}/config.toml" | str_quotes_remove)
VALUE_THEME_COLOR=$(toml_value "extra.theme.light" "${XDOC_PROJECT_DIR}/config.toml" | str_quotes_remove)


cat "${XDOC_CONFIG_DIR}/site.webmanifest" | \
  sed "s/{{title}}/${VALUE_TITLE}/" | \
  sed "s/{{short_name}}/${VALUE_SHORT_NAME}/" | \
  sed "s/{{description}}/${VALUE_DESCRIPTION}/" | \
  sed "s/{{background_color}}/${VALUE_BACKGROUND_COLOR}/" | \
  sed "s/{{theme_color}}/${VALUE_THEME_COLOR}/" > "${XDOC_STATIC_DIR}/site.webmanifest"

cat "${XDOC_STATIC_DIR}/site.webmanifest"
