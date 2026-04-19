// Elements
const loginSection = document.getElementById('login-section');
const registerSection = document.getElementById('register-section');
const dashboardSection = document.getElementById('dashboard-section');

const loginBtn = document.getElementById('login-btn');
const registerBtn = document.getElementById('register-btn');
const logoutBtn = document.getElementById('logout-btn');
const refreshBtn = document.getElementById('refresh-btn');
const uploadBtn = document.getElementById('upload-btn');
const selectFileBtn = document.getElementById('select-file-btn');
const fileInput = document.getElementById('file-input');

const showRegister = document.getElementById('show-register');
const showLogin = document.getElementById('show-login');

// --- UTILS ---
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

// --- NAVIGATION ---
showRegister.addEventListener('click', function(e) {
    e.preventDefault();
    loginSection.style.display = 'none';
    registerSection.style.display = 'block';
});

showLogin.addEventListener('click', function(e) {
    e.preventDefault();
    registerSection.style.display = 'none';
    loginSection.style.display = 'block';
});

// --- AUTHENTICATION ---
loginBtn.addEventListener('click', function() {
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    fetch('/auth/login', {
        method: 'POST',
        headers: { 
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': getCookie('XSRF-TOKEN')
        },
        body: JSON.stringify({ email: email, password: password })
    })
    .then(function(response) {
        if (response.ok) return response.json();
        throw new Error('Login Failed! Check credentials');
    })
    .then(function(data) {
        document.getElementById('welcome-user').innerText = 'Welcome, ' + data.userName;
        loginSection.style.display = 'none';
        dashboardSection.style.display = 'block';
        fetchFiles();
    })
    .catch(function(error) {
        document.getElementById('login-msg').innerText = error.message;
    });
});

registerBtn.addEventListener('click', function() {
    const name = document.getElementById('reg-name').value;
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;
    const role = document.getElementById('reg-role').value;

    fetch('/auth/register', {
        method: 'POST',
        headers: { 
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': getCookie('XSRF-TOKEN')
        },
        body: JSON.stringify({ 
            userName: name, 
            userEmail: email, 
            userPassword: password,
            roles: [role] 
        })
    })
    .then(function(response) {
        if (response.ok) return response.json();
        throw new Error('Registration failed');
    })
    .then(function(data) {
        document.getElementById('register-msg').innerText = 'Account created! Please login.';
        document.getElementById('register-msg').style.color = '#10b981';
        setTimeout(function() { showLogin.click(); }, 1500);
    })
    .catch(function(error) {
        document.getElementById('register-msg').innerText = error.message;
        document.getElementById('register-msg').style.color = '#ef4444';
    });
});

// --- FILE OPERATIONS ---
function fetchFiles() {
    fetch('/file/all')
    .then(function(response) {
        if (!response.ok) throw new Error('Failed to fetch files');
        return response.json();
    })
    .then(function(files) {
        const list = document.getElementById('file-list');
        list.innerHTML = '';
        files.forEach(function(file) {
            const li = document.createElement('li');
            li.innerHTML = `
                <div class="file-info">
                    <div class="file-name">${file.fileName}</div>
                    <small>${(file.fileSize / 1024).toFixed(2)} KB</small>
                </div>
                <button class="download-btn" onclick="downloadFile(${file.id})">Download</button>
            `;
            list.appendChild(li);
        });
    })
    .catch(function(error) {
        console.error(error);
    });
}

function downloadFile(fileId) {
    window.location.href = '/file/download/' + fileId;
}

selectFileBtn.addEventListener('click', function() {
    fileInput.click();
});

fileInput.addEventListener('change', function() {
    if (fileInput.files.length > 0) {
        document.getElementById('selected-file-name').innerText = fileInput.files[0].name;
    }
});

uploadBtn.addEventListener('click', function() {
    if (fileInput.files.length === 0) {
        alert('Please select a file first');
        return;
    }

    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    fetch('/file/upload', {
        method: 'POST',
        headers: {
            'X-XSRF-TOKEN': getCookie('XSRF-TOKEN')
        },
        body: formData
    })
    .then(function(response) {
        if (response.ok) {
            alert('Upload success!');
            fileInput.value = '';
            document.getElementById('selected-file-name').innerText = 'No file selected';
            fetchFiles();
        } else {
            throw new Error('Upload failed. Status: ' + response.status);
        }
    })
    .catch(function(error) {
        alert(error.message);
    });
});

refreshBtn.addEventListener('click', fetchFiles);
logoutBtn.addEventListener('click', function() {
    location.reload();
});
