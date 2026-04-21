const baseUrl = "http://localhost:8080";

let currentTab = 'active';

document.addEventListener('DOMContentLoaded', () => {
    // 1. Cek apakah user sudah login (dari localStorage & cookie)
    const userId = localStorage.getItem('userId');
    const username = localStorage.getItem('username');
    
    // Validasi super basic: kalo gaada userId di localstorage, tendang ke login
    if (!userId) {
        window.location.href = 'index.html';
        return;
    }

    // Set username di navbar
    document.getElementById('nav-username').textContent = username || 'User';

    // 2. Load file list pas pertama kali
    loadUserFiles();
});

function handleLogout() {
    localStorage.removeItem('userId');
    localStorage.removeItem('username');
    document.cookie = "SESSION_ID=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
    window.location.href = 'index.html';
}

function updateFileName(input) {
    const display = document.getElementById('file-name-display');
    if (input.files && input.files.length > 0) {
        const file = input.files[0];
        display.textContent = `Terpilih: ${file.name} (${formatBytes(file.size)})`;
        display.classList.remove('hidden');
    } else {
        display.classList.add('hidden');
    }
}

async function handleUpload(e) {
    e.preventDefault();
    
    const fileInput = document.getElementById('upload-input');
    const userId = localStorage.getItem('userId');
    
    const btn = document.getElementById('btn-upload');
    const errorEl = document.getElementById('upload-error');
    const successEl = document.getElementById('upload-success');
    
    errorEl.textContent = '';
    successEl.textContent = '';

    if (!fileInput.files || fileInput.files.length === 0) {
        errorEl.textContent = 'Pilih file terlebih dahulu!';
        return;
    }

    const formData = new FormData();
    formData.append('userId', userId);
    formData.append('file', fileInput.files[0]);

    btn.disabled = true;
    btn.textContent = 'Mengupload...';

    try {
        const response = await fetch(baseUrl + '/file/upload', {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            throw new Error('Upload gagal. Pastikan file valid.');
        }

        const data = await response.json();
        
        // Tampilkan pesan sukses
        successEl.textContent = 'File berhasil diupload!';
        document.getElementById('upload-form').reset();
        updateFileName(fileInput); // Sembunyikan label terpilih
        
        // Refresh daftar file
        loadUserFiles();

        // Hilangkan pesan sukses setelah 3 detik
        setTimeout(() => {
            successEl.textContent = '';
        }, 3000);

    } catch (error) {
        errorEl.textContent = error.message;
    } finally {
        btn.disabled = false;
        btn.textContent = 'Upload File';
    }
}

async function loadUserFiles() {
    const userId = localStorage.getItem('userId');
    const container = document.getElementById('file-list-container');
    
    container.innerHTML = '<div class="loading-state">Sedang memuat file...</div>';

    try {
        const response = await fetch(`/file/user/${userId}`);
        
        // Kalo statusnya 401 Unauthorized, berarti cookie session udah mati
        if (response.status === 401) {
            handleLogout();
            return;
        }

        if (!response.ok) {
            throw new Error('Gagal mengambil daftar file.');
        }

        const files = await response.json();
        
        // Filter based on active tab
        const filteredFiles = files.filter(f => currentTab === 'active' ? !f.isRemoved : f.isRemoved);
        
        if (filteredFiles.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <span style="font-size: 2rem; display:block; margin-bottom:0.5rem">📭</span>
                    <p>${currentTab === 'active' ? 'Belum ada file. Yuk upload file pertama kamu!' : 'Tempat sampah kosong.'}</p>
                </div>
            `;
            return;
        }

        // Render files
        container.innerHTML = filteredFiles.map(file => `
            <div class="file-item">
                <div class="file-info">
                    <span class="file-type-icon">${getFileIcon(file.originalName)}</span>
                    <div class="file-details">
                        <h4>${file.originalName}</h4>
                        <p>${formatBytes(file.size)} • ${formatDate(file.createdAt)}</p>
                    </div>
                </div>
                <div class="action-buttons">
                    ${currentTab === 'active' ? `
                        <button class="btn-download" onclick="downloadFile(${file.id}, '${file.originalName}')">⬇️ Download</button>
                        <button class="btn-danger" onclick="deleteFile(${file.id})">🗑️ Hapus</button>
                    ` : `
                        <button class="btn-restore" onclick="restoreFile(${file.id})">♻️ Restore</button>
                    `}
                </div>
            </div>
        `).join('');

    } catch (error) {
        container.innerHTML = `<div class="error-msg" style="display:block">Error: ${error.message}</div>`;
    }
}

function switchFileTab(tab) {
    currentTab = tab;
    
    document.getElementById('tab-active-files').classList.toggle('active', tab === 'active');
    document.getElementById('tab-trash-files').classList.toggle('active', tab === 'trash');
    
    loadUserFiles();
}

async function deleteFile(fileId) {
    if (!confirm('Yakin ingin menghapus file ini ke tempat sampah?')) return;
    
    const userId = localStorage.getItem('userId');
    try {
        const response = await fetch(baseUrl + `/file/delete/${fileId}?userId=${userId}`, { method: 'DELETE' });
        
        if (response.status === 401) {
            handleLogout();
            return;
        }
        
        if (!response.ok) throw new Error('Gagal menghapus file.');
        
        loadUserFiles();
    } catch (error) {
        alert(error.message);
    }
}

async function restoreFile(fileId) {
    const userId = localStorage.getItem('userId');
    try {
        const response = await fetch(baseUrl + `/file/restore/${fileId}?userId=${userId}`, { method: 'PUT' });
        
        if (response.status === 401) {
            handleLogout();
            return;
        }
        
        if (!response.ok) throw new Error('Gagal merestore file.');
        
        loadUserFiles();
    } catch (error) {
        alert(error.message);
    }
}

async function downloadFile(fileId, fileName) {
    const userId = localStorage.getItem('userId');
    
    try {
        // Kita menggunakan fetch agar bisa menyertakan cookie & nangkap response error dengan baik
        const response = await fetch(baseUrl + `/file/download/${fileId}?userId=${userId}`);
        
        if (response.status === 401) {
            handleLogout();
            return;
        }

        if (!response.ok) {
            alert('Gagal mendownload file. File mungkin sudah dihapus atau akses ditolak.');
            return;
        }

        // Konversi response stream menjadi blob URL untuk di-download browser
        const blob = await response.blob();
        const downloadUrl = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.style.display = 'none';
        a.href = downloadUrl;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(downloadUrl);
        a.remove();
        
    } catch (error) {
        alert('Terjadi kesalahan saat download: ' + error.message);
    }
}

// === UTILS ===

function formatBytes(bytes, decimals = 2) {
    if (!+bytes) return '0 Bytes';
    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return `${parseFloat((bytes / Math.pow(k, i)).toFixed(dm))} ${sizes[i]}`;
}

function formatDate(dateArray) {
    // Karena response JSON dari Java LocalDateTime biasanya berbentuk array [tahun, bulan, tanggal, jam, menit, ...]
    if (Array.isArray(dateArray)) {
        const [year, month, day] = dateArray;
        return `${day}/${month}/${year}`;
    }
    // Fallback kalau ternyata formatnya ISO string
    if (dateArray) {
        const d = new Date(dateArray);
        return d.toLocaleDateString('id-ID');
    }
    return '-';
}

function getFileIcon(filename) {
    if (!filename) return '📄';
    const ext = filename.split('.').pop().toLowerCase();
    
    const icons = {
        'pdf': '📕',
        'doc': '📘', 'docx': '📘',
        'xls': '📗', 'xlsx': '📗',
        'jpg': '🖼️', 'jpeg': '🖼️', 'png': '🖼️', 'gif': '🖼️',
        'mp4': '🎬', 'mkv': '🎬',
        'mp3': '🎵', 'wav': '🎵',
        'zip': '📦', 'rar': '📦'
    };

    return icons[ext] || '📄';
}
