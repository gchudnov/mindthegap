#!/usr/bin/env bash
set -exu
: "${XDOC_BUILD_DIR}"
: "${XDOC_APP_VERSION_PATH}"

# Replaces application version {{app_version}} in the ${XDOC_BUILD_DIR} (/public) directory

APP_VERSION=$(grep -oP 'ThisBuild / version\s+:= "\K[^"]+' "${XDOC_APP_VERSION_PATH}")

find "${XDOC_BUILD_DIR}" -type f -exec sed -i "s|{{app_version}}|${APP_VERSION}|g" {} +
