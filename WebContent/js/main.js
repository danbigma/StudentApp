/* global window, document, fetch */
(function() {
  function setBadge(el, up) {
    if (!el) return;
    el.classList.remove('badge-secondary', 'badge-success', 'badge-danger');
    el.classList.add(up ? 'badge-success' : 'badge-danger');
    el.textContent = up ? 'UP' : 'DOWN';
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
        setBadge(document.getElementById('healthStatus'), json && json.status === 'UP');
        var dbUp = json && json.db && json.db.status === 'UP';
        setBadge(document.getElementById('dbStatus'), !!dbUp);
      })
      .catch(function() {
        setBadge(document.getElementById('healthStatus'), false);
        setBadge(document.getElementById('dbStatus'), false);
      })
      .then(function(){ hideLoader(); });
  }

  function initStudentSearch() {
    var input = document.getElementById('studentSearch');
    var table = document.getElementById('studentsTable');
    var pager = document.getElementById('studentsPagination');
    var pageSizeSel = document.getElementById('studentsPageSize');
    if (!table) return;

    // If DataTables is available, use it for a richer UX and skip custom logic
    if (window.jQuery && jQuery.fn && typeof jQuery.fn.DataTable === 'function') {
      var $table = jQuery(table);
      var dt = $table.DataTable({
        paging: true,
        searching: true,
        info: false,
        lengthChange: false,
        pageLength: pageSizeSel ? parseInt(pageSizeSel.value||'10',10) : 10,
        dom: 'Bfrtip',
        buttons: [ 'copy', 'csvHtml5', 'excelHtml5', 'pdfHtml5', 'print' ],
        order: [],
        columnDefs: [
          { targets: -1, orderable: false }
        ]
      });
      if (input) input.addEventListener('input', function(){ dt.search(input.value).draw(); });
      if (pageSizeSel) pageSizeSel.addEventListener('change', function(){ dt.page.len(parseInt(pageSizeSel.value,10)||10).draw(); });
      if (window.jQuery && jQuery.fn && jQuery.fn.select2 && pageSizeSel) { jQuery(pageSizeSel).select2({ minimumResultsForSearch: Infinity, width: '100px' }); }
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
    var btn = document.getElementById('copyRequestIdBtn');
    var target = document.getElementById('requestIdVal');
    if (!btn || !target) return;
    btn.addEventListener('click', function() {
      var text = target.textContent || target.innerText;
      if (!navigator.clipboard) {
        var ta = document.createElement('textarea');
        ta.value = text; document.body.appendChild(ta); ta.select();
        try { document.execCommand('copy'); } catch (e) {}
        document.body.removeChild(ta);
        return;
      }
      navigator.clipboard.writeText(text);
    });
  }

  document.addEventListener('DOMContentLoaded', function() {
    updateHealth();
    setInterval(updateHealth, 15000);
    initStudentSearch();
    initConfirmLinks();
    initCopyRequestId();
    initBulkDeletePage();
    initTheme();
    // hide loader soon after initial init
    try { window.requestAnimationFrame(hideLoader); } catch (e) { hideLoader(); }
    initToastsFromFlash();
    initEditStudentModal();
    initAjaxDelete();
    initTooltips();
  });

  window.addEventListener('load', function(){
    // Fallback in case DOMContentLoaded fired too early
    hideLoader();
  });

  function initBulkDeletePage() {
    var form = document.querySelector('form[action="deletestudents"], form[action$="/deletestudents"]');
    if (!form) return;
    var table = document.getElementById('bulkTable') || form.querySelector('table');
    var selectAll = form.querySelector('input[name="students"]');
    var inputs = form.querySelectorAll('input[name="student"]');
    var btn = form.querySelector('#buttonDelete');
    var search = document.getElementById('bulkSearch');
    var dt = null;

    // Enhance with DataTables if available
    if (table && window.jQuery && jQuery.fn && typeof jQuery.fn.DataTable === 'function') {
      dt = jQuery(table).DataTable({
        paging: true,
        searching: true,
        info: false,
        lengthChange: false,
        pageLength: 10,
        dom: 'Bfrtip',
        buttons: [ 'copy', 'csvHtml5', 'excelHtml5', 'pdfHtml5', 'print' ],
        order: [],
        columnDefs: [ { targets: 0, orderable: false } ]
      });
      if (search) search.addEventListener('input', function(){ dt.search(search.value).draw(); });
      // When table redraws (paging/search), re-bind and update
      jQuery(table).on('draw.dt', function(){
        inputs = form.querySelectorAll('input[name="student"]');
        inputs.forEach(function(ch){ ch.addEventListener('click', enableButton); });
        enableButton();
        if (selectAll) selectAll.checked = false;
      });
    }
    function enableButton() {
      var any = false;
      inputs.forEach(function(ch){ if (ch.checked) any = true; });
      if (btn) btn.disabled = !any;
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

  // Theme toggle (light/dark) with localStorage and prefers-color-scheme
  function initTheme() {
    var toggle = document.getElementById('themeSwitch');
    var legacyBtn = document.getElementById('themeToggle');
    var saved = localStorage.getItem('theme');
    var prefersDark = false;
    try {
      prefersDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
    } catch (e) {}
    var current = saved || (prefersDark ? 'dark' : 'light');
    applyTheme(current);
    if (toggle) {
      try { toggle.checked = (current === 'dark'); } catch (e) {}
      toggle.addEventListener('change', function(){
        current = toggle.checked ? 'dark' : 'light';
        localStorage.setItem('theme', current);
        applyTheme(current);
      });
    }
    if (legacyBtn) { // fallback if older template exists
      legacyBtn.addEventListener('click', function(){
        current = (current === 'dark') ? 'light' : 'dark';
        localStorage.setItem('theme', current);
        applyTheme(current);
      });
    }
  }

  function applyTheme(theme) {
    var html = document.documentElement;
    if (theme === 'dark') {
      html.setAttribute('data-theme', 'dark');
      var t = document.getElementById('themeSwitch'); if (t) t.checked = true;
    } else {
      html.removeAttribute('data-theme');
      var t2 = document.getElementById('themeSwitch'); if (t2) t2.checked = false;
    }
  }

  function hideLoader() {
    var el = document.getElementById('appLoader');
    if (!el) return;
    if (!el.classList.contains('hidden')) el.classList.add('hidden');
    try { el.setAttribute('aria-busy', 'false'); } catch (e) {}
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
    toast.className = 'toast';
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');
    toast.setAttribute('data-delay', '3000');
    toast.innerHTML = '<div class="toast-header">'
      + '<strong class="mr-auto">' + (type === 'success' ? 'Success' : 'Notice') + '</strong>'
      + '<small class="text-muted">now</small>'
      + '<button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close"><span aria-hidden="true">&times;</span></button>'
      + '</div>'
      + '<div class="toast-body text-' + (type === 'success' ? 'success' : 'danger') + '">' + escapeHtml(message) + '</div>';
    container.appendChild(toast);
    try { $(toast).toast('show'); } catch (e) { toast.style.display='block'; }
  }

  function escapeHtml(s) {
    return String(s).replace(/[&<>"];/g, function(c){
      return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c] || c;
    });
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
        fetch(url, {
          method: 'POST',
          credentials: 'same-origin',
          headers: { 'X-Requested-With': 'XMLHttpRequest', 'Accept': 'application/json' },
          body: fd
        })
        .then(function(r){ return r.json(); })
        .then(function(json){
          if (json && json.ok) {
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
    var modal = $('#editStudentModal');
    if (!modal.length) return;
    modal.on('show.bs.modal', function (event) {
      var button = $(event.relatedTarget);
      var id = button.data('id');
      var first = button.data('first');
      var last = button.data('last');
      var email = button.data('email');
      $('#editStudentId').val(id);
      $('#editFirstName').val(first);
      $('#editLastName').val(last);
      $('#editEmail').val(email);
    });
  }

  function initTooltips() {
    if (window.jQuery && jQuery.fn && typeof jQuery.fn.tooltip === 'function') {
      try { jQuery('[data-toggle="tooltip"]').tooltip({ container: 'body' }); } catch (e) {}
      // Initialize tooltips for elements marked with data-toggle2="tooltip" without altering existing data-toggle (e.g., modal)
      try { jQuery('[data-toggle2="tooltip"]').each(function(){ jQuery(this).tooltip({ container: 'body' }); }); } catch (e) {}
    }
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
