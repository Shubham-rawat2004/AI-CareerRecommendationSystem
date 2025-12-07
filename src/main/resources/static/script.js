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
});

function showTab(tabId) {
    document.querySelectorAll('.tab-content').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.nav-tab').forEach(tab => tab.classList.remove('active'));
    document.getElementById(tabId).classList.add('active');
    document.querySelector(`[data-tab="${tabId}"]`).classList.add('active');
}

// Authentication
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
            showMessage('authMessage', 'Registered successfully! You are logged in.', 'success');
        } else {
            showMessage('authMessage', '❌ ' + result.message, 'error');
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
            showMessage('authMessage', `✅ Welcome ${result.data.firstName}!`, 'success');
        } else {
            showMessage('authMessage', '❌ ' + result.message, 'error');
        }
    } catch (error) {
        showMessage('authMessage', '❌ Network error. Is backend running?', 'error');
    }
}

// Profile Management
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
            showMessage('profileMessage', '❌ ' + result.message, 'error');
        }
    } catch (error) {
        showMessage('profileMessage', 'Network error', 'error');
    }
}

async function loadProfile() {
    if (!currentUser) return showMessage('profileMessage', 'Please login first', 'error');
    showMessage('profileMessage', '📥 Profile loaded (feature coming soon)', 'success');
}

// Quiz Management
async function loadQuiz() {
    if (!currentUser) return showMessage('quizResult', 'Please login first', 'error');

    try {
        const response = await fetch(`${API_BASE}/quizzes`);
        const result = await response.json();
        if (result.success && result.data.length > 0) {
            currentQuiz = result.data[0];
            displayQuiz(currentQuiz);
        }
    } catch (error) {
        document.getElementById('quizContent').innerHTML = '<div class="error">Failed to load quiz</div>';
    }
}

function displayQuiz(quiz) {
    let html = `<h3>${quiz.title}</h3><p>${quiz.description}</p>`;
    quiz.questions.forEach((q, index) => {
        html += `
            <div class="quiz-question">
                <h4>Q${index + 1}: ${q.questionText}</h4>
                ${q.options.map((option, optIndex) =>
                    `<label class="quiz-option" onclick="selectQuizAnswer(${q.id}, ${optIndex})">
                        ${optIndex + 1}. ${option}
                    </label>`
                ).join('')}
            </div>
        `;
    });
    html += '<button class="btn submit-quiz-btn" onclick="submitQuiz()" style="display: block; margin-top: 20px;">Submit Quiz</button>';
    document.getElementById('quizContent').innerHTML = html;
}

function selectQuizAnswer(questionId, optionIndex) {
    quizAnswers[questionId] = optionIndex;
    document.querySelectorAll('.quiz-option').forEach(opt => opt.classList.remove('selected'));
    event.target.classList.add('selected');
}

async function submitQuiz() {
    if (!currentQuiz || Object.keys(quizAnswers).length === 0) {
        return showMessage('quizResult', 'Please answer all questions', 'error');
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
                     Quiz Completed!<br>
                    Score: ${result.data.score}/${currentQuiz.questions.length} (${result.data.percentage.toFixed(1)}%)<br>
                    Completed: ${result.data.completedAt}
                </div>
            `;
        }
    } catch (error) {
        showMessage('quizResult', ' Failed to submit quiz', 'error');
    }
}

// Recommendations
async function generateRecommendations() {
    if (!currentUser) return showMessage('recommendationsContent', ' Please login first', 'error');

    document.getElementById('recommendationsContent').innerHTML = '<div class="loading"> Generating AI recommendations...</div>';

    try {
        const response = await fetch(`${API_BASE}/recommendations/generate/${currentUser.id}`);
        const result = await response.json();
        if (result.success) {
            displayRecommendations(result.data);
        }
    } catch (error) {
        document.getElementById('recommendationsContent').innerHTML = '<div class="error">Failed to generate recommendations. Complete your profile first!</div>';
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
                <button class="btn btn-secondary" onclick="rejectRecommendation(${rec.id})"> Reject</button>
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

// History
async function loadHistory() {
    if (!currentUser) return showMessage('historyContent', ' Please login first', 'error');

    document.getElementById('historyContent').innerHTML = '<div class="loading">Loading history...</div>';

    try {
        const recResponse = await fetch(`${API_BASE}/recommendations/user/${currentUser.id}`);
        const recResult = await recResponse.json();

        const quizResponse = await fetch(`${API_BASE}/quizzes/responses/${currentUser.id}`);
        const quizResult = await quizResponse.json();

        displayHistory(recResult.data || [], quizResult.data || []);
    } catch (error) {
        document.getElementById('historyContent').innerHTML = '<div class="error">Failed to load history</div>';
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
    }

    document.getElementById('historyContent').innerHTML = html;
}

function showMessage(elementId, message, type) {
    const element = document.getElementById(elementId);
    element.innerHTML = `<div class="${type}">${message}</div>`;
    setTimeout(() => element.innerHTML = '', 5000);
}
