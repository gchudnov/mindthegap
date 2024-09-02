# XDoc

A ZOLA-theme for documentation.

## Building

For the example of usage of the given instructions, see: `${PROJECT_ROOT}/build.sh` that is used in github-actions.

### 1. Install Dependencies

- zola v. >= 0.19.2
- node.js v. >= 20.0.0
- inkscape v. >= 1.3-dev-74adff3496-2022-05-26

```bash
# go to the theme directory
cd doc/themes/xdoc

# zola
snap install --edge zola

# zola 0.19.2

# inkscape
sudo add-apt-repository ppa:inkscape.dev/stable
sudo apt update
sudo apt install inkscape

# OR
sudo snap install --edge inkscape

# inkscape (edge) 1.3-dev-74adff3496-2022-05-26 from Inkscape Project (inkscape✓) installed

# node.js dependencies
npm install

# upgrades your package.json dependencies to the latest versions
npm install -g npm-check-updates

# run ncu to see if we want to update dependencies, defined in package.json
ncu

# update
ncu -u
```

### 2. Set Environment-Variables

Set environment variables:

```bash
export XDOC_ROOT_DIR=$(readlink -f .)
export XDOC_STATIC_DIR=$(realpath "${XDOC_ROOT_DIR}/static")
export XDOC_CONFIG_DIR=$(realpath "${XDOC_STATIC_DIR}/../config")
export XDOC_PROJECT_DIR=$(realpath "${XDOC_ROOT_DIR}/../..")
export XDOC_BUILD_DIR=$(realpath "${XDOC_PROJECT_DIR}/public")
```

where:

- `XDOC_ROOT_DIR` - is the root directory of the theme.
- `XDOC_STATIC_DIR` - static directory of the theme.
- `XDOC_CONFIG_DIR` - path to the configuration directory of the theme.
- `XDOC_PROJECT_DIR` - root directory of the project documentation.
- `XDOC_BUILD_DIR` - build directory of the project (not the theme). Usually it is `${XDOC_PROJECT_DIR}/public` directory.

### 3. Bundle Icons

```bash
export XDOC_ICON_SVG_PATH=/path/to/original/icon.svg
npm run icons
```

The command, given an input SVG in `XDOC_ICON_SVG_PATH`-variable, will create:

```text
/static/icon-512.png
/static/icon-192.png
/static/icon.svg
```

### 4. Build Manifest

To build a manifest for PWA (Progressive Web Application):

```bash
npm run manifest
```

will build:

```text
/static/site.webmanifest
```

### 5. Bundle Theme

Bundle theme storage management.

```bash
npm run theme
```

The command will take `theme-init.js` and produce:

```text
/static/theme-init.min.js
```

### 6. Bundle KaTeX

Bundle KaTeX related JavaScript and styles.

```bash
npm run katex
```

The command downloads [KaTeX](https://github.com/KaTeX/) and bundles it with `/static/katex-auto-render-init.js`, producing

```text
/static/katex-bundle.min.js
/static/katex.min.css
/static/fonts/katex*.woff2
```

### 7. Bundle Roboto-Font

Bundle roboto font

```bash
npm run roboto
```

The command downloads roboto font from [fonts.googleapis.com](https://fonts.googleapis.com), extracts regular, bold and italic fonts and produces:

```
/static/fonts/Roboto-*.woff2
```

### 8. Build Zola Project

```bash
# make a stub to avoid `Error: Reason: `get_url`: Could not find or open file xdoc-bundle.min.js`
# the proper file will be created on the second build
touch "${XDOC_STATIC_DIR}/xdoc-bundle.min.js"

# set the version of the application
export XDOC_APP_VERSION_PATH=/path/to/version/file.sbt

# build (1)
npm run build
```

It will build and copy to `/public` resources, including JavaScript required for search.

### 9. Bundle Search

```bash
npm run search
```

The command takes `search_index.en.js`, `elasticlunr.min.js`, `search-handler.js`, and bundles them into:

```text
/static/search-bundle.min.js
```

Next, it calculates sha-256 and sha-384 hashes for `search-bundle.min.js` and embeds them into:

```text
/static/search-facade.js
```

### 10. Bundle Application

```bash
npm run bundle
```

The command takes `search-facade.js` that was just created, `theme-switcher.js`, `code-copy.js`, `aside-highlighter.js` and `scroll-to-top.js` and bundles them into:

```text
/static/xdoc-bundle.min.js
```

This bundle is loaded on the every page.

### 11. Rebuild

The second build will include generated `xdoc-bundle.min.js` that was previously missing on the first build.

```bash
# build (2)
npm run build
```

### 12. Serve

To see the results locally, use:

```bash
zola serve
```

## Links

- [Mozilla Observatory ](https://observatory.mozilla.org/)
- [CSP Evaluator](https://csp-evaluator.withgoogle.com/)
