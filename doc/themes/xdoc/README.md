# XDoc

A ZOLA-theme for documentation.

## Building

### 1. Install Dependencies

- node.js v. >= 19.0.0
- inkscape

```bash
# go to the theme directory
cd theme/xdoc

# inkscape
sudo add-apt-repository ppa:inkscape.dev/stable
sudo apt update
sudo apt install inkscape

# node.js dependencies
npm install
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

- `XDOC_ROOT_DIR` - is the root directory of the theme`.
- `XDOC_STATIC_DIR` - static directory of the theme.
- `XDOC_CONFIG_DIR` - path to the configuration directory of the theme.
- `XDOC_PROJECT_DIR` - root directory of the project.
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

### 3. Bundle Theme

Bundle theme storage management.

```bash
npm run theme
```

The command will take `theme-init.js` and produce:

```text
/static/theme-init.min.js
```

### 4. Bundle KaTeX

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

### 5. Bundle Roboto-Font

Bundle roboto font

```bash
npm run roboto
```

The command downloads roboto font from [onts.googleapis.com](https://fonts.googleapis.com), extracts regular, bold and italic fonts and produces:

```
/static/fonts/Roboto-*.woff2
```

### 6. Build Zola Project

```bash
zola build
```

It will build and copy to `/public` resources, including JavaScript required for search.

### 7. Bundle Search

```bash
npm run search
```

The command will take: takes `search_index.en.js`, `elasticlunr.min.js`, `search-handler.js`, and bundles them into:

```text
/static/search-bundle.min.js
```

Next, it will calculate sha-256 and sha-384 hashes for `search-bundle.min.js` and embedded them into:

```text
/static/search-facade.js
```

NOTE: after running, run `bundle` next, it bundles `search-facade.js`.

### 8. Bundle Application

```bash
npm run bundle
```

The command takes `search-facade.js` (created with `npm run search`), `theme-switcher.js`, `code-copy.js`, `aside-highlighter.js` and `scroll-to-top.js` and bundles them into:

```text
/static/xdoc-bundle.min.js
```

This bundle is loaded in the every page.

## Links

- [Mozilla Observatory ](https://observatory.mozilla.org/)
- [CSP Evaluator](https://csp-evaluator.withgoogle.com/)
