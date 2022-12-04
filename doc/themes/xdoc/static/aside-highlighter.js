// Aside Highlighter
(() => {
  const LEVELS = [2, 3];

  const LINKS_QUERY_SELECTOR = "aside > nav a";
  const NAVBAR_QUERY_SELECTOR = "header > nav";
  const LINK_ACTIVE_CLASS_NAME = "active";

  let lastActiveLinkRef = null;

  function getLinks() {
    return Array.from(document.querySelectorAll(LINKS_QUERY_SELECTOR));
  }

  function getLinkAnchorValue(link) {
    return decodeURIComponent(link.href.substring(link.href.indexOf("#") + 1));
  }

  function getAnchors() {
    const selectors = LEVELS.reduce((acc, it) => {
      acc.push(`h${it}`);
      return acc;
    }, []);

    return Array.from(document.querySelectorAll(selectors.join(",")));
  }

  function getNavHeight() {
    return document.querySelector(NAVBAR_QUERY_SELECTOR).clientHeight;
  }

  function getClientRect(el) {
    const rect = el.getBoundingClientRect();
    return rect.top === rect.bottom ? getClientRect(el.parentNode) : rect;
  }

  function isInView(rect) {
    return rect.top > 0 && rect.bottom < window.innerHeight / 2;
  }

  function getActiveAnchor(anchors) {
    if (anchors.length == 0) {
      return null;
    }

    const navHeight = getNavHeight();

    const nextAnchor = anchors.find((anchor) => {
      const boundingRect = getClientRect(anchor);
      return boundingRect.top >= navHeight;
    });

    if (!nextAnchor) {
      return anchors[anchors.length - 1];
    }

    const anchorRect = getClientRect(nextAnchor);
    if (isInView(anchorRect)) {
      return nextAnchor;
    }

    const index = anchors.indexOf(nextAnchor);
    const effectiveIndex = index > 0 ? index - 1 : 0;
    return anchors[effectiveIndex];
  }

  function updateLinkActiveClass(link, isActive) {
    if (isActive) {
      if (lastActiveLinkRef && lastActiveLinkRef !== link) {
        lastActiveLinkRef.classList.remove(LINK_ACTIVE_CLASS_NAME);
      }

      link.classList.add(LINK_ACTIVE_CLASS_NAME);
      lastActiveLinkRef = link;
    } else {
      link.classList.remove(LINK_ACTIVE_CLASS_NAME);
    }
  }

  function updateActiveLink() {
    const links = getLinks();
    const anchors = getAnchors();
    const activeAnchor = getActiveAnchor(anchors);

    const activeLink = links.find((link) => activeAnchor && activeAnchor.id === getLinkAnchorValue(link));

    links.forEach((link) => {
      updateLinkActiveClass(link, link === activeLink);
    });
  }

  document.addEventListener("scroll", updateActiveLink);
  document.addEventListener("resize", updateActiveLink);

  updateActiveLink();
})();
