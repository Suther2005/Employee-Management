/* ============================================================
   Smart EMS — Main JavaScript
   ============================================================ */

document.addEventListener('DOMContentLoaded', function () {

  // ── Dark Mode ──────────────────────────────────────────────
  const darkToggle = document.getElementById('darkModeToggle');
  const body = document.body;
  if (localStorage.getItem('ems-dark') === '1') {
    body.setAttribute('data-theme', 'dark');
    if (darkToggle) darkToggle.innerHTML = '<i class="fas fa-sun"></i>';
  }
  if (darkToggle) {
    darkToggle.addEventListener('click', function () {
      const isDark = body.getAttribute('data-theme') === 'dark';
      if (isDark) {
        body.removeAttribute('data-theme');
        localStorage.setItem('ems-dark', '0');
        darkToggle.innerHTML = '<i class="fas fa-moon"></i>';
      } else {
        body.setAttribute('data-theme', 'dark');
        localStorage.setItem('ems-dark', '1');
        darkToggle.innerHTML = '<i class="fas fa-sun"></i>';
      }
    });
  }

  // ── Sidebar Mobile Toggle ──────────────────────────────────
  const sidebarToggle = document.getElementById('sidebarToggle');
  const sidebar = document.querySelector('.sidebar');
  if (sidebarToggle && sidebar) {
    sidebarToggle.addEventListener('click', () => sidebar.classList.toggle('open'));
    document.querySelector('.main-content')?.addEventListener('click', function (e) {
      if (window.innerWidth < 992 && sidebar.classList.contains('open')) {
        if (!sidebar.contains(e.target)) sidebar.classList.remove('open');
      }
    });
  }

  // ── Auto-dismiss flash messages after 4 seconds ───────────
  document.querySelectorAll('.flash-alert').forEach(function (alert) {
    setTimeout(function () {
      alert.style.animation = 'fadeOut 0.4s ease forwards';
      setTimeout(() => alert.remove(), 400);
    }, 4000);
  });

  // ── Delete Confirmation ────────────────────────────────────
  document.querySelectorAll('form.confirm-delete').forEach(function (form) {
    form.addEventListener('submit', function (e) {
      if (!confirm('Are you sure you want to delete this record? This action cannot be undone.')) {
        e.preventDefault();
      }
    });
  });

  // ── Live Search Debounce ───────────────────────────────────
  const searchInput = document.getElementById('searchInput');
  if (searchInput) {
    let debounceTimer;
    searchInput.addEventListener('input', function () {
      clearTimeout(debounceTimer);
      debounceTimer = setTimeout(function () {
        const form = searchInput.closest('form');
        if (form) form.submit();
      }, 350);
    });
  }

  // ── Profile Picture Preview ────────────────────────────────
  const photoInput = document.getElementById('photo');
  const photoPreview = document.getElementById('photoPreview');
  if (photoInput && photoPreview) {
    photoInput.addEventListener('change', function () {
      const file = this.files[0];
      if (file && file.type.startsWith('image/')) {
        const reader = new FileReader();
        reader.onload = e => photoPreview.src = e.target.result;
        reader.readAsDataURL(file);
      }
    });
  }

  // ── Attendance Clock ───────────────────────────────────────
  const clockEl = document.getElementById('liveClock');
  if (clockEl) {
    function updateClock() {
      const now = new Date();
      const h = String(now.getHours()).padStart(2, '0');
      const m = String(now.getMinutes()).padStart(2, '0');
      const s = String(now.getSeconds()).padStart(2, '0');
      clockEl.textContent = `${h}:${m}:${s}`;
    }
    updateClock();
    setInterval(updateClock, 1000);
  }

  // ── Leave Duration Calculator ──────────────────────────────
  const startDate = document.getElementById('startDate');
  const endDate = document.getElementById('endDate');
  const daysCount = document.getElementById('leaveDaysCount');
  function calcLeaveDays() {
    if (startDate && endDate && daysCount) {
      const start = new Date(startDate.value);
      const end = new Date(endDate.value);
      if (!isNaN(start) && !isNaN(end) && end >= start) {
        const diff = Math.round((end - start) / (1000 * 60 * 60 * 24)) + 1;
        daysCount.textContent = diff + ' day' + (diff > 1 ? 's' : '');
        daysCount.closest('.days-display')?.classList.remove('d-none');
      }
    }
  }
  startDate?.addEventListener('change', calcLeaveDays);
  endDate?.addEventListener('change', calcLeaveDays);

  // ── Net Salary Calculator ──────────────────────────────────
  function calcNetSalary() {
    const basic = parseFloat(document.getElementById('basicSalary')?.value) || 0;
    const allowance = parseFloat(document.getElementById('allowance')?.value) || 0;
    const bonus = parseFloat(document.getElementById('bonus')?.value) || 0;
    const deductions = parseFloat(document.getElementById('deductions')?.value) || 0;
    const net = basic + allowance + bonus - deductions;
    const netEl = document.getElementById('netSalaryDisplay');
    if (netEl) netEl.textContent = '₹ ' + net.toLocaleString('en-IN', { minimumFractionDigits: 2 });
  }
  ['basicSalary', 'allowance', 'bonus', 'deductions'].forEach(id => {
    document.getElementById(id)?.addEventListener('input', calcNetSalary);
  });
  calcNetSalary();

  // ── Password Strength ──────────────────────────────────────
  const pwdInput = document.getElementById('newPassword') || document.getElementById('password');
  const strengthFill = document.getElementById('strengthFill');
  const strengthText = document.getElementById('strengthText');
  if (pwdInput && strengthFill) {
    pwdInput.addEventListener('input', function () {
      const v = this.value;
      let score = 0;
      if (v.length >= 8) score++;
      if (/[A-Z]/.test(v)) score++;
      if (/[0-9]/.test(v)) score++;
      if (/[^A-Za-z0-9]/.test(v)) score++;
      const labels = ['', 'Weak', 'Fair', 'Good', 'Strong'];
      const classes = ['', 'strength-weak', 'strength-fair', 'strength-good', 'strength-strong'];
      strengthFill.className = 'strength-fill ' + (classes[score] || '');
      if (strengthText) strengthText.textContent = labels[score] || '';
    });
  }

  // ── Tooltips ───────────────────────────────────────────────
  if (typeof bootstrap !== 'undefined') {
    document.querySelectorAll('[data-bs-toggle="tooltip"]').forEach(el => {
      new bootstrap.Tooltip(el);
    });
  }

  // ── Active nav link highlight ──────────────────────────────
  const currentPath = window.location.pathname;
  document.querySelectorAll('.sidebar-nav a').forEach(link => {
    const href = link.getAttribute('href');
    if (href && currentPath.startsWith(href) && href !== '/') {
      link.classList.add('active');
    }
  });

});

// ── Chart.js Helpers ────────────────────────────────────────────

function initDeptChart(labels, data) {
  const ctx = document.getElementById('deptChart');
  if (!ctx) return;
  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: labels,
      datasets: [{
        label: 'Employees',
        data: data,
        backgroundColor: [
          'rgba(79,142,247,0.8)', 'rgba(0,210,160,0.8)',
          'rgba(139,92,246,0.8)', 'rgba(245,158,11,0.8)',
          'rgba(239,68,68,0.8)',  'rgba(99,102,241,0.8)'
        ],
        borderRadius: 8,
        borderSkipped: false,
      }]
    },
    options: {
      responsive: true, maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        y: { beginAtZero: true, grid: { color: 'rgba(0,0,0,0.05)' }, ticks: { stepSize: 1 } },
        x: { grid: { display: false } }
      }
    }
  });
}

function initTrendChart(labels, data) {
  const ctx = document.getElementById('trendChart');
  if (!ctx) return;
  new Chart(ctx, {
    type: 'line',
    data: {
      labels: labels,
      datasets: [{
        label: 'New Employees',
        data: data,
        borderColor: '#4f8ef7',
        backgroundColor: 'rgba(79,142,247,0.1)',
        borderWidth: 2.5,
        pointRadius: 4,
        pointBackgroundColor: '#4f8ef7',
        fill: true,
        tension: 0.4
      }]
    },
    options: {
      responsive: true, maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        y: { beginAtZero: true, grid: { color: 'rgba(0,0,0,0.05)' }, ticks: { stepSize: 1 } },
        x: { grid: { display: false } }
      }
    }
  });
}

function initLeaveChart(labels, data) {
  const ctx = document.getElementById('leaveChart');
  if (!ctx) return;
  new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: labels,
      datasets: [{
        data: data,
        backgroundColor: ['#4f8ef7','#00d2a0','#8b5cf6','#f59e0b','#ef4444','#6366f1','#64748b'],
        borderWidth: 0,
        hoverOffset: 6
      }]
    },
    options: {
      responsive: true, maintainAspectRatio: false,
      cutout: '65%',
      plugins: { legend: { position: 'bottom', labels: { padding: 14, usePointStyle: true } } }
    }
  });
}

function initAttendancePieChart(present, total) {
  const ctx = document.getElementById('attendanceChart');
  if (!ctx) return;
  const absent = Math.max(total - present, 0);
  new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: ['Present', 'Absent'],
      datasets: [{
        data: [present, absent],
        backgroundColor: ['#00d2a0', '#ef4444'],
        borderWidth: 0,
        hoverOffset: 6
      }]
    },
    options: {
      responsive: true, maintainAspectRatio: false,
      cutout: '70%',
      plugins: { legend: { position: 'bottom', labels: { padding: 14, usePointStyle: true } } }
    }
  });
}
