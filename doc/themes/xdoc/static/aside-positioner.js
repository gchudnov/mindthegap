// Aside Positioner - when Aside is too high, it makes sure the bottom part is visible when scrolled to the bottom
(() => {
  const docEl = document.querySelector("body > main > section > div[role='document']");
  const asideEl = document.querySelector("body > main > section > aside");

  const asideTop = asideEl ? getClientRect(asideEl).top + window.scrollY : 0;
  const navEl = asideEl ? asideEl.querySelector("nav") : null;

  let prevY = null;

  function getClientRect(el) {
    const rect = el.getBoundingClientRect();
    return rect.top === rect.bottom ? getClientRect(el.parentNode) : rect;
  }

  function updateAsideYOffset() {
    const docRc = getClientRect(docEl);

    const navHeight = navEl ? navEl.clientHeight : 0;
    const docBottom = docRc.bottom + window.scrollY;

    let asideY = null;
    if (window.scrollY + navHeight + asideTop >= docBottom) {
      asideY = docBottom - navHeight - asideTop;
    }

    if (navEl && prevY != asideY) {
      navEl.style.top = asideY ? `${asideY}px` : null;
      navEl.style.position = asideY ? "relative" : null;

      prevY = asideY;
    }
  }

  if (navEl) {
    document.addEventListener("scroll", updateAsideYOffset);
    document.addEventListener("resize", updateAsideYOffset);

    updateAsideYOffset();
  }
})();
