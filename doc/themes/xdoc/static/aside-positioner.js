// Aside Positioner - when Aside is too high, it makes sure the bottom part is visible when scrolled to the bottom
(() => {
  const docEl = document.querySelector("body > main > section > div[role='document']");
  const navEl = document.querySelector("body > main > section > aside > nav");

  const navHeight = navEl.clientHeight;
  const docRc = getClientRect(docEl);

  let prevY = null;

  function getClientRect(el) {
    const rect = el.getBoundingClientRect();
    return rect.top === rect.bottom ? getClientRect(el.parentNode) : rect;
  }

  function updateAsideYOffset() {
    let asideY = null;
    if (window.scrollY >= docRc.bottom - navHeight) {
      asideY = docRc.bottom - navHeight;
    }

    if (prevY != asideY) {
      navEl.style.top = asideY ? `${asideY}px` : null;
      navEl.style.position = asideY ? "relative" : null;

      prevY = asideY;
    } else {
      // no-op
    }
  }

  document.addEventListener("scroll", updateAsideYOffset);
  document.addEventListener("resize", updateAsideYOffset);

  updateAsideYOffset();
})();
