// Global State
let appState = {
    currentUser: null,
    currentKnowledgeCard: null,
    userDomains: [],
    userProgress: {},
    isCardFlipped: false,
    cardStartTime: 0,
};

const API_BASE = 'https://knowtify-bb7d.onrender.com/api';

// Initialize App
document.addEventListener('DOMContentLoaded', () => {
    initializeParticles();
    checkUserAuth();
    setupCardFlip();
});

// Particle Animation
function initializeParticles() {
    const container = document.querySelector('.particles-container');
    if (!container) return;

    for (let i = 0; i < 5; i++) {
        const particle = document.createElement('div');
        particle.style.position = 'absolute';
        particle.style.width = Math.random() * 300 + 100 + 'px';
        particle.style.height = particle.style.width;
        particle.style.borderRadius = '50%';
        particle.style.left = Math.random() * 100 + '%';
        particle.style.top = Math.random() * 100 + '%';
        particle.style.opacity = '0.05';
        particle.style.pointerEvents = 'none';
        particle.style.filter = `blur(${Math.random() * 50}px)`;
    }
}

// Authentication
async function handleRegister(event) {
    event.preventDefault();
    showLoading(true);

    const username = document.getElementById('regUsername').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    const fullName = document.getElementById('regFullName').value;

    try {
        const response = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password, fullName }),
        });

        const data = await response.json();

        if (response.ok) {
            console.log('User registered successfully:', data);
            showToast('✅ Account created! Please sign in with your credentials.');

            // Clear any previous login data
            localStorage.removeItem('knowtifyUser');
            appState.currentUser = null;

            // Redirect to login page
            navigateTo('login');

            // Clear the registration form
            document.getElementById('regUsername').value = '';
            document.getElementById('regEmail').value = '';
            document.getElementById('regPassword').value = '';
            document.getElementById('regFullName').value = '';
        } else {
            showToast(data.message || 'Registration failed');
        }
    } catch (error) {
        showToast('Error: ' + error.message);
    } finally {
        showLoading(false);
    }
}

async function handleLogin(event) {
    event.preventDefault();
    showLoading(true);

    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;

    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
        });

        const data = await response.json();

        if (response.ok) {
            // Ensure userId is set
            data.userId = data.userId || data.id;
            appState.currentUser = data;
            console.log('User logged in:', appState.currentUser);
            localStorage.setItem('knowtifyUser', JSON.stringify(data));
            showToast('Login successful!');

            // Check if user has selected domains
            if (data.preferredDomains && data.preferredDomains.length > 0) {
                appState.userDomains = Array.from(data.preferredDomains);
                localStorage.setItem('userDomains_' + data.userId, JSON.stringify(appState.userDomains));
                navigateTo('dashboard');
                loadDashboard();
            } else {
                // No domains selected - show onboarding
                navigateTo('onboarding');
            }
        } else {
            showToast(data.message || 'Login failed');
        }
    } catch (error) {
        showToast('Error: ' + error.message);
    } finally {
        showLoading(false);
    }
}

function checkUserAuth() {
    const savedUser = localStorage.getItem('knowtifyUser');
    if (savedUser) {
        appState.currentUser = JSON.parse(savedUser);

        // Ensure userId is set
        appState.currentUser.userId = appState.currentUser.userId || appState.currentUser.id;

        // Load streak from localStorage
        const currentStreak = parseInt(localStorage.getItem('currentStreak_' + appState.currentUser.userId)) || 0;
        const longestStreak = parseInt(localStorage.getItem('longestStreak_' + appState.currentUser.userId)) || 0;
        appState.currentUser.currentStreak = currentStreak;
        appState.currentUser.longestStreak = longestStreak;

        // Check if streak should be reset (more than 24 hours since last learning)
        checkStreakStatus(appState.currentUser.userId);

        // Load user domains from localStorage
        const savedDomains = localStorage.getItem('userDomains_' + appState.currentUser.userId);
        if (savedDomains) {
            appState.userDomains = JSON.parse(savedDomains);
            console.log('Loaded domains from localStorage:', appState.userDomains);
        } else if (appState.currentUser.preferredDomains && appState.currentUser.preferredDomains.length > 0) {
            appState.userDomains = Array.from(appState.currentUser.preferredDomains);
            console.log('Loaded domains from user object:', appState.userDomains);
            localStorage.setItem('userDomains_' + appState.currentUser.userId, JSON.stringify(appState.userDomains));
        }

        console.log('User authenticated. UserId:', appState.currentUser.userId, 'Domains:', appState.userDomains, 'Streak:', currentStreak);
        navigateTo('dashboard');
        loadDashboard();
    } else {
        navigateTo('landing');
    }
}

function logout() {
    appState.currentUser = null;
    localStorage.removeItem('knowtifyUser');
    navigateTo('landing');
    showToast('Logged out successfully');
}

// Page Navigation
function navigateTo(page) {
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));

    const targetPage = document.getElementById(page + 'Page');
    if (targetPage) {
        targetPage.classList.add('active');
        document.querySelector('.nav-links')?.classList.remove('active');

        // Hide navbar on landing/auth pages
        const navbar = document.querySelector('.navbar');
        if (['landing', 'login', 'register'].includes(page)) {
            navbar.style.display = 'none';
        } else {
            navbar.style.display = 'block';
        }
    }
}

function toggleMobileMenu() {
    const navLinks = document.querySelector('.nav-links');
    navLinks.classList.toggle('active');
}

function scrollToFeatures() {
    document.getElementById('features').scrollIntoView({ behavior: 'smooth' });
}

// Edit Preferences (change domains anytime)
function showEditPreferences() {
    const modal = document.createElement('div');
    modal.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0,0,0,0.7);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 1000;
    `;

    const domainsHtml = [
        'DSA', 'Machine Learning', 'Web Development', 'Cloud Computing',
        'Artificial Intelligence', 'Cybersecurity', 'DevOps', 'Database',
        'System Design', 'Mobile Development'
    ].map(d => `
        <label class="domain-checkbox" style="display: block; margin: 0.5rem 0;">
            <input type="checkbox" value="${d}" ${appState.userDomains && appState.userDomains.includes(d) ? 'checked' : ''}>
            ${d}
        </label>
    `).join('');

    modal.innerHTML = `
        <div style="background: var(--bg-card); border: 1px solid var(--border-glass); border-radius: 12px; padding: 2rem; max-width: 500px; backdrop-filter: blur(10px);">
            <h3 style="margin-bottom: 1rem; font-family: 'Space Grotesk';">Change Learning Domains</h3>
            <div style="margin-bottom: 1.5rem; max-height: 300px; overflow-y: auto;">
                ${domainsHtml}
            </div>
            <div style="display: flex; gap: 1rem;">
                <button class="btn btn-primary" onclick="savePreferencesModal()" style="flex: 1;">Save</button>
                <button class="btn btn-secondary" onclick="this.closest('div').parentElement.remove()" style="flex: 1;">Cancel</button>
            </div>
        </div>
    `;

    document.body.appendChild(modal);
}

async function savePreferencesModal() {
    const modal = document.querySelector('[style*="position: fixed"]');
    const selectedDomains = Array.from(modal.querySelectorAll('input:checked')).map(input => input.value);

    if (selectedDomains.length === 0) {
        showToast('Please select at least one domain');
        return;
    }

    showLoading(true);

    try {
        const response = await fetch(`${API_BASE}/user/${appState.currentUser.userId}/preferences`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                domains: selectedDomains,  // Send as array
                notificationTime: appState.currentUser.notificationTime || '12:00',
                notificationFrequency: appState.currentUser.notificationFrequency || 'daily',
            }),
        });

        if (response.ok) {
            const updatedUser = await response.json();
            appState.currentUser = updatedUser;
            appState.userDomains = selectedDomains;

            localStorage.setItem('userDomains_' + appState.currentUser.userId, JSON.stringify(selectedDomains));
            localStorage.setItem('knowtifyUser', JSON.stringify(appState.currentUser));

            showToast(`✅ Domains updated! Now learning: ${selectedDomains.join(', ')}`);
            modal.remove();
        }
    } catch (error) {
        showToast('Error: ' + error.message);
    } finally {
        showLoading(false);
    }
}

// Onboarding
async function completeOnboarding() {
    const domains = Array.from(document.querySelectorAll('.domain-checkbox input:checked'))
        .map(input => input.value);

    const notificationTime = document.getElementById('notificationTime').value;
    const notificationFrequency = document.getElementById('notificationFrequency').value;

    if (domains.length === 0) {
        showToast('Please select at least one domain');
        return;
    }

    showLoading(true);

    try {
        console.log('Saving preferences with domains:', domains);

        const response = await fetch(`${API_BASE}/user/${appState.currentUser.userId}/preferences`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                domains: domains,  // Send as array
                notificationTime: notificationTime,
                notificationFrequency: notificationFrequency,
            }),
        });

        if (response.ok) {
            const updatedUser = await response.json();
            appState.currentUser = updatedUser;

            // Ensure domains is always an array
            appState.userDomains = Array.isArray(updatedUser.preferredDomains) ? updatedUser.preferredDomains : domains;
            console.log('Updated domains:', appState.userDomains, 'Type:', typeof appState.userDomains);

            // IMPORTANT: Save to localStorage FIRST to ensure data persists
            const userId = appState.currentUser.userId || appState.currentUser.id;
            localStorage.setItem('userDomains_' + userId, JSON.stringify(appState.userDomains));
            localStorage.setItem('knowtifyUser', JSON.stringify(appState.currentUser));
            localStorage.setItem('authTimestamp_' + userId, new Date().getTime().toString());

            console.log('✅ Data saved to localStorage. Domains:', localStorage.getItem('userDomains_' + userId));
            console.log('✅ User saved:', localStorage.getItem('knowtifyUser') ? 'YES' : 'NO');
            console.log('Preferences saved. User domains:', appState.userDomains);
            showToast(`✅ Perfect! You'll get cards from: ${appState.userDomains.join(', ')}`);

            // Wait a bit longer to ensure localStorage is written
            await new Promise(resolve => setTimeout(resolve, 500));

            // Now navigate
            console.log('Navigating to dashboard...');
            navigateTo('dashboard');
            await loadDashboard();
        } else {
            const errorData = await response.json().catch(() => ({}));
            console.error('Preferences error:', errorData);
            showToast('Error: ' + (errorData.message || errorData.error || 'Failed to save preferences'));
        }
    } catch (error) {
        console.error('Onboarding error:', error);
        showToast('Error: ' + error.message);
    } finally {
        showLoading(false);
    }
}

// Dashboard
async function loadDashboard() {
    try {
        // Ensure userId is set
        if (!appState.currentUser || !appState.currentUser.userId) {
            console.error('No user ID available');
            showToast('Session error. Please login again.');
            navigateTo('login');
            return;
        }

        const userId = appState.currentUser.userId;
        console.log('Loading dashboard for userId:', userId);

        // Refresh user data (optional - use cached data if fails)
        try {
            const userResponse = await fetch(`${API_BASE}/user/${userId}`);
            if (userResponse.ok) {
                const freshUser = await userResponse.json();
                freshUser.userId = freshUser.userId || freshUser.id;
                appState.currentUser = freshUser;

                // Store user domains if they exist
                if (freshUser.preferredDomains && freshUser.preferredDomains.length > 0) {
                    appState.userDomains = Array.from(freshUser.preferredDomains);
                }
            }
        } catch (e) {
            console.warn('Could not refresh user data:', e.message);
        }

        // IMPORTANT: Reload streak from localStorage (not from API, as it's stored locally)
        const currentStreak = parseInt(localStorage.getItem('currentStreak_' + userId)) || 0;
        const longestStreak = parseInt(localStorage.getItem('longestStreak_' + userId)) || 0;
        appState.currentUser.currentStreak = currentStreak;
        appState.currentUser.longestStreak = longestStreak;

        // Load user stats (optional)
        try {
            const statsResponse = await fetch(`${API_BASE}/knowledge/user/${userId}/stats`);
            if (statsResponse.ok) {
                const stats = await statsResponse.json();
                document.getElementById('statCardsLearned').textContent = stats.totalCardsViewed || 0;
                document.getElementById('statStreak').textContent = appState.currentUser.currentStreak || 0;
                document.getElementById('statAccuracy').textContent = Math.round(stats.accuracy || 0) + '%';
            }
        } catch (e) {
            console.warn('Could not load stats:', e.message);
        }

        // Load domain progress
        try {
            await loadDomainProgress();
        } catch (e) {
            console.warn('Could not load domain progress:', e.message);
        }

        console.log('Dashboard loaded. User domains:', appState.userDomains);
    } catch (error) {
        console.error('Error loading dashboard:', error);
        showToast('Error loading dashboard');
    }
}

async function loadDomainProgress() {
    const container = document.getElementById('domainProgressGrid');
    container.innerHTML = '';

    const domains = [
        'DSA',
        'Machine Learning',
        'Web Development',
        'Cloud Computing',
        'Artificial Intelligence',
        'Cybersecurity',
        'DevOps',
        'Database',
        'System Design',
        'Mobile Development',
        'Blockchain',
        'Data Science',
        'Python',
        'Java',
        'Mathematics',
    ];

    for (const domain of domains) {
        const domainData = await fetch(`${API_BASE}/domains/name/${domain}`).then(r => r.json()).catch(() => null);

        if (domainData) {
            const statsResponse = await fetch(
                `${API_BASE}/knowledge/user/${appState.currentUser.userId}/domain/${domainData.id}/stats`
            );
            const stats = await statsResponse.json();

            const progressPercent = stats.progress || 0;

            const card = document.createElement('div');
            card.className = 'domain-card';
            card.innerHTML = `
                <div class="domain-name">${domain}</div>
                <div class="progress-bar">
                    <div class="progress-fill" style="width: ${progressPercent}%"></div>
                </div>
                <div class="domain-stats">
                    <span>${Math.round(stats.completedTopics || 0)}/${stats.totalTopics || 0} topics</span>
                    <span>${Math.round(progressPercent)}%</span>
                </div>
            `;
            container.appendChild(card);
        }
    }
}

// Knowledge Card
async function fetchNextCard() {
    console.log('fetchNextCard called. User domains:', appState.userDomains);

    if (!appState.userDomains || appState.userDomains.length === 0) {
        showToast('⚠️ No learning domains selected! Please set your preferences first.');
        console.log('No domains selected. Available:', appState.userDomains);
        return;
    }

    showLoading(true);

    try {
        // Get card from a user's selected domain
        const randomDomain = appState.userDomains[Math.floor(Math.random() * appState.userDomains.length)];
        console.log('Requesting card for domain:', randomDomain, 'User ID:', appState.currentUser.userId);

        const url = `${API_BASE}/knowledge/next?userId=${appState.currentUser.userId}&domain=${encodeURIComponent(randomDomain)}`;
        console.log('API URL:', url);

        const response = await fetch(url);

        if (response.ok) {
            const card = await response.json();
            console.log('Card received:', card);
            appState.currentKnowledgeCard = card;
            appState.cardStartTime = Date.now();
            appState.isCardFlipped = false;
            displayKnowledgeCard(card);
            navigateTo('cardDisplay');
            // Reset flip state
            const flipCard = document.querySelector('.knowledge-card-flip');
            if (flipCard) {
                flipCard.classList.remove('flipped');
            }
            showToast('📚 Loading your knowledge card...');
        } else {
            const errorText = await response.text();
            console.error('API Error:', response.status, errorText);
            if (response.status === 400) {
                showToast(`⚠️ Error: ${errorText || 'Could not load card'}`);
            } else {
                showToast(`⚠️ Server error. Try another domain.`);
            }
        }
    } catch (error) {
        console.error('Fetch error:', error);
        showToast('Error: ' + error.message);
    } finally {
        showLoading(false);
    }
}

function displayKnowledgeCard(card) {
    console.log('Displaying card:', card);

    const topic = card.topic;

    document.getElementById('cardTitle').textContent = topic.name;
    document.getElementById('cardDomain').textContent = topic.domain.name;
    document.getElementById('cardDifficulty').textContent = card.difficultyLevel;

    // Set summary (description)
    document.getElementById('cardSummary').textContent = topic.description || 'Expand your knowledge about this concept';

    // Get the full explanation content
    const fullContent = card.fullContent || topic.content || 'No detailed explanation available';

    // Display full content as main explanation in the "What is it?" section
    document.getElementById('cardWhat').textContent = fullContent;

    // Hide other sections if no additional content
    const howSection = document.getElementById('howSection');
    const exampleSection = document.getElementById('exampleSection');
    const whySection = document.getElementById('whySection');

    if (topic.example && topic.example !== fullContent) {
        document.getElementById('cardHow').textContent = topic.example;
        howSection.style.display = 'block';
    } else {
        howSection.style.display = 'none';
    }

    // Show real-world example section with topic-specific examples
    let realWorldExample = '';
    const topicLower = topic.name.toLowerCase();

    if (topicLower.includes('nlp') || topicLower.includes('natural language')) {
        realWorldExample = 'ChatGPT processes queries by tokenizing text, using transformer attention to understand context, generating responses word-by-word. Google Translate uses NLP to understand sentence structure before translating to target languages.';
    } else if (topicLower.includes('array')) {
        realWorldExample = 'Google Sheets stores data in 2D arrays for O(1) access. Netflix uses arrays for viewer history. Your phone\'s photo gallery indexes images by date in arrays for fast lookup.';
    } else if (topicLower.includes('linked list')) {
        realWorldExample = 'Browser history uses linked lists - each page points to previous. Undo/Redo uses doubly-linked lists. Spotify playlists link songs together sequentially.';
    } else if (topicLower.includes('stack') && !topicLower.includes('database')) {
        realWorldExample = 'Browser back button uses a stack for visited pages. Code editors use stacks for undo/redo. Function calls create a call stack tracking function nesting.';
    } else if (topicLower.includes('queue')) {
        realWorldExample = 'Printer queues manage jobs FIFO. Customer support tickets queue in order. Messaging apps queue messages for delivery. CPU schedulers queue processes.';
    } else if (topicLower.includes('tree') && !topicLower.includes('decision')) {
        realWorldExample = 'File systems - folders contain subfolders in tree structure. DOM trees represent HTML. Organization charts show hierarchies. Decision trees help doctors diagnose diseases.';
    } else if (topicLower.includes('graph') && !topicLower.includes('database')) {
        realWorldExample = 'Google Maps models cities as nodes and roads as edges for navigation. Facebook uses graphs for friendships and recommendations. GPS finds optimal routes using graph algorithms.';
    } else if (topicLower.includes('transformer') && !topicLower.includes('fine')) {
        realWorldExample = 'Transformers power ChatGPT, Claude, and all modern LLMs. Google Search uses transformers for understanding queries. Machine translation uses transformers for accurate document translation.';
    } else if (topicLower.includes('rag') || topicLower.includes('retrieval')) {
        realWorldExample = 'Customer support chatbots retrieve company docs before answering. Medical systems retrieve studies before suggesting treatments. Legal tools retrieve case law for analysis.';
    } else if (topicLower.includes('embedding')) {
        realWorldExample = 'Netflix generates embeddings for movies/users - similar movies have similar vectors. Amazon uses embeddings to find similar products. Spotify uses song embeddings for playlists.';
    } else if (topicLower.includes('attention') && !topicLower.includes('mechanism')) {
        realWorldExample = 'When reading, you focus on relevant words. Attention mechanisms learn which input words matter for each output. Machine translation uses attention to align languages.';
    } else {
        realWorldExample = topic.example || 'See practical applications by working with real-world problems and systems using these concepts.';
    }

    document.getElementById('cardExample').textContent = realWorldExample;
    exampleSection.style.display = 'block';

    // Only show why section if we have different content
    whySection.style.display = 'none';

    // Setup resource links
    const topicNameForUrl = topic.name.replace(/\s+/g, '_');
    document.getElementById('wikiLink').href = `https://en.wikipedia.org/wiki/${topicNameForUrl}`;
    document.getElementById('gfgLink').href = `https://www.geeksforgeeks.org/search/?q=${encodeURIComponent(topic.name)}`;

    // Show external link if available
    const moreLink = document.getElementById('moreLink');
    if (topic.externalLink) {
        moreLink.href = topic.externalLink;
        moreLink.textContent = 'Learn More';
        moreLink.style.display = 'inline-block';
    } else {
        moreLink.style.display = 'none';
    }
}

function setupCardFlip() {
    // No flip needed anymore - content is displayed directly
    console.log('Card flip removed - content displayed directly');
}

async function markCorrect() {
    if (!appState.currentKnowledgeCard) {
        showToast('No card to save');
        return;
    }

    const timeSpent = Math.round((Date.now() - appState.cardStartTime) / 1000);
    const knowledgeId = appState.currentKnowledgeCard.id;
    const topicId = appState.currentKnowledgeCard.topic.id;
    const userId = appState.currentUser.userId;

    showLoading(true);

    try {
        console.log('Marking card as correct:', knowledgeId);

        // Answer the card
        const answerResponse = await fetch(`${API_BASE}/knowledge/${knowledgeId}/answer`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userAnswer: 'understood',
                isCorrect: true,
                timeSpentSeconds: timeSpent,
            }),
        });

        if (!answerResponse.ok) {
            throw new Error('Failed to save answer: ' + answerResponse.status);
        }

        const result = await answerResponse.json();
        console.log('Answer saved:', result);

        // Mark topic as completed
        const completeResponse = await fetch(`${API_BASE}/user/${userId}/topic/${topicId}/complete`, {
            method: 'POST'
        });

        console.log('Topic marked complete:', completeResponse.status);

        // Update streak
        updateStreak();

        showToast('✅ Topic completed! +1 Streak! 🔥');
        await new Promise(resolve => setTimeout(resolve, 1000));
        showLoading(false);
        navigateTo('dashboard');
        loadDashboard();

    } catch (error) {
        console.error('Error marking correct:', error);
        showToast('❌ Error: ' + error.message);
        showLoading(false);
    }
}

async function markIncorrect() {
    if (!appState.currentKnowledgeCard) {
        showToast('No card to save');
        return;
    }

    const timeSpent = Math.round((Date.now() - appState.cardStartTime) / 1000);
    const knowledgeId = appState.currentKnowledgeCard.id;

    showLoading(true);

    try {
        console.log('Marking card as not clear:', knowledgeId);

        const response = await fetch(`${API_BASE}/knowledge/${knowledgeId}/answer`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userAnswer: 'unclear',
                isCorrect: false,
                timeSpentSeconds: timeSpent,
            }),
        });

        if (!response.ok) {
            throw new Error('Failed to save: ' + response.status);
        }

        const result = await response.json();
        console.log('Card saved as not clear:', result);

        showToast('📚 We\'ll show this again later!');
        await new Promise(resolve => setTimeout(resolve, 1000));
        showLoading(false);
        navigateTo('dashboard');
        loadDashboard();

    } catch (error) {
        console.error('Error marking not clear:', error);
        showToast('❌ Error: ' + error.message);
        showLoading(false);
    }
}

async function toggleBookmark() {
    if (!appState.currentKnowledgeCard) {
        showToast('No card to bookmark');
        return;
    }

    try {
        const topicId = appState.currentKnowledgeCard.topic.id;
        const userId = appState.currentUser.userId;

        console.log('Toggling bookmark for topic:', topicId);

        const response = await fetch(
            `${API_BASE}/user/${userId}/topic/${topicId}/bookmark`,
            { method: 'POST' }
        );

        if (response.ok) {
            const result = await response.json();
            console.log('Bookmark toggled:', result);

            if (result.bookmarked) {
                showToast('⭐ Bookmarked for later!');
                // Update button color
                const bookmarkBtn = document.querySelector('.btn-outline');
                if (bookmarkBtn) {
                    bookmarkBtn.style.color = '#fbbf24';
                }
            } else {
                showToast('📌 Removed from bookmarks');
                const bookmarkBtn = document.querySelector('.btn-outline');
                if (bookmarkBtn) {
                    bookmarkBtn.style.color = 'var(--text-secondary)';
                }
            }
        } else {
            showToast('Error bookmarking: ' + response.status);
        }
    } catch (error) {
        console.error('Bookmark error:', error);
        showToast('Error: ' + error.message);
    }
}

// History
async function loadHistory() {
    const container = document.getElementById('historyList');
    const filterDomain = document.getElementById('filterDomain');

    container.innerHTML = '<p style="text-align: center; color: var(--text-secondary);">Loading history...</p>';

    try {
        if (!appState.currentUser || !appState.currentUser.userId) {
            container.innerHTML = '<p style="color: red;">User not logged in</p>';
            return;
        }

        console.log('Fetching history for user:', appState.currentUser.userId);
        const response = await fetch(`${API_BASE}/knowledge/user/${appState.currentUser.userId}/history`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const cards = await response.json();
        console.log('History cards received:', cards.length, cards);

        // Clear and repopulate filter dropdown
        filterDomain.innerHTML = '<option value="">All Domains</option>';
        const domains = [...new Set(cards.map(c => c.topic?.domain?.name))].filter(Boolean);
        domains.forEach(domain => {
            const option = document.createElement('option');
            option.value = domain;
            option.textContent = domain;
            filterDomain.appendChild(option);
        });

        if (!cards || cards.length === 0) {
            container.innerHTML = '<p style="text-align: center; color: var(--text-secondary); padding: 2rem;">No learning history yet.<br>Start by clicking "Get Today\'s Card" to begin learning!</p>';
            return;
        }

        container.innerHTML = '';
        cards.forEach(card => {
            const item = document.createElement('div');
            item.className = 'history-item';
            const dateObj = new Date(card.presentedAt);
            const date = dateObj.toLocaleDateString('en-US', {
                year: 'numeric',
                month: 'short',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            });
            const status = card.isCorrect === null ? '⏳ Pending' : (card.isCorrect ? '✅ Understood' : '❌ Not Clear');
            item.innerHTML = `
                <div class="history-item-title">${card.topic?.name || 'Unknown Topic'}</div>
                <div class="history-item-meta">
                    <span>📚 ${card.topic?.domain?.name || 'Unknown Domain'}</span>
                    <span>📅 ${date}</span>
                    <span>${status}</span>
                </div>
            `;
            container.appendChild(item);
        });

        console.log('History display complete:', cards.length, 'cards');
    } catch (error) {
        console.error('History error:', error);
        container.innerHTML = '<p style="color: red; padding: 2rem;">Error loading history:<br>' + error.message + '</p>';
    }
}

function filterHistory() {
    const domain = document.getElementById('filterDomain').value;
    const search = document.getElementById('searchHistory').value.toLowerCase();
    const items = document.querySelectorAll('.history-item');

    items.forEach(item => {
        const title = item.querySelector('.history-item-title').textContent.toLowerCase();
        const domainText = item.textContent.toLowerCase();

        const matchesDomain = !domain || domainText.includes(domain.toLowerCase());
        const matchesSearch = !search || title.includes(search);

        item.style.display = matchesDomain && matchesSearch ? 'block' : 'none';
    });
}

// Profile
async function loadProfile() {
    try {
        // Load user info
        document.getElementById('profileUsername').textContent = appState.currentUser.username;
        document.getElementById('profileEmail').textContent = appState.currentUser.email;

        // Load stats
        const response = await fetch(
            `${API_BASE}/knowledge/user/${appState.currentUser.userId}/stats`
        );
        const stats = await response.json();

        document.getElementById('profileTotalCards').textContent = stats.totalCardsViewed || 0;
        document.getElementById('profileAccuracy').textContent = Math.round(stats.accuracy || 0) + '%';
        document.getElementById('profileStreak').textContent = appState.currentUser.currentStreak || 0;
        document.getElementById('profileLongestStreak').textContent = appState.currentUser.longestStreak || 0;

        // Load bookmarked topics
        try {
            const bookmarkResponse = await fetch(`${API_BASE}/user/${appState.currentUser.userId}/bookmarked`);
            const bookmarked = await bookmarkResponse.json();

            const bookmarkContainer = document.getElementById('bookmarkedList');
            if (!bookmarked || bookmarked.length === 0) {
                bookmarkContainer.innerHTML = '<p style="color: var(--text-secondary);">No bookmarks yet. Click 🔖 on cards to bookmark them!</p>';
            } else {
                bookmarkContainer.innerHTML = '';
                bookmarked.forEach(item => {
                    const badge = document.createElement('div');
                    badge.className = 'bookmarked-item';
                    badge.innerHTML = `<strong>${item.topic?.name || 'Unknown'}</strong><br><small>${item.topic?.domain?.name || ''}</small>`;
                    bookmarkContainer.appendChild(badge);
                });
            }
        } catch (bookmarkError) {
            console.error('Error loading bookmarks:', bookmarkError);
            document.getElementById('bookmarkedList').innerHTML = '<p style="color: var(--text-secondary);">No bookmarks yet</p>';
        }
    } catch (error) {
        console.error('Error loading profile:', error);
    }
}

// Navigation listener
document.addEventListener('click', (e) => {
    const target = e.target;
    if (target.textContent?.includes('History')) {
        setTimeout(() => {
            console.log('Loading history...');
            loadHistory();
        }, 100);
    }
    if (target.textContent?.includes('Profile')) {
        setTimeout(() => {
            console.log('Loading profile...');
            loadProfile();
        }, 100);
    }
});

// Override navigate for history and profile
const originalNavigateTo = navigateTo;
navigateTo = function(page) {
    originalNavigateTo(page);
    if (page === 'history') {
        console.log('History page loaded');
        setTimeout(loadHistory, 200);
    } else if (page === 'profile') {
        console.log('Profile page loaded');
        setTimeout(loadProfile, 200);
    }
};

// UI Utilities
function showToast(message) {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.classList.add('show');
    setTimeout(() => toast.classList.remove('show'), 3000);
}

function showLoading(show) {
    const spinner = document.getElementById('loadingSpinner');
    if (show) {
        spinner.classList.add('show');
    } else {
        spinner.classList.remove('show');
    }
}

// Check streak status on page load
function checkStreakStatus(userId) {
    const lastStreakDateTime = localStorage.getItem('lastStreakDateTime_' + userId);
    const currentStreak = parseInt(localStorage.getItem('currentStreak_' + userId)) || 0;
    const longestStreak = parseInt(localStorage.getItem('longestStreak_' + userId)) || 0;

    if (!lastStreakDateTime || currentStreak === 0) {
        return; // No streak to check
    }

    const now = new Date();
    const lastTime = new Date(lastStreakDateTime);
    const hoursSinceLastLearning = (now - lastTime) / (1000 * 60 * 60);

    console.log('Checking streak status... Hours since last: ' + Math.floor(hoursSinceLastLearning));

    if (hoursSinceLastLearning > 48) {
        // More than 1 day has passed - reset streak to 0
        console.log('🔥 STREAK BROKEN! No learning for ' + Math.floor(hoursSinceLastLearning / 24) + ' days. Resetting to 0.');
        localStorage.setItem('currentStreak_' + userId, '0');

        // Update app state
        if (appState.currentUser) {
            appState.currentUser.currentStreak = 0;
        }

        // Sync to server
        syncStreakToServer(0, longestStreak);
    }
}

// Streak Management
function updateStreak() {
    if (!appState.currentUser) return;

    const userId = appState.currentUser.userId;
    const now = new Date();
    const today = now.toDateString();

    const lastStreakDateTime = localStorage.getItem('lastStreakDateTime_' + userId);
    const currentStreak = parseInt(localStorage.getItem('currentStreak_' + userId)) || 0;
    const longestStreak = parseInt(localStorage.getItem('longestStreak_' + userId)) || 0;

    // Check if already counted today
    if (lastStreakDateTime) {
        const lastDate = new Date(lastStreakDateTime).toDateString();
        if (lastDate === today) {
            console.log('Already counted today. Streak: ' + currentStreak);
            return;
        }
    }

    // Calculate time since last learning
    let newStreak = currentStreak;

    if (lastStreakDateTime) {
        const lastTime = new Date(lastStreakDateTime);
        const hoursSinceLastLearning = (now - lastTime) / (1000 * 60 * 60);

        console.log('Hours since last learning:', hoursSinceLastLearning);

        if (hoursSinceLastLearning >= 24) {
            // More than 24 hours have passed
            // Check if it's been more than 48 hours (more than 1 day gap)
            if (hoursSinceLastLearning > 48) {
                // Streak broken - reset to 1
                newStreak = 1;
                console.log('Streak broken! Reset to 1 (gap was ' + Math.floor(hoursSinceLastLearning / 24) + ' days)');
            } else {
                // Exactly 1 day gap - continue streak
                newStreak = currentStreak + 1;
                console.log('Streak continued! Current: ' + newStreak);
            }
        } else {
            // Less than 24 hours - same day, shouldn't happen due to earlier check
            return;
        }
    } else {
        // First time learning
        newStreak = 1;
        console.log('First learning! Streak: 1');
    }

    // Update longest streak
    const newLongestStreak = Math.max(newStreak, longestStreak);

    // Save to localStorage with timestamp
    localStorage.setItem('lastStreakDateTime_' + userId, now.toISOString());
    localStorage.setItem('currentStreak_' + userId, newStreak);
    localStorage.setItem('longestStreak_' + userId, newLongestStreak);

    // Update app state
    appState.currentUser.currentStreak = newStreak;
    appState.currentUser.longestStreak = newLongestStreak;

    console.log('✅ Streak Updated - Current: ' + newStreak + ', Longest: ' + newLongestStreak);

    // Sync with server
    syncStreakToServer(newStreak, newLongestStreak);
}

async function syncStreakToServer(currentStreak, longestStreak) {
    try {
        const userId = appState.currentUser.userId;
        const response = await fetch(`${API_BASE}/user/${userId}/stats`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                cardsLearned: appState.currentUser.totalCardsLearned || 0,
                currentStreak: currentStreak,
                longestStreak: longestStreak,
            }),
        });

        if (response.ok) {
            console.log('✅ Streak synced to server - Current: ' + currentStreak + ', Longest: ' + longestStreak);
        } else {
            console.error('Failed to sync streak to server');
        }
    } catch (error) {
        console.error('Error syncing streak:', error);
    }
}
