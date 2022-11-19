module.exports = {
  plugins: [
    {
      name: "preset-default",
      params: {
        overrides: {
          removeViewBox: false
        }
      }
    },
    "cleanupListOfValues",
    "convertStyleToAttrs",
    "removeDimensions",
    "sortAttrs"
  ]
};
