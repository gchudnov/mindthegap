# RESOURCES

- [dark mode in SVG](http://jgraph.github.io/drawio-github/DARK-MODE.html)

## Creating a new theme-aware SVG

Create an SVG using [drawio](https://github.com/jgraph/drawio). When selecting colors, select only the ones available in the palette.

### 0. Ensure SVGO

Ensure that [svgo](https://github.com/svg/svgo) is available in the system *globally*.
This step is required to do only once.

```bash
npm -g install svgo
```

### 1. Build theme-aware SVG

From the root of the project, execute the following commands:

```bash
./res/svg-theme.sh /path/to/svg/file.svg
```

After running,
- `id="dark"` will be added to the root `<svg>` element.
- style section `<style>` will be attached to `<defs>` tag.
- SVG-file will be overwritten.

To change the theme of an SVG, change `img.src` property by adding and removing `#dark` hash in the url.

## Building Documentation

Follow the instructions located in [doc/README.md](../doc/README.md).

