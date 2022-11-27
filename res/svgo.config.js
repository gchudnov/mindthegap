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
        }
      }
    },
    "cleanupListOfValues",
    "removeDimensions",
    "sortAttrs",
    {
      name: 'addAttributesToSVGElement',
      params: {
        attribute: { "id": "dark" }
      }
    },
    {
      name: 'normalizeColors',
      type: 'visitor',
      params: {
        optionName: 'optionValue',
      },
      fn: (ast, params, info) => {
        return normalizeColors(ast, params, info);
      },
    },
  ]
};

/**
 * Plugin to normalize colors
 * 
 * rgb(a, b, c) => #rrggbb
 * #rgb         => #rrggbb
 * #rrggbb      => #rrggbb
 */
function normalizeColors(ast, params, info) {

  const rNumber = '([+-]?(?:\\d*\\.\\d+|\\d+\\.?)%?)';
  const rComma = '\\s*,\\s*';
  const rRGB = new RegExp('rgb\\(\\s*' + rNumber + rComma + rNumber + rComma + rNumber + '\\s*\\)');

  const xRGB = /#([a-fA-F0-9])([a-fA-F0-9])([a-fA-F0-9])\b/;

  /**
   * Convert [r, g, b] to #rrggbb.
   */
  const rgbToHex = (r, g, b) => {
    const hexNumber = ((r << 16) | (g << 8) | b);
    const hexStr = hexNumber.toString(16).toUpperCase().padStart(6, '0');
    return '#' + hexStr;
  };

  return {
    element: {
      enter: (node, parentNode) => {
        Object.entries(node.attributes).reduce((acc, [name, value]) => {

          // #rgb -> #rrggbb
          value = value.replace(xRGB, (match, r, g, b) => {
            return '#' + r.repeat(2) + g.repeat(2) + b.repeat(2);
          });

          // rgb(a, b, c) => #rrggbb
          value = value.replace(rRGB, (match, r, g, b) => {
            return rgbToHex(r, g, b);
          });

          acc[name] = value;
          return acc;
        }, {})
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