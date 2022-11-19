// initialization for auto-render Extension, auto-render.min.js
// renderMathInElement function will recursively search for text nodes inside this element and render the math in them.
// see: https://katex.org/docs/autorender.html for details

!function () {
  document.addEventListener("DOMContentLoaded", () => {
    renderMathInElement(document.body, {
      delimiters: [
          {left: '$$', right: '$$', display: true},
          {left: '$', right: '$', display: false},
          {left: '\\(', right: '\\)', display: false},
          {left: '\\[', right: '\\]', display: true}
      ],
      throwOnError: true
    });
  });
}();
