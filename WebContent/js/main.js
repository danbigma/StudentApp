/* global window, document, fetch */
(function() {
  function qs(sel){ return document.querySelector(sel); }
  function qsa(sel){ return Array.prototype.slice.call(document.querySelectorAll(sel)); }

  function setTheme(t) {
    var root = document.documentElement;
    root.setAttribute('data-theme', t);
    try { localStorage.setItem('theme', t); } catch (e) {}
    updateThemeToggle(t);
    try {
      if (window.StudentChart) {
        var css = getComputedStyle(document.documentElement);
        var color = (css.getPropertyValue('--primary') || '#111111').trim();
        window.StudentChart.data.datasets[0].backgroundColor = color;
        window.StudentChart.options.scales.x.ticks.color = color;
        window.StudentChart.options.scales.y.ticks.color = color;
        window.StudentChart.update('none');
      }
    } catch (e2) {}
  }

  function preferredTheme() {
    try {
      var t = localStorage.getItem('theme');
      if (t === 'dark' || t === 'light') return t;
    } catch (e) {}
    try {
      return (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches)
        ? 'dark' : 'light';
    } catch (e2) { return 'light'; }
  }

  function updateThemeToggle(t) {
    var btn = qs('[data-theme-toggle]');
    if (!btn) return;
    btn.setAttribute('aria-label', t === 'dark' ? 'Switch to light theme' : 'Switch to dark theme');
    btn.textContent = t === 'dark' ? 'Light' : 'Dark';
  }

  function initTheme() {
    setTheme(preferredTheme());
    var btn = qs('[data-theme-toggle]');
    if (!btn) return;
    btn.addEventListener('click', function(){
      var current = document.documentElement.getAttribute('data-theme') || 'light';
      setTheme(current === 'dark' ? 'light' : 'dark');
    });
  }

  function initNavActive() {
    var meta = document.querySelector('meta[name=\"current-route\"]');
    var current = meta ? meta.getAttribute('content') : null;
    if (!current || current.trim() === '') return;
    var links = qsa('.js-nav-route');
    links.forEach(function(a){ a.classList.remove('active'); });
    links.forEach(function(a){
      var r = a.getAttribute('data-route');
      if (r === current) a.classList.add('active');
    });
  }

  function setBadgeClasses(el, up) {
    if (!el) return;
    el.classList.remove('success', 'danger');
    el.classList.add('badge');
    el.classList.add(up ? 'success' : 'danger');
    el.textContent = up ? 'UP' : 'DOWN';
  }

  function setBadgeForSelector(selector, up) {
    var list = document.querySelectorAll(selector);
    if (!list || !list.length) return;
    list.forEach(function(el){ setBadgeClasses(el, up); });
  }

  function getContext() {
    var m = document.querySelector('meta[name="app-context"]');
    return (m && m.getAttribute('content')) || '';
  }

  function updateHealth() {
    var base = getContext() + '/health';
    fetch(base, { credentials: 'same-origin' })
      .then(function(r) { return r.json(); })
      .then(function(json) {
        setBadgeForSelector('.js-health', json && json.status === 'UP');
        var dbUp = json && json.db && json.db.status === 'UP';
        setBadgeForSelector('.js-db', !!dbUp);
      })
      .catch(function() {
        setBadgeForSelector('.js-health', false);
        setBadgeForSelector('.js-db', false);
      });
  }

  function initStudentSearch() {
    var input = document.getElementById('studentSearch');
    var inputTop = document.getElementById('studentSearchTop');
    var table = document.getElementById('studentsTable');
    var pager = document.getElementById('studentsPagination');
    var pageSizeSel = document.getElementById('studentsPageSize');
    if (!table) return;

    var state = { q: '', sortKey: null, sortDir: 'asc', page: 1, pageSize: 10 };
    if (pageSizeSel) state.pageSize = parseInt(pageSizeSel.value || '10', 10);

    function rows() { return Array.prototype.slice.call(table.querySelectorAll('tbody tr')); }

    function rowKey(tr, key) {
      if (key === 'first') return (tr.children[0].textContent || '').toLowerCase();
      if (key === 'last') return (tr.children[1].textContent || '').toLowerCase();
      if (key === 'email') return (tr.children[2].textContent || '').toLowerCase();
      return tr.textContent.toLowerCase();
    }

    function apply() {
      var all = rows();
      var filtered = state.q ? all.filter(function(tr){ return tr.textContent.toLowerCase().indexOf(state.q) >= 0; }) : all;
      if (state.sortKey) {
        filtered.sort(function(a,b){
          var ak = rowKey(a, state.sortKey);
          var bk = rowKey(b, state.sortKey);
          if (ak < bk) return state.sortDir === 'asc' ? -1 : 1;
          if (ak > bk) return state.sortDir === 'asc' ? 1 : -1;
          return 0;
        });
      }
      var total = filtered.length;
      var pageSize = state.pageSize;
      var pages = Math.max(1, Math.ceil(total / pageSize));
      if (state.page > pages) state.page = pages;
      var start = (state.page - 1) * pageSize;
      var end = start + pageSize;

      rows().forEach(function(tr){ tr.style.display = 'none'; });
      filtered.slice(start, end).forEach(function(tr){ tr.style.display = ''; });
      renderPager(pages);
      renderSortIndicators();
      updateStudentsChart(table);
    }

    function renderPager(pages) {
      if (!pager) return;
      var html = '';
      function li(p, label, disabled, active) {
        html += '<li class="page-item' + (disabled? ' disabled':'') + (active?' active':'') + '">'
             + '<a class="page-link" href="#" data-page="' + p + '">' + label + '</a></li>';
      }
      li(Math.max(1, state.page-1), '&laquo;', state.page===1, false);
      for (var i=1;i<=pages;i++) li(i, String(i), false, i===state.page);
      li(Math.min(pages, state.page+1), '&raquo;', state.page===pages, false);
      pager.innerHTML = html;
    }

    function renderSortIndicators() {
      var ths = table.querySelectorAll('thead th[data-sort]');
      ths.forEach(function(th){
        var key = th.getAttribute('data-sort');
        th.style.cursor = 'pointer';
        var label = th.textContent.replace(/[\u25B2\u25BC]/g,'').trim();
        if (state.sortKey === key) {
          label += state.sortDir === 'asc' ? ' \u25B2' : ' \u25BC';
        }
        th.textContent = label;
      });
    }

    if (input) {
      input.addEventListener('input', function(){ state.q = (input.value||'').toLowerCase(); state.page = 1; apply(); });
    }
    if (inputTop) {
      inputTop.addEventListener('input', function(){ state.q = (inputTop.value||'').toLowerCase(); state.page = 1; apply(); });
    }
    if (pageSizeSel) {
      pageSizeSel.addEventListener('change', function(){ state.pageSize = parseInt(pageSizeSel.value,10)||10; state.page=1; apply(); });
    }
    var thead = table.querySelector('thead');
    if (thead) {
      thead.addEventListener('click', function(e){
        var th = e.target.closest('th[data-sort]');
        if (!th) return;
        var key = th.getAttribute('data-sort');
        if (state.sortKey === key) {
          state.sortDir = state.sortDir === 'asc' ? 'desc' : 'asc';
        } else {
          state.sortKey = key; state.sortDir = 'asc';
        }
        apply();
      });
    }
    if (pager) {
      pager.addEventListener('click', function(e){
        var a = e.target.closest('a[data-page]');
        if (!a) return;
        e.preventDefault();
        var p = parseInt(a.getAttribute('data-page'),10);
        if (!isNaN(p)) { state.page = p; apply(); }
      });
    }

    apply();
    try { window.StudentTable = { apply: apply }; } catch (e) {}
  }

  function initBulkDeletePage() {
    var form = document.querySelector('form[action="deletestudents"], form[action$="/deletestudents"]');
    if (!form) return;
    var selectAll = form.querySelector('input[name="students"]');
    var inputs = form.querySelectorAll('input[name="student"]');
    var btn = form.querySelector('#buttonDelete');
    var search = document.getElementById('bulkSearch');

    function enableButton() {
      var any = false, count = 0;
      inputs.forEach(function(ch){ if (ch.checked) { any = true; count++; } });
      if (btn) btn.disabled = !any;
      var badge = document.getElementById('selectedCountBadge');
      if (badge) { badge.textContent = (count || 0) + ' selected'; badge.className = 'badge ' + (count ? 'success' : ''); }
    }
    function toggleAll() {
      var checked = !!(selectAll && selectAll.checked);
      inputs.forEach(function(ch){ ch.checked = checked; });
      enableButton();
    }
    if (selectAll) selectAll.addEventListener('change', toggleAll);
    inputs.forEach(function(ch){ ch.addEventListener('click', enableButton); });
    enableButton();

    if (search) {
      search.addEventListener('input', function(){
        var q = (search.value || '').toLowerCase();
        var rows = form.querySelectorAll('tbody tr');
        rows.forEach(function(tr){
          var text = tr.textContent.toLowerCase();
          tr.style.display = text.indexOf(q) >= 0 ? '' : 'none';
        });
      });
    }
  }

  function confirmDialog(message) {
    return Promise.resolve(window.confirm(message || 'Are you sure?'));
  }

  function initConfirmLinks() {
    document.body.addEventListener('click', function(e) {
      var a = e.target.closest('a[data-confirm]');
      if (!a) return;
      var msg = a.getAttribute('data-confirm');
      if (!window.confirm(msg || 'Are you sure?')) {
        e.preventDefault();
      }
    });
  }

  document.addEventListener('submit', function(e){
    var f = e.target.closest('form[data-confirm]');
    if (!f) return;
    e.preventDefault();
    var msg = f.getAttribute('data-confirm');
    confirmDialog(msg).then(function(ok){ if (ok) f.submit(); });
  });

  function showToast(message, type) {
    var container = document.getElementById('toastContainer');
    if (!container) return;
    var toast = document.createElement('div');
    toast.className = 'toast ' + (type || '');
    toast.textContent = message;
    container.appendChild(toast);
    setTimeout(function(){ toast.remove(); }, 3200);
  }

  function initToastsFromFlash() {
    var el = document.getElementById('flashData');
    if (!el) return;
    var success = el.getAttribute('data-success');
    var error = el.getAttribute('data-error');
    if (success && success !== 'null' && success.trim() !== '') showToast(success, 'success');
    if (error && error !== 'null' && error.trim() !== '') showToast(error, 'danger');
  }

  function initAjaxDelete() {
    document.addEventListener('submit', function(e){
      var form = e.target.closest('form.js-delete-student');
      if (!form) return;
      e.preventDefault();
      confirmDialog('Delete this student?').then(function(ok){
        if (!ok) return;
        var fd = new FormData(form);
        var url = form.getAttribute('action') || form.action;
        var meta = document.querySelector('meta[name="csrf-token"]');
        var csrf = meta ? meta.getAttribute('content') : null;
        fetch(url, {
          method: 'POST',
          credentials: 'same-origin',
          headers: (function(){
            var h = { 'X-Requested-With': 'XMLHttpRequest', 'Accept': 'application/json' };
            if (csrf) h['X-CSRF-Token'] = csrf;
            return h;
          })(),
          body: fd
        })
        .then(function(r){
          var ct = r.headers.get('content-type') || '';
          if (ct.indexOf('application/json') >= 0) return r.json().then(function(j){ return { ok: r.ok, json: j }; });
          return r.text().then(function(){ return { ok: r.ok, json: null }; });
        })
        .then(function(res){
          var success = (res.json && res.json.ok) || (!res.json && res.ok);
          if (success) {
            var id = form.getAttribute('data-id');
            var row = document.getElementById('student-row-' + id);
            if (row) row.parentNode.removeChild(row);
            showToast('Student deleted', 'success');
            if (window.StudentTable && typeof window.StudentTable.apply === 'function') {
              try { window.StudentTable.apply(); } catch (e) {}
            }
          } else {
            showToast('Delete failed', 'danger');
          }
        })
        .catch(function(){ showToast('Delete failed', 'danger'); });
      });
    });
  }

  function openModal(id) {
    var modal = document.getElementById(id);
    if (!modal) return;
    modal.classList.add('open');
    modal.setAttribute('aria-hidden', 'false');
  }
  function closeModal(modal) {
    if (!modal) return;
    modal.classList.remove('open');
    modal.setAttribute('aria-hidden', 'true');
  }

  function initModals() {
    document.addEventListener('click', function(e){
      var openBtn = e.target.closest('[data-modal-open]');
      if (openBtn) {
        var id = openBtn.getAttribute('data-modal-open');
        openModal(id);
        return;
      }
      var closeBtn = e.target.closest('[data-modal-close]');
      if (closeBtn) {
        var m = closeBtn.closest('.modal');
        closeModal(m);
        return;
      }
      var backdrop = e.target.classList.contains('modal-backdrop') ? e.target : null;
      if (backdrop) {
        var mm = backdrop.closest('.modal');
        closeModal(mm);
      }
    });
  }

  function initEditStudentModal() {
    document.addEventListener('click', function(e){
      var btn = e.target.closest('[data-modal-open="editStudentModal"]');
      if (!btn) return;
      var id = btn.getAttribute('data-id');
      var first = btn.getAttribute('data-first');
      var last = btn.getAttribute('data-last');
      var email = btn.getAttribute('data-email');
      var setVal = function(id, val){ var el = document.getElementById(id); if (el) el.value = val || ''; };
      setVal('editStudentId', id);
      setVal('editFirstName', first);
      setVal('editLastName', last);
      setVal('editEmail', email);
    });
  }

  function updateStudentsChart(table) {
    var canvas = document.getElementById('studentsChart');
    if (!canvas || !(window.Chart)) return;
    var emails = [];
    var rows = table.querySelectorAll('tbody tr');
    Array.prototype.forEach.call(rows, function(tr){
      if (tr.style.display === 'none') return;
      var cell = tr.children[2];
      if (cell) emails.push((cell.textContent||'').trim());
    });
    var counts = {};
    emails.forEach(function(e){
      var m = e.split('@');
      var d = m.length>1 ? m[1].toLowerCase() : 'unknown';
      counts[d] = (counts[d]||0)+1;
    });
    var labels = Object.keys(counts);
    var data = labels.map(function(k){ return counts[k]; });
    var color = '#111111';
    try {
      var css = getComputedStyle(document.documentElement);
      color = (css.getPropertyValue('--primary') || '#111111').trim();
    } catch (e) {}
    if (!window.StudentChart) {
      window.StudentChart = new Chart(canvas.getContext('2d'), {
        type: 'bar',
        data: { labels: labels, datasets: [{ label: 'By domain', data: data, backgroundColor: color }] },
        options: {
          responsive: true,
          plugins: { legend: { display: false } },
          scales: {
            y: { beginAtZero: true, ticks: { color: color }, grid: { color: 'rgba(127,127,127,0.2)' } },
            x: { ticks: { color: color }, grid: { color: 'rgba(127,127,127,0.12)' } }
          }
        }
      });
    } else {
      var c = window.StudentChart; c.data.labels = labels; c.data.datasets[0].data = data; c.update('none');
    }
  }

  function initCopyRequestId() {
    var btn = document.getElementById('copyRequestIdBtn');
    var target = document.getElementById('requestIdVal');
    if (!btn || !target) return;
    btn.addEventListener('click', function(){
      var text = target.textContent || target.innerText;
      if (!navigator.clipboard) {
        var ta = document.createElement('textarea');
        ta.value = text; document.body.appendChild(ta); ta.select();
        try { document.execCommand('copy'); } catch (e) {}
        document.body.removeChild(ta);
        return;
      }
      navigator.clipboard.writeText(text).catch(function(){});
    });
  }

  document.addEventListener('DOMContentLoaded', function() {
    initTheme();
    initNavActive();
    updateHealth();
    setInterval(updateHealth, 15000);
    initStudentSearch();
    initConfirmLinks();
    initBulkDeletePage();
    initToastsFromFlash();
    initAjaxDelete();
    initModals();
    initEditStudentModal();
    initCopyRequestId();
    var table = document.getElementById('studentsTable');
    if (table) updateStudentsChart(table);
  });
})();
