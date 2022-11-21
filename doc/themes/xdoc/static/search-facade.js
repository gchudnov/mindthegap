// https://developer.mozilla.org/en-US/docs/Web/HTML/Link_types/preload
(() => {
  window.onload = () => {
    const searchInput = document.getElementById("search-input");

    searchInput.addEventListener("focus", () => {
      let baseUrl = document.querySelector("base").getAttribute("href");

      if (baseUrl.slice(-1) == "/") {
        baseUrl = baseUrl.slice(0, -1);
      }

      const searchScript = document.createElement("script");
      searchScript.src = baseUrl + "/search-bundle.min.js?h=gABWFPaF73I1E3fbVcHxIP7sYRMEEV0HdCRn1C81gsY=";
      searchScript.setAttribute("integrity", "sha384-NPUoiqvZTkzh2WOWYkYSSQkcZ6g89XSSOoVP5Eh8bUFr3H6TeM2508g2KtlM4rle");

      document.head.appendChild(searchScript);
    }, { once: true });

    document.addEventListener("keydown", (e) => {
      const activeElement = document.activeElement;

      if (e.code === "Slash" && activeElement.tagName !== "INPUT" && activeElement.tagName !== "TEXTAREA") {
        e.preventDefault();
        searchInput.focus();
      }

      return false;
    });
  };
})();
