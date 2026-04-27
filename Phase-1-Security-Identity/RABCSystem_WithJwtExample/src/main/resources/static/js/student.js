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

function showSection(sectionId) {
    document.getElementById('section-my-courses').style.display = 'none';
    document.getElementById('section-explore').style.display = 'none';
    
    document.getElementById('btn-my-courses').classList.remove('active');
    document.getElementById('btn-explore').classList.remove('active');

    document.getElementById(`section-${sectionId}`).style.display = 'block';
    document.getElementById(`btn-${sectionId}`).classList.add('active');

    if (sectionId === 'my-courses') fetchMyCourses();
    if (sectionId === 'explore') fetchAvailableCourses();
}

let enrolledCourseIds = [];

async function fetchMyCourses() {
    try {
        const response = await fetch(basePath + `/api/enrolments/user/${payload.userId}/courses`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const list = document.getElementById('myCoursesList');
        
        if (response.ok) {
            const courses = await response.json();
            enrolledCourseIds = courses.map(c => c.course.id);
            
            if(courses.length === 0) {
                list.innerHTML = '<div class="empty-state">You are not enrolled in any courses yet.</div>';
                return;
            }
            
            list.innerHTML = courses.map(c => {
                let badgeClass = c.status === 'APPROVED' ? 'success' : (c.status === 'REJECTED' ? 'danger' : 'warning');
                return `
                <div class="card" style="border-color: ${c.status === 'APPROVED' ? 'var(--success)' : 'var(--border-color)'};">
                    <div class="flex-between" style="margin-bottom: 0.8rem;">
                        <strong style="font-size: 1.1rem; color: var(--accent);">${c.course.tittle || c.course.title || 'Untitled'}</strong>
                        <span class="badge badge-${badgeClass}">${c.status}</span>
                    </div>
                    <div style="font-size: 0.9rem; color: var(--text-secondary); margin-bottom: 1.5rem; line-height: 1.5;">${c.course.description}</div>
                    
                    ${c.status === 'PENDING' ? '<div style="font-size:0.8rem; color:var(--warning); margin-bottom: 1rem;">Waiting for lecturer approval...</div>' : ''}
                    ${c.status === 'REJECTED' ? '<div style="font-size:0.8rem; color:var(--danger); margin-bottom: 1rem;">Your enrollment was rejected.</div>' : ''}
                    
                    <button onclick="unenrollCourse(${c.course.id})" class="btn-danger btn-small">Unenroll / Cancel</button>
                </div>
            `}).join('');
        }
    } catch (e) { console.error(e); }
}

async function fetchAvailableCourses() {
    // Ensure we have the latest enrolled list to hide already enrolled courses
    await fetchMyCourses(); 
    
    try {
        const response = await fetch(basePath + '/api/courses/active', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        const list = document.getElementById('availableCoursesList');
        
        if (response.ok) {
            const courses = await response.json();
            const available = courses.filter(c => !enrolledCourseIds.includes(c.id));
            
            if(available.length === 0) {
                list.innerHTML = '<div class="empty-state">No new courses available to enroll right now.</div>';
                return;
            }
            
            list.innerHTML = available.map(c => `
                <div class="card" style="border-color: var(--primary);">
                    <div class="flex-between" style="margin-bottom: 0.8rem;">
                        <strong style="font-size: 1.1rem;">${c.tittle || c.title || 'Untitled'}</strong>
                        <span class="badge badge-primary">New</span>
                    </div>
                    <div style="font-size: 0.9rem; color: var(--text-secondary); margin-bottom: 1.5rem; line-height: 1.5;">${c.description}</div>
                    <button onclick="enrollCourse(${c.id})" class="btn-success btn-small" style="width:100%;">Enroll Now</button>
                </div>
            `).join('');
        }
    } catch (e) { console.error(e); }
}

async function enrollCourse(courseId) {
    if(!confirm('Do you want to enroll in this course?')) return;
    try {
        const response = await fetch(basePath + '/api/enrolments', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
            body: JSON.stringify({ userId: payload.userId, courseId: courseId })
        });
        
        if (response.ok) {
            alert('Successfully enrolled! Waiting for lecturer approval.');
            showSection('my-courses');
        } else {
            if(response.status === 403) alert('Access Denied: You do not have permission to enroll.');
            else alert('Failed to enroll.');
        }
    } catch (e) { console.error(e); }
}

async function unenrollCourse(courseId) {
    if(!confirm('Are you sure you want to unenroll from this course?')) return;
    try {
        const response = await fetch(basePath + '/api/enrolments', {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
            body: JSON.stringify({ userId: payload.userId, courseId: courseId })
        });
        
        if (response.ok) fetchMyCourses();
        else alert('Failed to unenroll.');
    } catch (e) { console.error(e); }
}

// Initial Load
fetchMyCourses();
