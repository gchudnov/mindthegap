/**
 * Scroll-To-Top
 */
(() => {
  const button = document.querySelector(".scroll-top");

  function updateScrollToTop() {
    const isButtonVisible = window.pageYOffset > window.innerHeight;
    if (isButtonVisible) {
      button.style.display = "block";
    } else {
      button.style.display = "none";
    }
  }

  // add handler only if the button is present
  const hasButton = !!button;
  if (hasButton) {
    document.addEventListener("scroll", updateScrollToTop);

    updateScrollToTop();
  }
})();
