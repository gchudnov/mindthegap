module.exports = {
  multipass: true,
  plugins: [
    {
      name: "preset-default",
      params: {
        overrides: {
          removeViewBox: false,
          convertColors: {
            shorthex: false,
            shortname: false,
          },
        },
      },
    },
    "cleanupListOfValues",
    "removeDimensions",
    "sortAttrs",
    {
      name: "addAttributesToSVGElement",
      params: {
        attribute: { id: "dark" },
      },
    },
    {
      name: "normalizeColors",
      type: "visitor",
      params: {},
      fn: (ast, params, info) => {
        return normalizeColors(ast, params, info);
      },
    },
    {
      name: "attributeColors",
      type: "visitor",
      params: {},
      fn: (ast, params, info) => {
        return attributeColors(ast, params, info);
      },
    },
    {
      name: "themeColors",
      type: "visitor",
      params: {},
      fn: (ast, params, info) => {
        return themeColors(ast, params, info);
      },
    },
  ],
};

/**
 * Plugin to normalize colors
 *
 * rgb(a, b, c) => #rrggbb
 * #rgb         => #rrggbb
 * #rrggbb      => #rrggbb | as-is
 */
function normalizeColors(ast, params, info) {
  const rNumber = "([+-]?(?:\\d*\\.\\d+|\\d+\\.?)%?)";
  const rComma = "\\s*,\\s*";
  const rRGB = new RegExp(
    "rgb\\(\\s*" + rNumber + rComma + rNumber + rComma + rNumber + "\\s*\\)"
  );

  const rComponent = "[a-fA-F0-9]";
  const xRGB = new RegExp(`#(${rComponent})(${rComponent})(${rComponent})\b`);

  /**
   * Convert [r, g, b] to #rrggbb.
   */
  const rgbToHex = (r, g, b) => {
    const hexNumber = (r << 16) | (g << 8) | b;
    const hexStr = hexNumber.toString(16).toUpperCase().padStart(6, "0");
    return "#" + hexStr;
  };

  return {
    element: {
      enter: (node, parentNode) => {
        node.attributes = Object.entries(node.attributes).reduce(
          (acc, [name, value]) => {
            // #rgb -> #rrggbb
            value = value.replace(xRGB, (match, r, g, b) => {
              return "#" + r.repeat(2) + g.repeat(2) + b.repeat(2);
            });

            // rgb(a, b, c) => #rrggbb
            value = value.replace(rRGB, (match, r, g, b) => {
              return rgbToHex(r, g, b);
            });

            acc[name] = value;
            return acc;
          },
          {}
        );
      },
    },
  };
}

/**
 * Find colors and their types, store in the info object
 */
function attributeColors(ast, params, info) {
  const rHEX = /#([0-9a-fA-F]{2}){3}/;

  const rColor = /color\s*:\s*(#([0-9a-fA-F]{2}){3})/g;
  const rBorderColor = /border-color\s*:\s*(#([0-9a-fA-F]{2}){3})/g;
  const rBackgroundColor = /background-color\s*:\s*(#([0-9a-fA-F]{2}){3})/g;

  const colors = {
    fill: [],
    stroke: [],
    color: [],
    borderColor: [],
    backgroundColor: [],
  };

  info.colors = colors;

  const addColor = (kind, value) => {
    if (!colors[kind].includes(value)) {
      colors[kind].push(value);
    }
  };

  return {
    element: {
      enter: (node, parentNode) => {
        Object.entries(node.attributes).forEach(([name, value]) => {
          // fill="#rrggbb"
          if (name == "fill" && value.match(rHEX)) {
            addColor("fill", value);
          }

          // stroke="#rrggbb"
          if (name == "stroke" && value.match(rHEX)) {
            addColor("stroke", value);
          }

          // color: #rrggbb
          [...value.matchAll(rColor)].forEach((m) => {
            addColor("color", m[1]);
          });

          // border-color: #rrggbb
          [...value.matchAll(rBorderColor)].forEach((m) => {
            addColor("borderColor", m[1]);
          });

          // background-color: #rrggbb
          [...value.matchAll(rBackgroundColor)].forEach((m) => {
            addColor("backgroundColor", m[1]);
          });
        });
      },
    },
  };
}

/**
 * Make Colors for a Theme
 */
function themeColors(ast, params, info) {
  const colors = {
    "light-primary": "#c9d1d9", // primary
    "dark-primary": "#0d1117",
    "light-grey": "#f5f5f5", // grey
    "dark-grey": "#666666",
    "light-blue": "#dae8fc", // blue
    "dark-blue": "#6c8ebf",
    "light-green": "#d5e8d4", // green
    "dark-green": "#82b366",
    "light-orange": "#ffe6cc", // orange
    "dark-orange": "#d79b00",
    "light-yellow": "#fff2cc", // yellow
    "dark-yellow": "#d6b656",
    "light-red": "#f8cecc", // red
    "dark-red": "#b85450",
    "light-violet": "#e1d5e7", // violet
    "dark-violet": "#9673a6",

    white: "#ffffff", // white
    black: "#000000", // black
  };

  const values = Object.fromEntries(
    Object.entries(colors).map(([key, value]) => {
      return [value, key];
    })
  );

  const inversions = [
    "primary",
    "grey",
    "blue",
    "green",
    "orange",
    "yellow",
    "red",
    "violet",
  ].reduce(
    (acc, name) => {
      return Object.assign(acc, {
        [`light-${name}`]: `dark-${name}`,
        [`dark-${name}`]: `light-${name}`,
      });
    },
    { white: "dark-primary", black: "light-primary" }
  );

  const toInverse = (code) => {
    const name = values[code];
    const inversion = inversions[name];
    if (!inversion) { console.log(`ERROR: Inversion of the color '${code}' is not defined. Update the script to define it.`); }
    return inversion;
  };

  return {
    element: {
      enter: (node, parentNode) => {
        if (node.name === 'svg' && parentNode.type === 'root') {
          // remove defs with styles
          node.children.reduce((acc, child) => {
            if (child.name === 'defs' && child.attributes.hasOwnProperty('id') && child.attributes["id"] === "styles") {
              console.log({child, cs: child.children, css: child.children[0].children });
              // no-op
            } else {
              acc.push(child);
            }
            return acc;
          }, []);

          // add new defs
          const codes = Array.from(new Set([].concat(info.colors.fill, info.colors.stroke, info.colors.color, info.colors.borderColor, info.colors.backgroundColor)));
          
          codes.push(colors["light-primary"]); // to force the opposite in to the variables, `dark-primary`

          const {vars, varValues} = codes.reduce((acc, code) => {
            const inversion = toInverse(code);

            acc.vars.push(`--${inversion}-color`);
            acc.varValues.push(colors[inversion]);

            return acc;
          }, { vars: [], varValues: [] });

          const styles = [];

          styles.push(":root {");
          styles.push(...[...Array(vars.length).keys()].map((i) => {  return `${vars[i]}: ${varValues[i]};`; }));
          styles.push("}");

          styles.push("svg:target[style^=\"background-color:\"] { background-color: var(--dark-primary-color) !important; }");
          styles.push(":target g[filter=\"url(#dropShadow)\"] { filter: none !important; }");

          // fill
          const fillStyles = info.colors.fill.flatMap((code) => {
            const inversion = toInverse(code);
            return [
              `:target [fill=${code}] { fill: var(--${inversion}-color); }`,
              `:target g[fill=${code}] text { fill: var(--${inversion}-color); }`,
            ];
          });
          styles.push(...fillStyles);

          // stroke
          const strokeStyles = info.colors.stroke.flatMap((code) => {
            const inversion = toInverse(code);
            return [
              `:target [stroke=${code}] { stroke: var(--${inversion}-color); }`
            ];
          });
          styles.push(...strokeStyles);

          // color
          const colorStyles = info.colors.color.flatMap((code) => {
            const inversion = toInverse(code);
            return [
              `:target div[data-drawio-colors*=\"color: ${code}\"] div { color: var(--${inversion}-color) !important; }`
            ];
          });
          styles.push(...colorStyles);

          // border-color
          const borderColorStyles = info.colors.borderColor.flatMap((code) => {
            const inversion = toInverse(code);
            return [
              `:target div[data-drawio-colors*=\"border-color: ${code}\"] { border-color: var(--${inversion}-color) !important; }`,
              `:target div[data-drawio-colors*=\"border-color: ${code}\"] div { border-color: var(--${inversion}-color) !important; }`
            ];
          });
          styles.push(...borderColorStyles);

          // background-color
          const backgroundColorStyles = info.colors.backgroundColor.flatMap((code) => {
            const inversion = toInverse(code);
            return [
              `:target div[data-drawio-colors*=\"background-color: ${code}\"] { background-color: var(--${inversion}-color) !important; }`,
              `:target div[data-drawio-colors*=\"background-color: ${code}\"] div { background-color: var(--${inversion}-color) !important; }`
            ];
          });
          styles.push(...backgroundColorStyles);

          console.log({codes, vars, varValues});
          console.log({styles});

          const defs = {

          };
        }
      }
    }
  };  
}

/*
{
  child: {
    type: 'element',
    name: 'defs',
    attributes: { id: 'styles' },
    children: [ [Object] ]
  },
  cs: [
    {
      type: 'element',
      name: 'style',
      attributes: {},
      children: [Array]
    }
  ],
  css: [
    {
      type: 'text',
      value: ':root{--light-color:#c9d1d9;--dark-color:#0d1117}svg:target[style^="background-color:"]{background-color:var(--dark-color)!important}:target [stroke="rgb(0, 0, 0)"]{stroke:var(--light-color)}:target [stroke="rgb(255, 255, 255)"]{stroke:var(--dark-color)}:target [fill="rgb(0, 0, 0)"]{fill:var(--light-color)}:target [fill="rgb(255, 255, 255)"]{fill:var(--dark-color)}:target div[data-drawio-colors*="color: rgb(0, 0, 0)"] div{color:var(--light-color)!important}:target div[data-drawio-colors*="border-color: rgb(0, 0, 0)"],:target div[data-drawio-colors*="border-color: rgb(0, 0, 0)"] div{border-color:var(--light-color)!important}:target div[data-drawio-colors*="background-color: rgb(255, 255, 255)"],:target div[data-drawio-colors*="background-color: rgb(255, 255, 255)"] div{background-color:var(--dark-color)!important}'
    }
  ]



{
  child: {
    type: 'element',
    name: 'defs',
    attributes: {},
    children: [ [Object] ]
  }
}


{
  fill: [ '#f5f5f5' ],
  stroke: [ '#000000' ],
  color: [ '#000000' ],
  borderColor: [],
  backgroundColor: []
}


{
  child: {
    type: 'element',
    name: 'path',
    attributes: {
      stroke: '#000',
      'stroke-miterlimit': '10',
      d: 'm325.88 170-7 3.5 1.75-3.5-1.75-3.5Z',
      'pointer-events': 'all'
    },
    children: [],
    pathJS: [ [Object], [Object], [Object], [Object], [Object] ]
  }
}


{
  node: {
    type: 'element',
    name: 'div',
    attributes: {
      'data-drawio-colors': 'color: rgb(0, 0, 0);',
      style: 'box-sizing:border-box;font-size:0;text-align:center'
    },
    children: [ [Object] ]
  }
}

/////////////////////

{ ast: { type: 'root', children: [ [Object] ] } }
{ params: { optionName: 'optionValue' } }
{
  info: {
    path: '/home/gchudnov/Projects/mindthegap/res/domain.svg',
    multipassCount: 0
  }
}
*/
