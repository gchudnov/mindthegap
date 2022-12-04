#!/usr/bin/env bash
set -exu

# current directory
DIR_SELF="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# node.js
curl -fsSL https://deb.nodesource.com/setup_19.x | sudo -E bash - && \
sudo apt-get install -y nodejs

# inkscape
sudo add-apt-repository ppa:inkscape.dev/stable
sudo apt update
sudo apt install -y inkscape

# zola
snap install --edge zola

# install xdoc dependencies
cd "${DIR_SELF}/doc/themes/xdoc"

npm install

# set env variables
export XDOC_ROOT_DIR=$(readlink -f .)
export XDOC_STATIC_DIR=$(realpath "${XDOC_ROOT_DIR}/static")
export XDOC_CONFIG_DIR=$(realpath "${XDOC_STATIC_DIR}/../config")
export XDOC_PROJECT_DIR=$(realpath "${XDOC_ROOT_DIR}/../..")
export XDOC_BUILD_DIR=$(realpath "${XDOC_PROJECT_DIR}/public")

# bundle icons
export XDOC_ICON_SVG_PATH="${DIR_SELF}/res/logo512.svg"
npm run icons

# build Manifest
npm run manifest

# bundle theme
npm run theme

# bundle KaTeX
npm run katex

# bundle Roboto-Font
npm run roboto

# build zola project (1)
zola build

# bundle search
npm run search

# bundle application
npm run bundle

# build zola project (2)
zola build
