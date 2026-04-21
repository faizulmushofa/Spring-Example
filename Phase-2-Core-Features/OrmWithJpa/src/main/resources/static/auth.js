const baseUrl = "http://localhost:8080";


function switchTab(tab) {
    const loginForm = document.getElementById('form-login');
    const registerForm = document.getElementById('form-register');
    const btnLogin = document.getElementById('tab-login');
    const btnRegister = document.getElementById('tab-register');
    const container = document.querySelector('.auth-container');

    if (tab === 'login') {
        loginForm.classList.remove('hidden');
        registerForm.classList.add('hidden');
        btnLogin.classList.add('active');
        btnRegister.classList.remove('active');
        container.classList.remove('register-mode');
    } else {
        loginForm.classList.add('hidden');
        registerForm.classList.remove('hidden');
        btnLogin.classList.remove('active');
        btnRegister.classList.add('active');
        container.classList.add('register-mode');
    }
}

async function handleLogin(e) {
    e.preventDefault();
    
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    const btn = document.getElementById('btn-login');
    const errorEl = document.getElementById('login-error');
    
    errorEl.textContent = '';
    btn.disabled = true;
    btn.textContent = 'Memproses...';

    try {
        const response = await fetch(baseUrl + '/Auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });

        if (!response.ok) {
            const errorMsg = await response.text();
            throw new Error(errorMsg || 'Login gagal. Periksa email dan password.');
        }

        const data = await response.json();
        
        // Save user data for dashboard access
        localStorage.setItem('userId', data.userId);
        localStorage.setItem('username', data.username);
        
        // Note: SESSION_ID cookie is automatically saved by the browser because of the Set-Cookie header.
        
        // Session cookie sudah di-set oleh server — langsung redirect
        window.location.href = 'dashboard.html';
        
    } catch (error) {
        errorEl.textContent = error.message;
    } finally {
        btn.disabled = false;
        btn.textContent = 'Masuk';
    }
}

async function handleRegister(e) {
    e.preventDefault();
    
    const firstName = document.getElementById('reg-firstname').value;
    const lastName = document.getElementById('reg-lastname').value;
    const username = document.getElementById('reg-username').value;
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;
    
    const btn = document.getElementById('btn-register');
    const errorEl = document.getElementById('reg-error');
    const successEl = document.getElementById('reg-success');
    
    errorEl.textContent = '';
    successEl.textContent = '';
    btn.disabled = true;
    btn.textContent = 'Mendaftar...';

    try {
        const response = await fetch(baseUrl + '/Auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ firstName, lastName, username, email, password })
        });

        if (!response.ok) {
            const errorMsg = await response.text();
            throw new Error(errorMsg || 'Registrasi gagal. Coba lagi.');
        }

        // Jika berhasil
        successEl.textContent = 'Registrasi berhasil! Silahkan login.';
        document.getElementById('form-register').reset();
        
        setTimeout(() => {
            switchTab('login');
        }, 1500);
        
    } catch (error) {
        errorEl.textContent = error.message;
    } finally {
        btn.disabled = false;
        btn.textContent = 'Daftar Sekarang';
    }
}
