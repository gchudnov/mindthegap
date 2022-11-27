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
    { white: "dark-primary", dark: "light-primary" }
  );

  console.log(info.colors);

  return {
    element: {
      enter: (node, parentNode) => {
      }
    }
  };  
}

/*
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
