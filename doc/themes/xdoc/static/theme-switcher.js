// theme switcher
(() => {
  const LIGHT = "light";
  const DARK = "dark";

  const THEME_ATTR = "data-theme";

  function setHtmlTheme(value) {
    document.querySelector("html").setAttribute(THEME_ATTR, value);
  }

  function getHtmlTheme() {
    return document.querySelector("html").getAttribute(THEME_ATTR);
  }

  function storeTheme(value) {
    localStorage.setItem(THEME_ATTR, value);
  }

  function styleSvg(theme) {
    document.querySelectorAll("img").forEach((img) => {
      if (img.src.includes(".svg")) {
        if (theme === DARK) {
          if (!img.src.endsWith("#dark")) {
            img.src += "#dark";
          }
        } else {
          if (img.src.endsWith("#dark")) {
            img.src = img.src.substring(0, img.src.indexOf("#dark"));
          }
        }
      }
    });
  }

  document.getElementById("theme-switcher").addEventListener("click", () => {
    const curTheme = getHtmlTheme() == LIGHT ? DARK : LIGHT;

    storeTheme(curTheme);
    setHtmlTheme(curTheme);
    styleSvg(curTheme);
  });

  styleSvg(getHtmlTheme());
})();
