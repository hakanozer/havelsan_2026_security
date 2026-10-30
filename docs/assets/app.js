"use strict";

const themeToggle = document.querySelector("[data-theme-toggle]");
if (themeToggle) {
  themeToggle.addEventListener("click", () => {
    const root = document.documentElement;
    const nextTheme = root.dataset.theme === "dark" ? "light" : "dark";
    root.dataset.theme = nextTheme;
    themeToggle.setAttribute("aria-pressed", String(nextTheme === "dark"));
    themeToggle.textContent = nextTheme === "dark" ? "Açık temaya geç" : "Koyu temaya geç";
  });
}

const topicSearch = document.querySelector("#topic-search");
if (topicSearch) {
  const cards = [...document.querySelectorAll("[data-topic-card]")];
  const noResults = document.querySelector("#no-results");

  topicSearch.addEventListener("input", () => {
    const query = topicSearch.value.trim().toLocaleLowerCase("tr-TR");
    let visibleCount = 0;

    for (const card of cards) {
      const searchableText = card.textContent.toLocaleLowerCase("tr-TR");
      const matches = searchableText.includes(query);
      card.hidden = !matches;
      if (matches) visibleCount += 1;
    }

    if (noResults) noResults.hidden = visibleCount !== 0;
  });
}

for (const button of document.querySelectorAll("[data-copy]")) {
  button.addEventListener("click", () => {
    const target = document.querySelector(button.dataset.copy);
    const status = button.parentElement.querySelector("[data-copy-status]");

    if (!target || !status) {
      return;
    }

    const text = target.innerText;
    const report = (success) => {
      status.textContent = success ? "Kod kopyalandı." : "Kopyalama başarısız; kodu seçip kopyalayın.";
    };
    const fallbackCopy = () => {
      const selection = document.createElement("textarea");
      selection.value = text;
      selection.setAttribute("readonly", "");
      selection.style.position = "fixed";
      selection.style.opacity = "0";
      document.body.append(selection);
      selection.select();
      const copied = document.execCommand("copy");
      selection.remove();
      report(copied);
    };

    if (navigator.clipboard && window.isSecureContext) {
      navigator.clipboard.writeText(text).then(
        () => report(true),
        fallbackCopy
      );
    } else {
      fallbackCopy();
    }
  });
}

for (const button of document.querySelectorAll("[data-print]")) {
  button.addEventListener("click", () => window.print());
}
