// js/auth.js — shared auth helpers loaded by both dashboards

function getToken() {
  return localStorage.getItem('slm_token');
}

function getUser() {
  try {
    return JSON.parse(localStorage.getItem('slm_user') || '{}');
  } catch (e) {
    return {};
  }
}

function clearAuth() {
  localStorage.removeItem('slm_token');
  localStorage.removeItem('slm_user');
}

// Guard: call at top of each dashboard with the required role
function requireRole(role) {
  const token = getToken();
  const user  = getUser();
  if (!token || !user.role) {
    window.location.href = 'login.html';
    return false;
  }
  if (user.role.toUpperCase() !== role.toUpperCase()) {
    window.location.href = 'login.html';
    return false;
  }
  return true;
}