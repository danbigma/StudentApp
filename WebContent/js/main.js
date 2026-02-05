/* global window, document, fetch */
(function() {
  // Loader gating state
  window.AppReady = { dom: false, dtPending: 0 };
  function addDtWait(){ try { window.AppReady.dtPending++; } catch (e) {} }
  function markDtReady(){ try { if (window.AppReady.dtPending>0) window.AppReady.dtPending--; } catch (e) {} tryHide(); }
  function tryHide(){ try { if (window.AppReady.dom && window.AppReady.dtPending === 0) hideLoader(); } catch (e) {} }
  function setBadgeClasses(el, up) {
    if (!el) return;
    el.classList.remove('bg-secondary', 'bg-success', 'bg-danger');
    if (!el.classList.contains('badge')) el.classList.add('badge');
    el.classList.add(up ? 'bg-success' : 'bg-danger');
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

    // If DataTables is available, use it for a richer UX and skip custom logic
    if (window.jQuery && jQuery.fn && typeof jQuery.fn.DataTable === 'function') {
      var $table = jQuery(table);
      var btnClass = (document.documentElement.getAttribute('data-bs-theme') === 'dark')
        ? 'btn btn-sm btn-light'
        : 'btn btn-sm btn-outline-secondary';
      try { addDtWait(); $table.on('init.dt', function(){ markDtReady(); }); } catch (e) {}
      var dt = $table.DataTable({
        paging: true,
        pagingType: 'simple_numbers',
        searching: true,
        info: false,
        lengthChange: false,
        pageLength: pageSizeSel ? parseInt(pageSizeSel.value||'10',10) : 10,
        dom: 'Bfrtip',
        buttons: [
          { extend: 'copy', className: btnClass },
          { extend: 'csv', className: btnClass },
          { extend: 'excel', className: btnClass },
          { extend: 'pdf', className: btnClass },
          { extend: 'print', className: btnClass }
        ],
        order: [],
        columnDefs: [
          { targets: -1, orderable: false }
        ]
      });
      try {
        var host = $table.closest('.card').find('.card-body').first();
        dt.buttons().container().addClass('mb-2').appendTo(host.length ? host : $table.closest('div'));
      } catch (e) {}
      try { setTimeout(markDtReady, 0); } catch (e2) {}
      if (input) input.addEventListener('input', function(){ dt.search(input.value).draw(); });
      if (inputTop) inputTop.addEventListener('input', function(){ dt.search(inputTop.value).draw(); });
      if (pageSizeSel) pageSizeSel.addEventListener('change', function(){ dt.page.len(parseInt(pageSizeSel.value,10)||10).draw(); });
      // Keep native select styling (Bootstrap 5)
      // expose for external triggers
      try { window.StudentTable = { apply: function(){ dt.draw(false); updateStudentsChart(table, dt); } }; } catch (e) {}
      // Update chart on draw
      $table.on('draw.dt', function(){ updateStudentsChart(table, dt); });
      // Initial chart
      updateStudentsChart(table, dt);
      return; // stop here, DataTables handles the rest
    }

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
      // filter
      var filtered = state.q ? all.filter(function(tr){ return tr.textContent.toLowerCase().indexOf(state.q) >= 0; }) : all;
      // sort
      if (state.sortKey) {
        filtered.sort(function(a,b){
          var ak = rowKey(a, state.sortKey);
          var bk = rowKey(b, state.sortKey);
          if (ak < bk) return state.sortDir === 'asc' ? -1 : 1;
          if (ak > bk) return state.sortDir === 'asc' ? 1 : -1;
          return 0;
        });
      }
      // pagination
      var total = filtered.length;
      var pageSize = state.pageSize;
      var pages = Math.max(1, Math.ceil(total / pageSize));
      if (state.page > pages) state.page = pages;
      var start = (state.page - 1) * pageSize;
      var end = start + pageSize;

      // render
      rows().forEach(function(tr){ tr.style.display = 'none'; });
      filtered.slice(start, end).forEach(function(tr){ tr.style.display = ''; });
      renderPager(pages);
      renderSortIndicators();
      updateStudentsChart(table, null);
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
    // expose reapply for external triggers (e.g., after AJAX delete)
    try { window.StudentTable = { apply: apply }; } catch (e) {}
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

  // Promise-based confirm dialog using SweetAlert2 if available
  function confirmDialog(message) {
    return new Promise(function(resolve){
      if (window.Swal && Swal.fire) {
        Swal.fire({
          title: 'Are you sure?', text: message || '', icon: 'warning',
          showCancelButton: true, confirmButtonText: 'Yes', cancelButtonText: 'Cancel',
          focusCancel: true
        }).then(function(result){ resolve(!!result.isConfirmed); });
      } else {
        resolve(window.confirm(message || 'Are you sure?'));
      }
    });
  }

  function initCopyRequestId() {
    const btn = document.getElementById('copyRequestIdBtn');
    const target = document.getElementById('requestIdVal');
    if (!btn || !target) return;

    btn.addEventListener('click', () => {
      const text = target.textContent || target.innerText;
      if (!navigator.clipboard) {
        // Fallback para navegadores antiguos (API obsoleta)
        const ta = document.createElement('textarea');
        ta.value = text; document.body.appendChild(ta); ta.select();
        try { document.execCommand('copy'); } catch (e) {}
        document.body.removeChild(ta);
        return;
      }
      navigator.clipboard.writeText(text).catch(err => console.error('Error al copiar el texto: ', err));
    });
  }

  document.addEventListener('DOMContentLoaded', function() {
    updateHealth();
    setInterval(updateHealth, 15000);
    initStudentSearch();
    initConfirmLinks();
    initCopyRequestId();
    initBulkDeletePage();
    initNavActive();
    // mark DOM ready; loader hides when DTs (if any) finish
    try { window.AppReady.dom = true; } catch (e) {}
    initToastsFromFlash();
    initEditStudentModal();
    initAjaxDelete();
    initTooltips();
  });

window.addEventListener('load', function(){ tryHide(); });

  function initBulkDeletePage() {
    var form = document.querySelector('form[action="deletestudents"], form[action$="/deletestudents"]');
    if (!form) return;
    var table = document.getElementById('bulkTable') || form.querySelector('table');
    var selectAll = form.querySelector('input[name="students"]');
    var inputs = form.querySelectorAll('input[name="student"]');
    var btn = form.querySelector('#buttonDelete');
    var search = document.getElementById('bulkSearch');
    var pageSizeSel = document.getElementById('bulkPageSize');
    var dt = null;

    // Enhance with DataTables if available
    if (table && window.jQuery && jQuery.fn && typeof jQuery.fn.DataTable === 'function') {
      try { addDtWait(); jQuery(table).on('init.dt', function(){ markDtReady(); }); } catch (e) {}
      var btnClass = (document.documentElement.getAttribute('data-bs-theme') === 'dark')
        ? 'btn btn-sm btn-light'
        : 'btn btn-sm btn-outline-secondary';
      dt = jQuery(table).DataTable({
        paging: true,
        pagingType: 'simple_numbers',
        searching: true,
        info: false,
        lengthChange: false,
        pageLength: 10,
        dom: 'Bfrtip',
        buttons: [
          { extend: 'copy', className: btnClass },
          { extend: 'csv', className: btnClass },
          { extend: 'excel', className: btnClass },
          { extend: 'pdf', className: btnClass },
          { extend: 'print', className: btnClass }
        ],
        order: [],
        columnDefs: [ { targets: 0, orderable: false } ]
      });
      try {
        var host = jQuery(table).closest('.card').find('.card-header');
        dt.buttons().container().addClass('mb-0').appendTo(host.length ? host : jQuery(table).closest('div'));
      } catch (e) {}
      if (search) search.addEventListener('input', function(){ dt.search(search.value).draw(); });
      if (pageSizeSel) pageSizeSel.addEventListener('change', function(){ dt.page.len(parseInt(pageSizeSel.value,10)||10).draw(); });
      // When table redraws (paging/search), re-bind and update
      jQuery(table).on('draw.dt', function(){
        inputs = form.querySelectorAll('input[name="student"]');
        inputs.forEach(function(ch){ ch.addEventListener('click', enableButton); });
        enableButton();
        if (selectAll) selectAll.checked = false;
      });
      try { setTimeout(markDtReady, 0); } catch (e2) {}
    }
    function enableButton() {
      var any = false, count = 0;
      inputs.forEach(function(ch){ if (ch.checked) { any = true; count++; } });
      if (btn) btn.disabled = !any;
      var badge = document.getElementById('selectedCountBadge');
      if (badge) { badge.textContent = (count || 0) + ' selected'; badge.className = 'badge ' + (count ? 'text-bg-warning' : 'bg-secondary'); }
    }
    function toggleAll() {
      var checked = !!(selectAll && selectAll.checked);
      if (dt) {
        var nodes = dt.rows({ page: 'current' }).nodes();
        Array.prototype.forEach.call(nodes, function(row){
          var cb = row.querySelector('input[name="student"]');
          if (cb) cb.checked = checked;
        });
      } else {
        inputs.forEach(function(ch){ ch.checked = checked; });
      }
      enableButton();
    }
    if (selectAll) selectAll.addEventListener('change', toggleAll);
    inputs.forEach(function(ch){ ch.addEventListener('click', enableButton); });
    enableButton();

    // lightweight search filter on delete page
    if (search && !dt) {
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

  // Confirm handler for forms with data-confirm (CSP-safe)
  document.addEventListener('submit', function(e){
    var f = e.target.closest('form[data-confirm]');
    if (!f) return;
    e.preventDefault();
    var msg = f.getAttribute('data-confirm');
    confirmDialog(msg).then(function(ok){ if (ok) f.submit(); });
  });

  function hideLoader() {
    var el = document.getElementById('appLoader');
    if (!el) return;
    try { el.classList.add('d-none'); } catch (e) { el.style.display = 'none'; }
    try { el.setAttribute('aria-busy', 'false'); } catch (e2) {}
  }

  function initToastsFromFlash() {
    var el = document.getElementById('flashData');
    if (!el) return;
    var success = el.getAttribute('data-success');
    var error = el.getAttribute('data-error');
    if (success && success !== 'null' && success.trim() !== '') showToast(success, 'success');
    if (error && error !== 'null' && error.trim() !== '') showToast(error, 'danger');
  }

  function showToast(message, type) {
    var container = document.getElementById('toastContainer');
    if (!container) return;
    var toast = document.createElement('div');
    var variant = (type === 'success') ? 'text-bg-success' : (type === 'danger' ? 'text-bg-danger' : '');
    toast.className = 'toast align-items-center ' + variant;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');
    toast.setAttribute('data-bs-delay', '3000');
    toast.innerHTML = '<div class="d-flex">'
      + '<div class="toast-body">' + escapeHtml(message) + '</div>'
      + '<button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>'
      + '</div>';
    container.appendChild(toast);
    try {
      if (window.bootstrap && window.bootstrap.Toast) {
        var t = new window.bootstrap.Toast(toast, { autohide: true, delay: 3000 });
        t.show();
      } else {
        toast.style.display='block';
      }
    } catch (e) { toast.style.display='block'; }
  }

  function escapeHtml(s) {
    const map = {
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      '"': '&quot;',
      "'": '&#039;' // o &apos;
    };
    return String(s).replace(/[&<>"']/g, (m) => map[m]);
  }

  function initAjaxDelete() {
    document.addEventListener('submit', function(e){
      var form = e.target.closest('form.js-delete-student');
      if (!form) return;
      e.preventDefault();
      confirmDialog('Delete this student?').then(function(ok){
        if (!ok) return;
        var fd = new FormData(form);
        var url = form.getAttribute('action') || form.action; // avoid name="action" collision
        // also send CSRF header for filters that expect header
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
            // reapply table state (filter/sort/pagination)
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

  function initEditStudentModal() {
    var modal = document.getElementById('editStudentModal');
    if (!modal) return;
    modal.addEventListener('show.bs.modal', function (event) {
      var button = event.relatedTarget;
      if (!button) return;
      var id = button.getAttribute('data-id');
      var first = button.getAttribute('data-first');
      var last = button.getAttribute('data-last');
      var email = button.getAttribute('data-email');
      var setVal = function(id, val){ var el = document.getElementById(id); if (el) el.value = val || ''; };
      setVal('editStudentId', id);
      setVal('editFirstName', first);
      setVal('editLastName', last);
      setVal('editEmail', email);
    });
  }

  function initTooltips() {
    if (!(window.bootstrap && window.bootstrap.Tooltip)) return;
    try {
      var triggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"], [data-toggle="tooltip"]'));
      triggerList.forEach(function(el){ new window.bootstrap.Tooltip(el, { container: 'body' }); });
      var triggerList2 = [].slice.call(document.querySelectorAll('[data-bs-toggle2="tooltip"], [data-toggle2="tooltip"]'));
      triggerList2.forEach(function(el){ new window.bootstrap.Tooltip(el, { container: 'body' }); });
    } catch (e) {}
  }

  // Build/update Chart.js chart based on visible rows (email domain distribution)
  function updateStudentsChart(table, dt) {
    var canvas = document.getElementById('studentsChart');
    if (!canvas || !(window.Chart)) return;
    var emails = [];
    if (dt) {
      var nodes = dt.rows({ search: 'applied' }).nodes();
      Array.prototype.forEach.call(nodes, function(row){
        var cell = row.children[2];
        if (cell) emails.push((cell.textContent||'').trim());
      });
    } else {
      var rows = table.querySelectorAll('tbody tr');
      Array.prototype.forEach.call(rows, function(tr){
        if (tr.style.display === 'none') return;
        var cell = tr.children[2];
        if (cell) emails.push((cell.textContent||'').trim());
      });
    }
    var counts = {};
    emails.forEach(function(e){
      var m = e.split('@');
      var d = m.length>1 ? m[1].toLowerCase() : 'unknown';
      counts[d] = (counts[d]||0)+1;
    });
    var labels = Object.keys(counts);
    var data = labels.map(function(k){ return counts[k]; });
    if (!window.StudentChart) {
      window.StudentChart = new Chart(canvas.getContext('2d'), {
        type: 'bar',
        data: { labels: labels, datasets: [{ label: 'By domain', data: data, backgroundColor: '#4facfe' }] },
        options: { responsive: true, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true } } }
      });
    } else {
      var c = window.StudentChart; c.data.labels = labels; c.data.datasets[0].data = data; c.update('none');
    }
  }
})();
  function initNavActive() {
    var meta = document.querySelector('meta[name="current-route"]');
    var current = meta ? meta.getAttribute('content') : null;
    if (!current || current.trim() === '') {
      var p = window.location.pathname;
      if (p.indexOf('/admin/deletestudents') >= 0) current = 'delete';
      else if (p.indexOf('/admin/clientInformation') >= 0) current = 'clientinfo';
      else current = 'dashboard';
    }
    var links = document.querySelectorAll('.js-nav-route');
    links.forEach(function(a){ a.classList.remove('active'); });
    links.forEach(function(a){
      var r = a.getAttribute('data-route');
      if (r === current) {
        a.classList.add('active');
        a.setAttribute('aria-current', 'page');
      }
      a.addEventListener('click', function(e){ if (r === current) { e.preventDefault(); } });
    });
  }
