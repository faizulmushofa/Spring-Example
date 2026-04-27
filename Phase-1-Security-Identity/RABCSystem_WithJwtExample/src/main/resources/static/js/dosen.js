const token = localStorage.getItem('token');
if (!token) window.location.href = 'login.html';

const basePath = 'http://localhost:8080';
let isEditMode = false;

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

async function fetchCourses() {
    try {
        const response = await fetch(basePath + '/api/courses/my-courses', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const list = document.getElementById('coursesList');
        if (response.ok) {
            const courses = await response.json();
            if(courses.length === 0) {
                list.innerHTML = '<div class="empty-state">You have not published any courses yet.</div>';
                return;
            }
            list.innerHTML = courses.map(c => `
                <div class="card" style="border-color: ${c.active ? 'var(--border-color)' : 'rgba(239, 68, 68, 0.4)'}; margin-bottom: 1.5rem;">
                    <div class="flex-between" style="margin-bottom: 0.8rem;">
                        <strong style="font-size: 1.1rem;">${c.tittle || c.title || 'Untitled'}</strong>
                        ${c.active ? '<span class="badge badge-success">Active</span>' : '<span class="badge badge-danger">Inactive</span>'}
                    </div>
                    <div style="font-size: 0.9rem; color: var(--text-secondary); margin-bottom: 1.5rem; line-height: 1.5;">${c.description}</div>
                    <div class="flex-gap" style="flex-wrap: wrap;">
                        <button onclick="viewEnrolledUsers(${c.id}, '${c.tittle || c.title || ''}')" class="btn-small" style="background:var(--accent);">Students</button>
                        <button onclick="editCourse(${c.id}, '${c.tittle || c.title || ''}', '${c.description}')" class="btn-small" style="background:var(--primary);">Edit</button>
                        ${c.active ? `<button onclick="deActiveCourse(${c.id})" class="btn-danger btn-small">Deactivate</button>` : ''}
                    </div>
                </div>
            `).join('');
        }
    } catch (e) { console.error(e); }
}

document.getElementById('courseForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('courseId').value;
    const tittle = document.getElementById('courseTitle').value;
    const description = document.getElementById('courseDesc').value;
    const msg = document.getElementById('msg');

    try {
        const url = isEditMode ? `${basePath}/api/courses/${id}` : `${basePath}/api/courses`;
        const method = isEditMode ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
            body: JSON.stringify({ tittle, description, active: true })
        });
        
        if (response.ok) {
            msg.style.color = 'var(--success)';
            msg.innerText = isEditMode ? 'Course updated successfully!' : 'Course published successfully!';
            document.getElementById('courseForm').reset();
            if(isEditMode) cancelEdit();
            fetchCourses();
            setTimeout(() => msg.innerText='', 3000);
        } else {
            msg.style.color = 'var(--danger)';
            if (response.status === 403) msg.innerText = 'Access Denied: You do not have MAKE_COURSE permission.';
            else msg.innerText = 'Operation failed. Please try again.';
        }
    } catch (err) { console.error(err); }
});

function editCourse(id, title, desc) {
    isEditMode = true;
    document.getElementById('courseId').value = id;
    document.getElementById('courseTitle').value = title;
    document.getElementById('courseDesc').value = desc;
    document.getElementById('formTitle').innerText = 'Update Course';
    document.getElementById('submitBtn').innerText = 'Save Changes';
    document.getElementById('cancelBtn').style.display = 'block';
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function cancelEdit() {
    isEditMode = false;
    document.getElementById('courseForm').reset();
    document.getElementById('formTitle').innerText = 'Create New Course';
    document.getElementById('submitBtn').innerText = 'Publish Course';
    document.getElementById('cancelBtn').style.display = 'none';
}

async function deActiveCourse(courseId) {
    if(!confirm('Are you sure you want to deactivate this course?')) return;
    try {
        const response = await fetch(basePath + `/api/courses/${courseId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if(response.ok) fetchCourses();
    } catch (e) { console.error(e); }
}

async function viewEnrolledUsers(courseId, title) {
    try {
        const response = await fetch(basePath + `/api/enrolments/course/${courseId}/users`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (response.ok) {
            const enrolments = await response.json();
            const card = document.getElementById('enrolledUsersCard');
            const list = document.getElementById('enrolledUsersList');
            document.getElementById('enrolledUsersTitle').innerText = `Students in: ${title}`;
            
            if(enrolments.length === 0) {
                list.innerHTML = '<div class="empty-state" style="padding:1.5rem;">No students enrolled yet.</div>';
            } else {
                list.innerHTML = enrolments.map(e => {
                    let badgeClass = e.status === 'APPROVED' ? 'success' : (e.status === 'REJECTED' ? 'danger' : 'warning');
                    return `
                    <div style="padding: 1rem; border-bottom: 1px solid var(--border-color); display: flex; justify-content: space-between; align-items: center; background: rgba(0,0,0,0.2); border-radius: 8px; margin-bottom: 0.5rem;">
                        <div>
                            <div style="font-weight: 500; color: var(--text-main); margin-bottom: 0.3rem;">${e.user.email}</div>
                            <span class="badge badge-${badgeClass}">${e.status}</span>
                        </div>
                        ${e.status === 'PENDING' ? `
                            <div class="flex-gap">
                                <button onclick="updateEnrolment(${e.enrolmentId}, 'approve')" class="btn-success btn-small">Approve</button>
                                <button onclick="updateEnrolment(${e.enrolmentId}, 'reject')" class="btn-danger btn-small">Reject</button>
                            </div>
                        ` : ''}
                    </div>
                `}).join('');
            }
            card.style.display = 'block';
            card.scrollIntoView({ behavior: 'smooth' });
        }
    } catch (e) { console.error(e); }
}

async function updateEnrolment(enrolmentId, action) {
    try {
        const response = await fetch(basePath + `/api/enrolments/${enrolmentId}/${action}`, {
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (response.ok) {
            alert(`Student successfully ${action}d!`);
            document.getElementById('enrolledUsersCard').style.display = 'none';
        } else {
            alert('Failed to update student status.');
        }
    } catch (e) { console.error(e); }
}

fetchCourses();
