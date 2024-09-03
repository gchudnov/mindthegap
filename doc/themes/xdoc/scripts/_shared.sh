#!/usr/bin/env bash
set -exu

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

# escapes slashes in a string
function str_escape() {
  sed 's/\//\\\//g'
}
