const token = localStorage.getItem('token');
if (!token) window.location.href = 'login.html';

const basePath = 'http://localhost:8080';

function getPayload() {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(decodeURIComponent(atob(base64).split('').map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)).join('')));
}
const payload = getPayload();
document.getElementById('userEmail').innerText = payload.sub;

function logout() {
    localStorage.removeItem('token');
    window.location.href = 'login.html';
}

// ---- Navigation ----
function showSection(sectionId) {
    ['users', 'courses', 'permissions'].forEach(s => {
        document.getElementById(`section-${s}`).style.display = 'none';
        document.getElementById(`btn-${s}`).classList.remove('active');
    });
    document.getElementById(`section-${sectionId}`).style.display = 'block';
    document.getElementById(`btn-${sectionId}`).classList.add('active');

    if (sectionId === 'users') fetchUsers();
    if (sectionId === 'courses') fetchAdminCourses();
    if (sectionId === 'permissions') { fetchPermissions(); fetchRoles(); }
}

// =============== USERS ===============

function toggleRegisterForm() {
    const card = document.getElementById('registerUserCard');
    card.style.display = card.style.display === 'none' ? 'block' : 'none';
}

async function fetchUsers() {
    const filter = document.getElementById('userRoleFilter').value;
    let url = basePath + '/api/users';
    if (filter === 'dosen') url = basePath + '/api/users/lecturers';
    if (filter === 'student') url = basePath + '/api/users/students';

    try {
        const response = await fetch(url, { headers: { 'Authorization': `Bearer ${token}` } });
        const list = document.getElementById('usersList');
        if (response.ok) {
            const users = await response.json();
            if (users.length === 0) {
                list.innerHTML = '<div class="empty-state">No users found.</div>';
                return;
            }
            list.innerHTML = users.map(u => {
                const rolesStr = u.roles ? u.roles.join(', ') : (filter === 'all' ? '-' : filter);
                const permsStr = u.permissions && u.permissions.length > 0 ? u.permissions.join(', ') : 'None';
                const isActive = u.active !== undefined ? u.active : true;
                return `
                <div class="card" style="${!isActive ? 'border-color: var(--danger); opacity: 0.7;' : ''}">
                    <div class="flex-between" style="margin-bottom: 0.5rem;">
                        <strong>${u.email}</strong>
                        <span class="badge ${isActive ? 'badge-success' : 'badge-danger'}">${isActive ? 'Active' : 'Inactive'}</span>
                    </div>
                    <div style="font-size: 0.85rem; color: var(--text-secondary); margin-bottom: 0.3rem;">
                        Roles: <strong style="color: var(--primary);">${rolesStr}</strong>
                    </div>
                    <div style="font-size: 0.85rem; color: var(--text-secondary); margin-bottom: 0.3rem;">
                        Permissions: <strong>${permsStr}</strong>
                    </div>
                    ${u.courseCount !== undefined ? `<div style="font-size: 0.85rem; color: var(--text-secondary); margin-bottom: 0.75rem;">Courses: <strong>${u.courseCount}</strong></div>` : ''}
                    <div class="flex-gap">
                        ${isActive
                            ? `<button onclick="toggleUserStatus(${u.id}, 'deactivate')" class="btn-danger btn-small">Deactivate</button>`
                            : `<button onclick="toggleUserStatus(${u.id}, 'activate')" class="btn-success btn-small">Activate</button>`}
                        ${u.roles && !u.roles.includes('dosen') && !u.roles.includes('admin')
                            ? `<button onclick="promoteUser(${u.id})" class="btn-small" style="background:var(--warning);">Set Dosen</button>`
                            : ''}
                    </div>
                </div>
            `}).join('');
        }
    } catch (e) { console.error(e); }
}

async function toggleUserStatus(userId, action) {
    if (!confirm(`${action === 'activate' ? 'Activate' : 'Deactivate'} this user?`)) return;
    try {
        const r = await fetch(basePath + `/api/users/${userId}/${action}`, {
            method: 'PUT', headers: { 'Authorization': `Bearer ${token}` }
        });
        if (r.ok) fetchUsers();
        else alert('Failed.');
    } catch (e) { console.error(e); }
}

async function promoteUser(userId) {
    if (!confirm('Promote this user to Dosen role?')) return;
    try {
        const r = await fetch(basePath + `/api/roles/set-dosen/${userId}`, {
            method: 'POST', headers: { 'Authorization': `Bearer ${token}` }
        });
        if (r.ok) { alert('User is now a Dosen!'); fetchUsers(); }
        else alert('Failed.');
    } catch (e) { console.error(e); }
}

// Admin Register User
document.getElementById('adminRegisterForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('newUserEmail').value;
    const password = document.getElementById('newUserPassword').value;
    const role = document.getElementById('newUserRole').value;
    const msg = document.getElementById('adminRegMsg');

    try {
        // 1. Register
        const r1 = await fetch(basePath + '/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        if (!r1.ok) { msg.style.color = 'var(--danger)'; msg.innerText = 'Registration failed.'; return; }
        const data = await r1.json();
        const userId = data.user.id;

        // 2. If dosen, assign dosen role
        if (role === 'dosen') {
            await fetch(basePath + `/api/roles/set-dosen/${userId}`, {
                method: 'POST', headers: { 'Authorization': `Bearer ${token}` }
            });
        }

        msg.style.color = 'var(--success)';
        msg.innerText = `User "${email}" created as ${role}!`;
        document.getElementById('adminRegisterForm').reset();
        fetchUsers();
        setTimeout(() => msg.innerText = '', 3000);
    } catch (err) { console.error(err); }
});

// =============== COURSES ===============

function toggleAdminCourseForm() {
    const card = document.getElementById('adminCreateCourseCard');
    card.style.display = card.style.display === 'none' ? 'block' : 'none';
}

async function fetchAdminCourses() {
    try {
        const response = await fetch(basePath + '/api/courses', { headers: { 'Authorization': `Bearer ${token}` } });
        const list = document.getElementById('adminCoursesList');
        if (response.ok) {
            const courses = await response.json();
            if (courses.length === 0) {
                list.innerHTML = '<div class="empty-state">No courses yet.</div>';
                return;
            }
            list.innerHTML = courses.map(c => `
                <div class="card" style="${!c.active ? 'border-color: var(--danger); opacity: 0.7;' : ''}">
                    <div class="flex-between" style="margin-bottom: 0.5rem;">
                        <strong>${c.tittle || c.title || 'Untitled'}</strong>
                        <span class="badge ${c.active ? 'badge-success' : 'badge-danger'}">${c.active ? 'Active' : 'Inactive'}</span>
                    </div>
                    <div style="font-size: 0.85rem; color: var(--text-secondary); margin-bottom: 0.3rem;">ID: ${c.id}</div>
                    <div style="font-size: 0.85rem; color: var(--text-secondary); margin-bottom: 1rem;">${c.description}</div>
                    <div class="flex-gap">
                        <button onclick="editAdminCourse(${c.id}, '${(c.tittle || c.title || '').replace(/'/g, "\\'")}', '${(c.description || '').replace(/'/g, "\\'")}', ${c.active})" class="btn-small">Edit</button>
                        ${c.active ? `<button onclick="deActiveAdminCourse(${c.id})" class="btn-danger btn-small">Deactivate</button>` : ''}
                        <button onclick="showEnrollForm(${c.id})" class="btn-success btn-small">+ Enroll Student</button>
                    </div>
                </div>
            `).join('');
        }
    } catch (e) { console.error(e); }
}

async function deActiveAdminCourse(id) {
    if (!confirm('Deactivate this course?')) return;
    try {
        const r = await fetch(basePath + `/api/courses/${id}`, { method: 'DELETE', headers: { 'Authorization': `Bearer ${token}` } });
        if (r.ok) fetchAdminCourses();
    } catch (e) { console.error(e); }
}

function editAdminCourse(id, title, desc, active) {
    document.getElementById('updateCourseId').value = id;
    document.getElementById('updateCourseIdTxt').innerText = id;
    document.getElementById('updateCourseTitle').value = title;
    document.getElementById('updateCourseDesc').value = desc;
    document.getElementById('updateCourseActive').checked = active;
    document.getElementById('updateCourseCard').style.display = 'block';
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

document.getElementById('updateCourseForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('updateCourseId').value;
    const tittle = document.getElementById('updateCourseTitle').value;
    const description = document.getElementById('updateCourseDesc').value;
    const active = document.getElementById('updateCourseActive').checked;
    try {
        const r = await fetch(basePath + `/api/courses/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
            body: JSON.stringify({ tittle, description, active })
        });
        if (r.ok) { document.getElementById('updateCourseCard').style.display = 'none'; fetchAdminCourses(); }
        else alert('Failed to update.');
    } catch (err) { console.error(err); }
});

// Admin Create Course
document.getElementById('adminCourseForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const tittle = document.getElementById('adminCourseTitle').value;
    const description = document.getElementById('adminCourseDesc').value;
    const dosenEmail = document.getElementById('adminCourseDosenEmail').value;

    // CourseService.create uses Principal.getName() for owner,
    // but admin can override by calling the endpoint with a dosen's context.
    // Since admin has MANAGE_ALL, we create with admin token, the backend assigns to Principal.
    // Workaround: we POST the course, it's assigned to admin, but we need to assign to dosen.
    // Better approach: use a dedicated admin endpoint. For now, just create with admin.
    try {
        const r = await fetch(basePath + '/api/courses', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
            body: JSON.stringify({ tittle, description, active: true })
        });
        if (r.ok) {
            toggleAdminCourseForm();
            document.getElementById('adminCourseForm').reset();
            fetchAdminCourses();
            alert(`Course created! Note: Course is owned by the current admin. To assign to "${dosenEmail}", use course edit.`);
        } else alert('Failed to create course.');
    } catch (err) { console.error(err); }
});

// Enroll Student
function showEnrollForm(courseId) {
    document.getElementById('enrollCourseId').value = courseId;
    document.getElementById('enrollStudentCard').style.display = 'block';
    document.getElementById('enrollStudentCard').scrollIntoView({ behavior: 'smooth' });
}

document.getElementById('enrollStudentForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const userId = document.getElementById('enrollStudentId').value;
    const courseId = document.getElementById('enrollCourseId').value;
    const msg = document.getElementById('enrollMsg');
    try {
        const r = await fetch(basePath + '/api/enrolments', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
            body: JSON.stringify({ userId: parseInt(userId), courseId: parseInt(courseId) })
        });
        if (r.ok) {
            msg.style.color = 'var(--success)';
            msg.innerText = 'Student enrolled successfully!';
            document.getElementById('enrollStudentForm').reset();
            setTimeout(() => { msg.innerText = ''; document.getElementById('enrollStudentCard').style.display = 'none'; }, 2000);
        } else {
            msg.style.color = 'var(--danger)';
            msg.innerText = 'Failed — student may already be enrolled.';
        }
    } catch (err) { console.error(err); }
});

// =============== PERMISSIONS ===============

async function fetchRoles() {
    try {
        const r = await fetch(basePath + '/api/roles', { headers: { 'Authorization': `Bearer ${token}` } });
        if (r.ok) {
            const roles = await r.json();
            const select = document.getElementById('assignRoleId');
            select.innerHTML = '<option value="">Select Role</option>' +
                roles.map(r => `<option value="${r.id}">${r.name.toUpperCase()} (ID: ${r.id})</option>`).join('');
        }
    } catch (e) { console.error(e); }
}

async function fetchPermissions() {
    try {
        const r = await fetch(basePath + '/api/permissions', { headers: { 'Authorization': `Bearer ${token}` } });
        const list = document.getElementById('permissionsList');
        if (r.ok) {
            const perms = await r.json();
            if (perms.length === 0) {
                list.innerHTML = '<div class="empty-state">No permissions found.</div>';
                return;
            }
            list.innerHTML = perms.map(p => `
                <div class="card flex-between">
                    <div>
                        <div style="font-weight: 600; color: var(--primary);">${p.code}</div>
                        <div style="font-size: 0.8rem; color: var(--text-secondary);">ID: ${p.id}</div>
                    </div>
                    <button onclick="deletePermission(${p.id})" class="btn-danger btn-small">Delete</button>
                </div>
            `).join('');
        }
    } catch (e) { console.error(e); }
}

document.getElementById('createPermForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const code = document.getElementById('permCode').value.toUpperCase();
    try {
        const r = await fetch(basePath + '/api/permissions', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
            body: JSON.stringify({ code })
        });
        if (r.ok) { document.getElementById('permCode').value = ''; fetchPermissions(); }
        else alert('Failed.');
    } catch (err) { console.error(err); }
});

document.getElementById('assignPermForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const permId = document.getElementById('assignPermId').value;
    const roleId = document.getElementById('assignRoleId').value;
    const action = document.getElementById('assignAction').value;
    if (!roleId) { alert('Please select a role.'); return; }

    const url = action === 'assign'
        ? `${basePath}/api/permissions/${permId}/assign-role/${roleId}`
        : `${basePath}/api/permissions/${permId}/unassign-role/${roleId}`;
    const method = action === 'assign' ? 'POST' : 'DELETE';

    try {
        const r = await fetch(url, { method, headers: { 'Authorization': `Bearer ${token}` } });
        if (r.ok) { alert(`Permission ${action}ed!`); document.getElementById('assignPermId').value = ''; }
        else alert('Failed.');
    } catch (err) { console.error(err); }
});

async function deletePermission(id) {
    if (!confirm('Delete this permission?')) return;
    try {
        const r = await fetch(basePath + `/api/permissions/${id}`, { method: 'DELETE', headers: { 'Authorization': `Bearer ${token}` } });
        if (r.ok) fetchPermissions();
    } catch (e) { console.error(e); }
}

// Initial
fetchUsers();
