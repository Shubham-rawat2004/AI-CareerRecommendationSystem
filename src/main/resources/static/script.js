const API_BASE = 'http://localhost:8080';
let currentUser = null;
let currentQuiz = null;
let quizAnswers = {};
let skillsQuizData = {};
let userSkills = [];

// Tab Navigation + Event Delegation
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.nav-tab').forEach(tab => {
        tab.addEventListener('click', function () {
            const tabId = this.getAttribute('data-tab');
            showTab(tabId);
        });
    });

    // Delegation for skills quiz options
    document.addEventListener('click', function (e) {
        if (e.target.classList.contains('quiz-option')) {
            const skill = e.target.getAttribute('data-skill');
            const level = e.target.getAttribute('data-level');
            if (skill && level) {
                selectSkillsAnswer(skill, level, e);
            }
        }
    });

    updateUserDisplay();
});

function showTab(tabId) {
    document.querySelectorAll('.tab-content').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.nav-tab').forEach(tab => tab.classList.remove('active'));
    document.getElementById(tabId).classList.add('active');
    document.querySelector(`[data-tab="${tabId}"]`).classList.add('active');
}

// User Display Management
function updateUserDisplay() {
    const greeting = document.getElementById('userGreeting');
    const userActions = document.getElementById('userActions');
    const userName = document.getElementById('userName');

    if (currentUser) {
        greeting.textContent = `Welcome back, ${currentUser.firstName}!`;
        userName.textContent = `${currentUser.firstName} ${currentUser.lastName}`;
        userActions.style.display = 'flex';
    } else {
        greeting.textContent = 'Discover your perfect career path with AI-powered insights';
        userActions.style.display = 'none';
    }
}

// Logout Function (with full refresh)
function logoutUser() {
    currentUser = null;
    quizAnswers = {};
    skillsQuizData = {};
    userSkills = [];
    updateUserDisplay();
    showMessage('authMessage', 'Logged out successfully', 'success');

    setTimeout(() => {
        window.location.reload();
    }, 300);
}

// Authentication
async function registerUser() {
    const email = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value;

    if (!email || !password) {
        return showMessage('authMessage', 'Please fill all fields', 'error');
    }

    try {
        const response = await fetch(`${API_BASE}/users/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email, password,
                firstName: 'Student',
                lastName: email.split('@')[0],
                role: 'STUDENT'
            })
        });
        const result = await response.json();
        if (result.success) {
            currentUser = result.data;
            updateUserDisplay();
            showMessage('authMessage', 'Registered successfully! Complete your profile →', 'success');
            showTab('profile');
        } else {
            showMessage('authMessage', result.message, 'error');
        }
    } catch (error) {
        showMessage('authMessage', 'Network error. Is backend running on port 8080?', 'error');
    }
}

async function loginUser() {
    const email = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value;

    if (!email || !password) {
        return showMessage('authMessage', 'Please fill all fields', 'error');
    }

    try {
        const response = await fetch(`${API_BASE}/users/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const result = await response.json();
        if (result.success) {
            currentUser = result.data;
            updateUserDisplay();
            showMessage('authMessage', `Welcome ${result.data.firstName}!`, 'success');
            showTab('profile');
        } else {
            showMessage('authMessage', result.message, 'error');
        }
    } catch (error) {
        showMessage('authMessage', 'Network error. Is backend running?', 'error');
    }
}

// Profile Management
async function loadProfile() {
    if (!currentUser) return showMessage('profileMessage', 'Please login first', 'error');

    try {
        const response = await fetch(`${API_BASE}/student-profiles/user/${currentUser.id}`);
        const result = await response.json();

        if (result.success && result.data) {
            const profile = result.data;
            document.getElementById('phoneNumber').value = profile.phoneNumber || '';
            document.getElementById('academicBranch').value = profile.academicBranch || '';
            document.getElementById('cgpa').value = profile.cgpa || '';
            document.getElementById('bio').value = profile.bio || '';
            document.getElementById('skills').value = profile.skills?.join(', ') || '';
            document.getElementById('interests').value = profile.interests?.join(', ') || '';
            showMessage('profileMessage', '✅ Profile loaded successfully!', 'success');
        } else {
            showMessage('profileMessage', 'No profile found. Create one first.', 'info');
        }
    } catch (error) {
        showMessage('profileMessage', 'Failed to load profile', 'error');
    }
}

async function saveProfile() {
    if (!currentUser) return showMessage('profileMessage', 'Please login first', 'error');

    const formData = new FormData(document.getElementById('profileForm'));
    const profileData = {
        phoneNumber: formData.get('phoneNumber') || '',
        academicBranch: formData.get('academicBranch') || '',
        cgpa: parseFloat(formData.get('cgpa')) || 0,
        bio: formData.get('bio') || '',
        skills: formData.get('skills')
            ? formData.get('skills').split(',').map(s => s.trim()).filter(s => s)
            : [],
        interests: formData.get('interests')
            ? formData.get('interests').split(',').map(s => s.trim()).filter(s => s)
            : []
    };

    try {
        const response = await fetch(`${API_BASE}/student-profiles/user/${currentUser.id}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(profileData)
        });
        const result = await response.json();
        if (result.success) {
            showMessage('profileMessage', '✅ Profile saved! Now try Skills Quiz →', 'success');
            showTab('skillsTab');
        } else {
            showMessage('profileMessage', result.message, 'error');
        }
    } catch (error) {
        showMessage('profileMessage', 'Network error', 'error');
    }
}

// Skills Quiz
async function loadSkillsQuiz() {
    if (!currentUser) {
        document.getElementById('skillsQuizResult').innerHTML =
            '<div class="error">Please login first</div>';
        return;
    }

    document.getElementById('skillsQuizContent').innerHTML =
        '<div class="loading">Loading your skills from profile...</div>';

    try {
        const response = await fetch(`${API_BASE}/student-profiles/user/${currentUser.id}`);
        const result = await response.json();

        if (result.success && result.data && result.data.skills && result.data.skills.length > 0) {
            userSkills = result.data.skills;
            displaySkillsQuiz(userSkills);
        } else {
            document.getElementById('skillsQuizContent').innerHTML = `
                <div class="error">
                    <p>No skills found in your profile!</p>
                    <button class="btn" onclick="showTab('profile')">📝 Add Skills First</button>
                </div>`;
        }
    } catch (error) {
        document.getElementById('skillsQuizContent').innerHTML =
            '<div class="error">Failed to load profile</div>';
    }
}

function displaySkillsQuiz(skills) {
    let html = `<h3>📊 Rate Your ${skills.length} Skills</h3>
                <div id="skillsProgress" class="progress-ongoing">
                    Rated 0/${skills.length} skills
                </div>`;

    skills.forEach(skill => {
        html += `
            <div class="quiz-question" data-skill="${skill}">
                <h4>🔧 ${skill}</h4>
                <div class="question-options">
                    <label class="quiz-option" data-skill="${skill}" data-level="beginner">
                        🟡 Beginner
                    </label>
                    <label class="quiz-option" data-skill="${skill}" data-level="intermediate">
                        🟢 Intermediate
                    </label>
                    <label class="quiz-option" data-skill="${skill}" data-level="advanced">
                        🟣 Advanced (🚀 Career Boost!)
                    </label>
                </div>
            </div>`;
    });

    html += '<button class="btn" onclick="submitSkillsQuiz()" style="display: block; margin: 20px auto;">✅ Submit Skills Assessment</button>';
    document.getElementById('skillsQuizContent').innerHTML = html;
    skillsQuizData = {};
    updateSkillsProgress();
}

function selectSkillsAnswer(skill, level, e) {
    skillsQuizData[skill] = level;

    const skillDiv = e.target.closest('.quiz-question');
    const options = skillDiv.querySelectorAll('.quiz-option');

    options.forEach(option => option.classList.remove('selected'));
    e.target.classList.add('selected');

    updateSkillsProgress();
}

function updateSkillsProgress() {
    const rated = Object.keys(skillsQuizData).length;
    const total = userSkills.length;
    const progress = document.getElementById('skillsProgress');

    if (progress) {
        progress.textContent = `Rated ${rated}/${total} skills`;
        if (rated === total) {
            progress.className = 'progress-complete';
            progress.textContent += '  Ready to submit! 🚀';
        } else {
            progress.className = 'progress-ongoing';
        }
    }
}

async function submitSkillsQuiz() {
    if (Object.keys(skillsQuizData).length < userSkills.length) {
        return document.getElementById('skillsQuizResult').innerHTML =
            `<div class="error">Please rate all ${userSkills.length} skills! (${Object.keys(skillsQuizData).length} rated)</div>`;
    }

    try {
        const response = await fetch(`${API_BASE}/skill-quizzes/submit/${currentUser.id}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(skillsQuizData)
        });
        const result = await response.json();

        if (result.success) {
            document.getElementById('skillsQuizResult').innerHTML = `
                <div class="success">
                    <h3>✅ Skills Assessment Completed!</h3>
                    <p><strong>Your advanced skills will boost relevant careers by 50%!</strong></p>
                    <button class="btn" onclick="showTab('recommendations')">🎯 Get Updated Recommendations</button>
                </div>`;
        } else {
            document.getElementById('skillsQuizResult').innerHTML =
                `<div class="error">${result.message}</div>`;
        }
    } catch (error) {
        document.getElementById('skillsQuizResult').innerHTML =
            '<div class="error">Failed to submit. Is backend running?</div>';
    }
}

// Career Quiz
async function loadQuiz() {
    if (!currentUser) return showMessage('quizResult', 'Please login first', 'error');

    try {
        const response = await fetch(`${API_BASE}/quizzes`);
        const result = await response.json();
        if (result.success && result.data.length > 0) {
            currentQuiz = result.data[0];
            displayQuiz(currentQuiz);
        } else {
            document.getElementById('quizContent').innerHTML =
                '<div class="error">No quizzes available</div>';
        }
    } catch (error) {
        document.getElementById('quizContent').innerHTML =
            '<div class="error">Failed to load quiz</div>';
    }
}

function displayQuiz(quiz) {
    let html = `<h3>${quiz.title}</h3><p>${quiz.description}</p>
                <div id="quizProgress" class="progress-ongoing">Question 1 of ${quiz.questions.length}</div>`;

    quiz.questions.forEach((q, qIndex) => {
        html += `
            <div class="quiz-question" data-question-id="${q.id}">
                <h4>Q${qIndex + 1}: ${q.questionText}</h4>
                <div class="question-options">`;

        q.options.forEach((option, optIndex) => {
            html += `
                <label class="quiz-option" data-qid="${q.id}" data-opt="${optIndex}"
                       onclick="selectQuizAnswer(${q.id}, ${optIndex})">
                    ${optIndex + 1}. ${option}
                </label>`;
        });
        html += `</div></div>`;
    });

    html += '<button class="btn" onclick="submitQuiz()" style="display: block; margin: 20px auto;">Submit Quiz</button>';
    document.getElementById('quizContent').innerHTML = html;
    quizAnswers = {};
}

function selectQuizAnswer(questionId, optionIndex) {
    quizAnswers[questionId] = optionIndex;

    const currentQuestion = document.querySelector(`.quiz-question[data-question-id="${questionId}"]`);
    const options = currentQuestion.querySelectorAll('.quiz-option');

    options.forEach(option => {
        const optIndex = parseInt(option.getAttribute('data-opt'));
        if (optIndex === optionIndex) {
            option.classList.add('selected');
        } else {
            option.classList.remove('selected');
        }
    });

    updateQuizProgress();
}

function updateQuizProgress() {
    const answered = Object.keys(quizAnswers).length;
    const total = currentQuiz ? currentQuiz.questions.length : 0;
    const progress = document.getElementById('quizProgress');

    if (progress) {
        progress.textContent = `Answered ${answered}/${total} questions`;
        if (answered === total) {
            progress.className = 'progress-complete';
            progress.textContent += '  Ready to submit!';
        } else {
            progress.className = 'progress-ongoing';
        }
    }
}

async function submitQuiz() {
    if (!currentQuiz) {
        return document.getElementById('quizResult').innerHTML =
            '<div class="error">Please load quiz first</div>';
    }

    const answeredCount = Object.keys(quizAnswers).length;
    if (answeredCount < currentQuiz.questions.length) {
        return document.getElementById('quizResult').innerHTML =
            `<div class="error">Please answer all ${currentQuiz.questions.length} questions (you answered ${answeredCount})</div>`;
    }

    try {
        const response = await fetch(`${API_BASE}/quizzes/submit/${currentUser.id}/${currentQuiz.id}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ answers: quizAnswers })
        });
        const result = await response.json();

        if (result.success) {
            document.getElementById('quizResult').innerHTML = `
                <div class="success">
                    <h3>🎉 Quiz Completed!</h3>
                    <p><strong>Score:</strong> ${result.data.score}/${currentQuiz.questions.length}</p>
                    <p><strong>Percentage:</strong> ${result.data.percentage.toFixed(1)}%</p>
                    <p><strong>Completed:</strong> ${new Date(result.data.completedAt).toLocaleString()}</p>
                </div>`;
            quizAnswers = {};
        } else {
            document.getElementById('quizResult').innerHTML =
                `<div class="error">${result.message}</div>`;
        }
    } catch (error) {
        document.getElementById('quizResult').innerHTML =
            '<div class="error">Failed to submit quiz</div>';
    }
}

// Recommendations
async function generateRecommendations() {
    const container = document.getElementById('recommendationsContent');

    if (!currentUser) {
        container.innerHTML = '<div class="error">Please login first.</div>';
        return;
    }

    container.innerHTML =
        '<div class="loading">🔥 Generating AI recommendations with skills boost...</div>';

    try {
        const response = await fetch(`${API_BASE}/recommendations/generate/${currentUser.id}`, {
            method: 'POST'
        });
        const result = await response.json();

        if (response.ok && result.success) {
            displayRecommendations(result.data);
        } else {
            container.innerHTML =
                `<div class="error">${result.message || 'Failed to generate recommendations.'}</div>`;
        }
    } catch (error) {
        container.innerHTML =
            '<div class="error">Backend not running? Check localhost:8080</div>';
    }
}

function displayRecommendations(recommendations) {
    let html = '<h3>🚀 Top Career Recommendations</h3>';
    if (recommendations.length === 0) {
        html += '<div class="info">No recommendations yet. Complete Skills Quiz first!</div>';
        document.getElementById('recommendationsContent').innerHTML = html;
        return;
    }

    recommendations.forEach(rec => {
        html += `
            <div class="card recommendation-card">
                <h4>${rec.careerName}</h4>
                <span class="score-badge">${rec.matchScore.toFixed(1)}% Match</span>
                <p><strong>Why:</strong> ${rec.matchReason}</p>
                <button class="btn" onclick="acceptRecommendation(${rec.id})">✅ Accept</button>
                <button class="btn btn-secondary" onclick="rejectRecommendation(${rec.id})">❌ Reject</button>
            </div>`;
    });
    document.getElementById('recommendationsContent').innerHTML = html;
}

async function acceptRecommendation(recId) {
    await updateRecommendationStatus(recId, 'ACCEPTED');
}

async function rejectRecommendation(recId) {
    await updateRecommendationStatus(recId, 'REJECTED');
}

async function updateRecommendationStatus(recId, status) {
    try {
        const response = await fetch(
            `${API_BASE}/recommendations/${recId}/status?status=${status}`,
            { method: 'PUT' }
        );

        if (!response.ok) {
            console.error('Failed to update status, HTTP', response.status);
            return;
        }

        await generateRecommendations();
        await loadHistory();
        showTab('history');
    } catch (error) {
        console.error('Failed to update status', error);
    }
}

// History
async function loadHistory() {
    if (!currentUser) {
        document.getElementById('historyContent').innerHTML =
            '<div class="error">Please login first</div>';
        return;
    }

    const container = document.getElementById('historyContent');
    container.innerHTML = '<div class="loading">Loading your complete history...</div>';

    try {
        const [latestAcceptedResponse, recResponse, quizResponse] = await Promise.all([
            fetch(`${API_BASE}/recommendations/user/${currentUser.id}/latest-accepted`),
            fetch(`${API_BASE}/recommendations/user/${currentUser.id}`),
            fetch(`${API_BASE}/quizzes/responses/${currentUser.id}`)
        ]);

        const latestAcceptedResult = latestAcceptedResponse.ok
            ? await latestAcceptedResponse.json()
            : { success: false, data: null };

        const recResult = recResponse.ok
            ? await recResponse.json()
            : { success: false, data: [] };

        const quizResult = quizResponse.ok
            ? await quizResponse.json()
            : { success: false, data: [] };

        const latestAccepted = latestAcceptedResult && latestAcceptedResult.success
            ? latestAcceptedResult.data
            : null;

        const recData = recResult && recResult.success && Array.isArray(recResult.data)
            ? recResult.data
            : [];

        const quizData = quizResult && quizResult.success && Array.isArray(quizResult.data)
            ? quizResult.data
            : [];

        displayHistory(recData, quizData, latestAccepted);
    } catch (error) {
        container.innerHTML =
            '<div class="error">Failed to load history. Check if backend is running.</div>';
    }
}

function displayHistory(recommendations, quizzes, latestAccepted) {
    let html = '<h3>📈 Activity History</h3>';

    if (latestAccepted) {
        html += `
            <h4>Latest Accepted Career</h4>
            <div class="history-item" style="border-left: 5px solid #10b981;">
                <span>${latestAccepted.careerName} - ${latestAccepted.matchScore.toFixed(1)}% (ACCEPTED)</span>
                <small>${new Date(latestAccepted.createdAt).toLocaleDateString()}</small>
            </div>
        `;
    }

    if (recommendations.length > 0) {
        html += '<h4>Recent Recommendations</h4>';
        recommendations.slice(0, 5).forEach(rec => {
            html += `<div class="history-item">
                <span>${rec.careerName} - ${rec.matchScore.toFixed(1)}% (${rec.status})</span>
                <small>${new Date(rec.createdAt).toLocaleDateString()}</small>
            </div>`;
        });
    }

    if (quizzes.length > 0) {
        html += '<h4>Quizzes</h4>';
        quizzes.slice(0, 5).forEach(quiz => {
            html += `<div class="history-item">
                <span>Quiz - ${quiz.percentage.toFixed(1)}%</span>
                <small>${new Date(quiz.completedAt).toLocaleDateString()}</small>
            </div>`;
        });
    }

    if (!latestAccepted && recommendations.length === 0 && quizzes.length === 0) {
        html += '<p>No history yet. Complete some quizzes or generate recommendations!</p>';
    }

    document.getElementById('historyContent').innerHTML = html;
}

// Message Utility
function showMessage(elementId, message, type) {
    const element = document.getElementById(elementId);
    element.innerHTML = `<div class="${type}">${message}</div>`;
    setTimeout(() => element.innerHTML = '', 5000);
}
