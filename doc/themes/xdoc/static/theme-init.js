// init theme and assign it to HTML
(() => {
  const LIGHT = "light";
  const DARK = "dark";

  const THEME_ATTR = "data-theme";

  const initTheme = localStorage.getItem(THEME_ATTR);
  const isPreferDark = window.matchMedia("(prefers-color-scheme: dark)").matches;

  const effectiveTheme = (initTheme === LIGHT || (!initTheme && !isPreferDark)) ? LIGHT : DARK;

  document.querySelector("html").setAttribute(THEME_ATTR, effectiveTheme);
  localStorage.setItem(THEME_ATTR, effectiveTheme);
})();
