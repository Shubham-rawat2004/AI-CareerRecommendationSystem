const API_BASE = 'http://localhost:8080';
let currentUser = null;
let currentQuiz = null;
let quizAnswers = {};

// Tab Navigation
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.nav-tab').forEach(tab => {
        tab.addEventListener('click', function() {
            const tabId = this.getAttribute('data-tab');
            showTab(tabId);
        });
    });
    updateUserDisplay(); // NEW: Initialize user display
});

function showTab(tabId) {
    document.querySelectorAll('.tab-content').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.nav-tab').forEach(tab => tab.classList.remove('active'));
    document.getElementById(tabId).classList.add('active');
    document.querySelector(`[data-tab="${tabId}"]`).classList.add('active');
}

//  NEW: User Display Management
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

//  NEW: Logout Function
function logoutUser() {
    currentUser = null;
    quizAnswers = {};
    updateUserDisplay();
    showMessage('authMessage', 'Logged out successfully', 'success');
    showTab('login');
}

// Authentication - UPDATED
async function registerUser() {
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;

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
            showMessage('authMessage', 'Registered successfully! You are logged in.', 'success');
            showTab('profile');
        } else {
            showMessage('authMessage', result.message, 'error');
        }
    } catch (error) {
        showMessage('authMessage', 'Network error. Is backend running?', 'error');
    }
}

async function loginUser() {
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;

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

// Profile Management - FIXED
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
            showMessage('profileMessage', ' Profile loaded successfully!', 'success');
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
        phoneNumber: formData.get('phoneNumber'),
        academicBranch: formData.get('academicBranch'),
        cgpa: parseFloat(formData.get('cgpa')),
        bio: formData.get('bio'),
        skills: formData.get('skills') ? formData.get('skills').split(',').map(s => s.trim()).filter(s => s) : [],
        interests: formData.get('interests') ? formData.get('interests').split(',').map(s => s.trim()).filter(s => s) : []
    };

    try {
        const response = await fetch(`${API_BASE}/student-profiles/user/${currentUser.id}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(profileData)
        });
        const result = await response.json();
        if (result.success) {
            showMessage('profileMessage', 'Profile saved successfully!', 'success');
        } else {
            showMessage('profileMessage', result.message, 'error');
        }
    } catch (error) {
        showMessage('profileMessage', 'Network error', 'error');
    }
}

//  FIXED QUIZ FUNCTIONS
async function loadQuiz() {
    if (!currentUser) return showMessage('quizResult', 'Please login first', 'error');

    try {
        const response = await fetch(`${API_BASE}/quizzes`);
        const result = await response.json();
        if (result.success && result.data.length > 0) {
            currentQuiz = result.data[0];
            displayQuiz(currentQuiz);
        } else {
            document.getElementById('quizContent').innerHTML = '<div class="error">No quizzes available</div>';
        }
    } catch (error) {
        document.getElementById('quizContent').innerHTML = '<div class="error">Failed to load quiz</div>';
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
                <label class="quiz-option" data-qid="${q.id}" data-opt="${optIndex}" onclick="selectQuizAnswer(${q.id}, ${optIndex})">
                    ${optIndex + 1}. ${option}
                </label>`;
        });
        html += `</div></div>`;
    });

    html += '<button class="btn" onclick="submitQuiz()" style="display: block; margin: 20px auto;">Submit Quiz</button>';
    document.getElementById('quizContent').innerHTML = html;
    quizAnswers = {}; // Reset answers
}

function selectQuizAnswer(questionId, optionIndex) {
    quizAnswers[questionId] = optionIndex;

    // Target ONLY current question's options
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
        return document.getElementById('quizResult').innerHTML = '<div class="error">Please load quiz first</div>';
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
                </div>
            `;
            quizAnswers = {}; // Reset for next time
        } else {
            document.getElementById('quizResult').innerHTML = `<div class="error">${result.message}</div>`;
        }
    } catch (error) {
        document.getElementById('quizResult').innerHTML = '<div class="error">Failed to submit quiz</div>';
    }
}

// Recommendations
async function generateRecommendations() {
    const container = document.getElementById('recommendationsContent');

    if (!currentUser) {
        container.innerHTML = '<div class="error">Please login first.</div>';
        return;
    }

    container.innerHTML = '<div class="loading">Generating AI recommendations...</div>';

    try {
        const response = await fetch(`${API_BASE}/recommendations/generate/${currentUser.id}`, {
            method: 'POST'
        });
        const result = await response.json();

        if (response.ok && result.success) {
            displayRecommendations(result.data);
        } else {
            container.innerHTML = `<div class="error">${result.message || 'Failed to generate recommendations.'}</div>`;
        }
    } catch (error) {
        container.innerHTML = '<div class="error">Failed to connect to server. Check if backend is running.</div>';
    }
}

function displayRecommendations(recommendations) {
    let html = '<h3>Top Career Recommendations</h3>';
    recommendations.forEach(rec => {
        const scoreClass = rec.matchScore >= 80 ? 'excellent' : rec.matchScore >= 60 ? 'good' : '';
        const scoreBadge = rec.matchScore >= 90 ? 'score-90' : rec.matchScore >= 70 ? 'score-70' : 'score-50';
        html += `
            <div class="card recommendation-card ${scoreClass}">
                <h4>${rec.careerName}</h4>
                <span class="score-badge ${scoreBadge}">${rec.matchScore.toFixed(1)}% Match</span>
                <p><strong>Why:</strong> ${rec.matchReason}</p>
                <button class="btn" onclick="acceptRecommendation(${rec.id})">Accept</button>
                <button class="btn btn-secondary" onclick="rejectRecommendation(${rec.id})">Reject</button>
            </div>
        `;
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
        await fetch(`${API_BASE}/recommendations/${recId}/status?status=${status}`, { method: 'PUT' });
        generateRecommendations();
    } catch (error) {
        console.error('Failed to update status');
    }
}

// History - FIXED with Promise.all
async function loadHistory() {
    if (!currentUser) {
        document.getElementById('historyContent').innerHTML = '<div class="error">Please login first</div>';
        return;
    }

    const container = document.getElementById('historyContent');
    container.innerHTML = '<div class="loading">Loading your complete history...</div>';

    try {
        const [recResponse, quizResponse] = await Promise.all([
            fetch(`${API_BASE}/recommendations/user/${currentUser.id}`),
            fetch(`${API_BASE}/quizzes/responses/${currentUser.id}`)
        ]);

        const recResult = await recResponse.json();
        const quizResult = await quizResponse.json();

        displayHistory(recResult.data || [], quizResult.data || []);
    } catch (error) {
        container.innerHTML = '<div class="error">Failed to load history. Is backend running?</div>';
    }
}

function displayHistory(recommendations, quizzes) {
    let html = '<h3>📈 Activity History</h3>';

    if (recommendations.length > 0) {
        html += '<h4>Recommendations</h4>';
        recommendations.slice(0, 5).forEach(rec => {
            html += `<div class="history-item">
                <span>${rec.careerName} - ${rec.matchScore.toFixed(1)}%</span>
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
    } else {
        html += '<p>No history yet. Complete some quizzes or generate recommendations!</p>';
    }

    document.getElementById('historyContent').innerHTML = html;
}

function showMessage(elementId, message, type) {
    const element = document.getElementById(elementId);
    element.innerHTML = `<div class="${type}">${message}</div>`;
    setTimeout(() => element.innerHTML = '', 5000);
}
