// Pre-apply Bootstrap color mode and primary palette before CSS
(function () {
  try {
    var d = document.documentElement;
    var t = localStorage.getItem('bs-theme');
    if (!t) {
      try {
        t = (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches)
          ? 'dark' : 'light';
      } catch (e) { t = 'light'; }
    }
    if (t === 'dark') d.setAttribute('data-bs-theme', 'dark');
    var c = localStorage.getItem('bs-color');
    if (c && c !== 'blue') d.setAttribute('data-color', c);
  } catch (e) {}
})();

