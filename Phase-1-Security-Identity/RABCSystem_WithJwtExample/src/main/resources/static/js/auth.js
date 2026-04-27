// js/auth.js
const basePath = "http://localhost:8080";

function decodeJWT(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch(e) {
        return null;
    }
}

function showAlert(elementId, message, isSuccess) {
    const alertEl = document.getElementById(elementId);
    if (!alertEl) return;
    alertEl.innerHTML = message;
    alertEl.className = 'alert ' + (isSuccess ? 'success' : 'error');
}

document.getElementById('loginForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const btn = document.getElementById('loginBtn');
    
    btn.textContent = 'Memproses...';
    btn.disabled = true;

    try {
        const response = await fetch(basePath + '/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('token', data.token);
            
            showAlert('loginAlert', 'Login berhasil! Mengalihkan...', true);
            
            const payload = decodeJWT(data.token);
            const roles = payload.roles || [];

            setTimeout(() => {
                if (roles.includes('admin')) {
                    window.location.href = 'admin.html';
                } else if (roles.includes('dosen')) {
                    window.location.href = 'dosen.html';
                } else if (roles.includes('student')) {
                    window.location.href = 'student.html';
                } else {
                    showAlert('loginAlert', 'Role tidak dikenali!', false);
                    btn.textContent = 'Sign In';
                    btn.disabled = false;
                }
            }, 1000);
        } else {
            const errData = await response.json().catch(() => ({}));
            showAlert('loginAlert', errData.message || 'Email atau password salah!', false);
            btn.textContent = 'Sign In';
            btn.disabled = false;
        }
    } catch (err) {
        console.error(err);
        showAlert('loginAlert', 'Terjadi kesalahan pada server.', false);
        btn.textContent = 'Sign In';
        btn.disabled = false;
    }
});

// Logic untuk Register
document.getElementById('registerForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    const btn = document.getElementById('regBtn');

    btn.textContent = 'Memproses...';
    btn.disabled = true;

    try {
        const response = await fetch(basePath + '/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json().catch(() => ({}));

        if (response.ok) {
            // "semua harus sesuai dengan fetch apinya di pakai semua juga"
            const successMsg = data.message || 'Pendaftaran berhasil!';
            const userEmail = data.user ? data.user.email : email;
            showAlert('regAlert', `<strong>${successMsg}</strong><br>Selamat datang, ${userEmail}!<br>Silakan login.`, true);
            
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 2500);
        } else {
            // Handle validation errors from Spring (usually in data.errors or data.message)
            let errMsg = data.message || 'Pendaftaran gagal.';
            if (data.errors && Array.isArray(data.errors)) {
                errMsg = data.errors.join('<br>');
            } else if (data.email) {
                // If it's a field error map
                errMsg = data.email;
            }
            showAlert('regAlert', errMsg, false);
            btn.textContent = 'Register Now';
            btn.disabled = false;
        }
    } catch (err) {
        console.error(err);
        showAlert('regAlert', 'Terjadi kesalahan pada server.', false);
        btn.textContent = 'Register Now';
        btn.disabled = false;
    }
});

