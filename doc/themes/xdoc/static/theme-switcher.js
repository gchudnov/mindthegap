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

  document.getElementById("theme-switcher").addEventListener("click", () => {
    const curTheme = getHtmlTheme() == LIGHT ? DARK : LIGHT;

    storeTheme(curTheme);
    setHtmlTheme(curTheme);
  });
})();
