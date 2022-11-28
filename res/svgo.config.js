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

  const rChan = "a-fA-F0-9";
  const xRGB = new RegExp(`#([${rChan}])([${rChan}])([${rChan}])\\b`);

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
    if (!inversion) {
      console.log(
        `ERROR: Inversion of the color '${code}' is not defined. Update the script to define it.`
      );
    }
    return inversion;
  };

  const hasTheme = info.hasOwnProperty("hasTheme") && info["hasTheme"] == true;

  return {
    element: {
      enter: (node, parentNode) => {
        if (node.name === "svg" && parentNode.type === "root") {
          if (hasTheme) {
            // the theme was already set, do nothing
            return;
          }

          // remove defs with styles
          node.children = node.children.reduce((acc, child) => {
            if (
              child.name === "defs" &&
              child.attributes.hasOwnProperty("id") &&
              child.attributes["id"] === "styles"
            ) {
              // no-op
            } else {
              acc.push(child);
            }
            return acc;
          }, []);

          // add new defs
          const codes = Array.from(
            new Set(
              [].concat(
                info.colors.fill,
                info.colors.stroke,
                info.colors.color,
                info.colors.borderColor,
                info.colors.backgroundColor
              )
            )
          );

          codes.push(colors["light-primary"]); // to force the opposite in to the variables, `dark-primary`

          const { vars, varValues } = codes.reduce(
            (acc, code) => {
              const inversion = toInverse(code);

              acc.vars.push(`--${inversion}-color`);
              acc.varValues.push(colors[inversion]);

              return acc;
            },
            { vars: [], varValues: [] }
          );

          const styles = [];

          styles.push(":root {");
          styles.push(
            ...[...Array(vars.length).keys()].map((i) => {
              return `${vars[i]}: ${varValues[i]};`;
            })
          );
          styles.push("}");

          styles.push(
            'svg:target[style^="background-color:"] { background-color: var(--dark-primary-color) !important; }'
          );
          styles.push(
            ':target g[filter="url(#dropShadow)"] { filter: none !important; }'
          );

          // fill
          const fillStyles = info.colors.fill.flatMap((code) => {
            const inversion = toInverse(code);
            return [
              `:target [fill="${code}"] { fill: var(--${inversion}-color); }`,
              `:target g[fill="${code}"] text { fill: var(--${inversion}-color); }`,
            ];
          });
          styles.push(...fillStyles);

          // stroke
          const strokeStyles = info.colors.stroke.flatMap((code) => {
            const inversion = toInverse(code);
            return [
              `:target [stroke="${code}"] { stroke: var(--${inversion}-color); }`,
            ];
          });
          styles.push(...strokeStyles);

          // color
          const colorStyles = info.colors.color.flatMap((code) => {
            const inversion = toInverse(code);
            return [
              `:target div[data-drawio-colors*=\"color: ${code}\"] div { color: var(--${inversion}-color) !important; }`,
            ];
          });
          styles.push(...colorStyles);

          // border-color
          const borderColorStyles = info.colors.borderColor.flatMap((code) => {
            const inversion = toInverse(code);
            return [
              `:target div[data-drawio-colors*=\"border-color: ${code}\"] { border-color: var(--${inversion}-color) !important; }`,
              `:target div[data-drawio-colors*=\"border-color: ${code}\"] div { border-color: var(--${inversion}-color) !important; }`,
            ];
          });
          styles.push(...borderColorStyles);

          // background-color
          const backgroundColorStyles = info.colors.backgroundColor.flatMap(
            (code) => {
              const inversion = toInverse(code);
              return [
                `:target div[data-drawio-colors*=\"background-color: ${code}\"] { background-color: var(--${inversion}-color) !important; }`,
                `:target div[data-drawio-colors*=\"background-color: ${code}\"] div { background-color: var(--${inversion}-color) !important; }`,
              ];
            }
          );
          styles.push(...backgroundColorStyles);

          // console.log({ styles: styles });

          const defs = {
            type: "element",
            name: "defs",
            attributes: { id: "styles" },
            children: [
              {
                type: "element",
                name: "style",
                attributes: { type: "text/css" },
                children: [
                  {
                    type: "text",
                    value: styles.join(""),
                  },
                ],
              },
            ],
          };

          node.children.unshift(defs);
          info.hasTheme = true;
        }
      },
    },
  };
}
