// based on: https://aaronluna.dev/blog/add-copy-button-to-code-blocks-hugo-chroma/

(() => {
  function createCopyButton(highlightDiv) {
    const button = document.createElement("button");
    button.className = "code-copy secondary";
    button.type = "button";
    button.innerText = "";
    button.addEventListener("click", () => copyCodeToClipboard(button, highlightDiv));

    addCopyButtonToDom(button, highlightDiv);
  }

  function addCopyButtonToDom(button, highlightDiv) {
    const wrapper = document.createElement("div");
    wrapper.className = "code-copy-wrapper";
    wrapper.appendChild(button);
    highlightDiv.parentNode.insertBefore(wrapper, highlightDiv);
    wrapper.appendChild(highlightDiv);
  }

  async function copyCodeToClipboard(button, highlightDiv) {
    const codeToCopy = selectCode(highlightDiv);

    try {
      result = await navigator.permissions.query({ name: "clipboard-write" });
      if (result.state == "granted" || result.state == "prompt") {
        await navigator.clipboard.writeText(codeToCopy);
      } else {
        copyCodeBlockExecCommand(codeToCopy, highlightDiv);
      }
    } catch (_) {
      copyCodeBlockExecCommand(codeToCopy, highlightDiv);
    } finally {
      codeWasCopied(button);
    }
  }

  function selectCode(highlightDiv) {
    const { length } = highlightDiv.querySelectorAll("table");
    let codeToCopy = "";
    if (length > 0) {
      const items = highlightDiv.querySelectorAll(":last-child > tr > td:last-child");
      items.forEach((item) => {
        codeToCopy = codeToCopy + item.innerText;
      });
    } else {
      codeToCopy = highlightDiv.querySelector(":last-child > code").innerText;
    }
    return codeToCopy;
  }

  function copyCodeBlockExecCommand(codeToCopy, highlightDiv) {
    const textArea = document.createElement("textArea");
    textArea.contentEditable = "true";
    textArea.readOnly = "false";
    textArea.className = "copyable-text-area";
    textArea.value = codeToCopy;
    highlightDiv.insertBefore(textArea, highlightDiv.firstChild);
    const range = document.createRange();
    range.selectNodeContents(textArea);
    const sel = window.getSelection();
    sel.removeAllRanges();
    sel.addRange(range);
    textArea.setSelectionRange(0, 999999);
    document.execCommand("copy");
    highlightDiv.removeChild(textArea);
  }

  function codeWasCopied(button) {
    button.blur();
    if (!button.classList.contains("ok")) {
      button.classList.add("ok");
    }

    setTimeout(() => {
      button.classList.remove("ok");
    }, 2000);
  }

  document.querySelectorAll("pre").forEach(createCopyButton);
})();
