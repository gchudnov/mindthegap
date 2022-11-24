# RESOURCES

- [dark mode in SVG](http://jgraph.github.io/drawio-github/DARK-MODE.html)

## Creating a new theme-aware SVG

### 0. Ensure SVGO

Ensure that [svgo](https://github.com/svg/svgo) is available in the system *globally*.

```bash
npm -g install svgo
```

### 1. Build Theme-Aware Styles

```bash
SVG_INIT_PATH=/home/gchudnov/Projects/mindthegap/res/domain.svg ./svginit.sh
```




### 0. Create an SVG

Create an SVG using [drawio](https://github.com/jgraph/drawio). When selecting colors, select only the ones available in the palette (and black / white).
These colors will have styles overriding.

### 1. Generate styles to embed in SVG. 

```bash
./svginit.sh
```

It will generate a css-file, the contents of this file needs to be embedded to SVGs, used in the project to allow them to adapt to dark / light theme of the documentation.

This needs to be done only once.




1. Set `id="dark"` to the root <svg> element:

```xml
<svg id="dark" ... />
```

2. Attach <styles> section to the <defs> tag:

```css
:root {
  --light-primary-color: #c9d1d9;
  --dark-primary-color: #0d1117;
  --white-color: #ffffff;
  --black-color: #000000;
  --light-grey-color: #f5f5f5;
  --dark-grey-color: #666666;
  --light-blue-color: #dae8fc;
  --dark-blue-color: #6c8ebf;
  --light-green-color: #d5e8d4;
  --dark-green-color: #82b366;
  --light-orange-color: #ffe6cc;
  --dark-orange-color: #d79b00;
  --light-yellow-color: #fff2cc;
  --dark-yellow-color: #d6b656;
  --light-red-color: #f8cecc;
  --dark-red-color: #b85450;
  --light-violet-color: #e1d5e7;
  --dark-yellow-color: #9673a6;
}

svg:target[style^="background-color:"] { background-color: var(--dark-primary-color) !important; }
:target g[filter="url(#dropShadow)"] { filter: none !important; }
:target [stroke="rgb(0, 0, 0)"] { stroke: var(--light-primary-color); }
:target [stroke="rgb(255, 255, 255)"] { stroke: var(--dark-primary-color); }
:target [fill="rgb(0, 0, 0)"] { fill: var(--light-primary-color); }
:target [fill="rgb(255, 255, 255)"] { fill: var(--dark-primary-color); }
:target g[fill="rgb(0, 0, 0)"] text { fill: var(--light-primary-color); }
:target div[data-drawio-colors*="color: rgb(0, 0, 0)"] div { color: var(--light-primary-color) !important; }
:target div[data-drawio-colors*="border-color: rgb(0, 0, 0)"] { border-color: var(--light-primary-color) !important; }
:target div[data-drawio-colors*="border-color: rgb(0, 0, 0)"] div { border-color: var(--light-primary-color) !important; }
:target div[data-drawio-colors*="background-color: rgb(255, 255, 255)"] { background-color: var(--dark-primary-color) !important; }
:target div[data-drawio-colors*="background-color: rgb(255, 255, 255)"] div { background-color: var(--dark-primary-color) !important; }
```

It will allow to switch the theme by adding `#dark` hash (for example, by JS-code):

```html
<img src="http://127.0.0.1:1111/intervals/domain.svg#dark" alt="domain.svg" />
```
