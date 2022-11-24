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
    "removeDimensions",
    "sortAttrs"
  ]
};
