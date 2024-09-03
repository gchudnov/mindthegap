#!/usr/bin/env bash
set -exu
: "${XDOC_BUILD_DIR}"
: "${XDOC_APP_VERSION_PATH}"

# get app version from version.sbt
APP_VERSION=$(grep -oP 'ThisBuild / version\s+:= "\K[^"]+' "${XDOC_APP_VERSION_PATH}")
APP_VERSION=$(echo "${APP_VERSION}" | sed 's/-SNAPSHOT//')

find "${XDOC_BUILD_DIR}" -type f -exec sed -i "s|{{APP_VERSION}}|${APP_VERSION}|g" {} +
