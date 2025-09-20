(() => {
  const api = {
    getGames: () => fetch('/api/store/games').then(r => r.json()),
    createUser: (data) => fetch('/api/store/users', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    }).then(r => r.json()),
    getUser: (username) => fetch(`/api/store/users/${username}`).then(r => r.json()),
    addToWallet: (username, amount) => fetch(`/api/store/users/${username}/wallet/add`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ amount })
    }).then(r => r.json()),
    purchaseGame: (data) => fetch('/api/store/purchases', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    }).then(r => r.json()),
    getUserPurchases: (username) => fetch(`/api/store/users/${username}/purchases`).then(r => r.json()),
    createSubscription: (data) => fetch('/api/store/subscriptions', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    }).then(r => r.json()),
    getUserSubscription: (username) => fetch(`/api/store/users/${username}/subscription`).then(r => r.json()),
    cancelSubscription: (username) => fetch(`/api/store/users/${username}/subscription/cancel`, {
      method: 'POST'
    }).then(r => r.json()),
    getTopSpenders: (limit = 10) => fetch(`/api/store/analytics/top-spenders?limit=${limit}`).then(r => r.json()),
    getTopBuyers: (limit = 10) => fetch(`/api/store/analytics/top-buyers?limit=${limit}`).then(r => r.json()),
    getStoreStats: () => fetch('/api/store/analytics/stats').then(r => r.json())
  };

  const $ = sel => document.querySelector(sel);
  const $$ = sel => document.querySelectorAll(sel);
  const escapeHtml = s => String(s).replace(/[&<>"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));

  let currentUser = null;
  let games = [];
  let userProfile = null;
  let userSubscription = null;
  let userPurchases = [];

  const welcomeMessage = $('#welcomeMessage');
  const currentUsername = $('#currentUsername');
  const logoutBtn = $('#logoutBtn');
  const profilePanel = $('#profilePanel');
  const subscriptionPanel = $('#subscriptionPanel');
  const myGamesPanel = $('#myGamesPanel');
  const gamesGrid = $('#gamesGrid');
  const categoryFilter = $('#categoryFilter');
  const searchInput = $('#searchInput');
  const searchBtn = $('#searchBtn');

  async function init() {
    // Check authentication first
    if (!checkAuthentication()) {
      return; // Will redirect to login
    }
    
    // Show user panels since user is authenticated
    profilePanel.style.display = 'block';
    subscriptionPanel.style.display = 'block';
    myGamesPanel.style.display = 'block';
    
    await loadGames();
    await loadAnalytics();
    await loadUserProfile();
    await loadUserSubscription();
    await loadUserPurchases();
    setupEventListeners();
  }

  function setupEventListeners() {
    logoutBtn.addEventListener('click', logout);
    $('#refreshProfileBtn').addEventListener('click', loadUserProfile);
    $('#addWalletBtn').addEventListener('click', addToWallet);
    $('#refreshSubscriptionBtn').addEventListener('click', loadUserSubscription);
    $('#cancelSubscriptionBtn').addEventListener('click', cancelSubscription);
    $$('.subscribe-btn').forEach(btn => {
      btn.addEventListener('click', (e) => subscribeToPlan(e.target.dataset.plan));
    });
    $('#refreshPurchasesBtn').addEventListener('click', loadUserPurchases);
    $('#refreshStatsBtn').addEventListener('click', loadAnalytics);
    categoryFilter.addEventListener('change', filterGames);
    searchBtn.addEventListener('click', searchGames);
    searchInput.addEventListener('keypress', (e) => {
      if (e.key === 'Enter') searchGames();
    });
  }

  function checkAuthentication() {
    const userData = localStorage.getItem('currentUser');
    if (!userData) {
      // User not authenticated, redirect to home page
      window.location.href = '/home.html';
      return false;
    }
    
    try {
      const user = JSON.parse(userData);
      currentUser = user.username;
      currentUsername.textContent = user.username;
      return true;
    } catch (error) {
      console.error('Error parsing user data:', error);
      localStorage.removeItem('currentUser');
      window.location.href = '/home.html';
      return false;
    }
  }

  async function logout() {
    try {
      // Call logout endpoint if it exists
      await fetch('/api/auth/signout', {
        method: 'POST',
        credentials: 'include'
      });
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      // Clear local storage and redirect
      localStorage.removeItem('currentUser');
      window.location.href = '/home.html';
    }
  }

  async function loadUserProfile() {
    if (!currentUser) return;
    
    try {
      const response = await api.getUser(currentUser);
      if (response.id) {
        userProfile = response;
        updateProfileDisplay();
      }
    } catch (error) {
      console.error('Failed to load profile:', error);
    }
  }

  function updateProfileDisplay() {
    if (!userProfile) return;
    
    $('#walletBalance').textContent = `$${userProfile.walletBalance || 0}`;
    $('#gamesOwned').textContent = userProfile.totalGamesPurchased || 0;
    $('#totalSpent').textContent = `$${userProfile.totalSpent || 0}`;
    $('#premiumStatus').textContent = userProfile.isPremiumMember ? 'Yes' : 'No';
  }

  async function addToWallet() {
    if (!currentUser) return;
    
    const amount = parseFloat($('#walletAmount').value);
    if (!amount || amount <= 0) {
      alert('Please enter a valid amount');
      return;
    }

    try {
      const result = await api.addToWallet(currentUser, amount);
      if (result.success) {
        $('#walletAmount').value = '';
        await loadUserProfile();
        showNotification('Wallet updated successfully!', 'success');
      } else {
        showNotification(result.message, 'error');
      }
    } catch (error) {
      showNotification('Failed to update wallet', 'error');
    }
  }

  async function loadGames() {
    try {
      games = await api.getGames();
      renderGames(games);
    } catch (error) {
      console.error('Failed to load games:', error);
    }
  }

  function renderGames(gamesToRender) {
    gamesGrid.innerHTML = '';
    
    gamesToRender.forEach(game => {
      const gameCard = document.createElement('div');
      gameCard.className = 'game-card';
      gameCard.innerHTML = `
        <div class="game-image">
          <img src="${game.imageUrl || 'https://via.placeholder.com/300x400'}" alt="${escapeHtml(game.title)}" onerror="this.src='https://via.placeholder.com/300x400'">
        </div>
        <div class="game-info">
          <h3>${escapeHtml(game.title)}</h3>
          <p class="game-developer">${escapeHtml(game.developer)}</p>
          <p class="game-description">${escapeHtml(game.description || 'No description available')}</p>
          <div class="game-meta">
            <span class="game-category">${escapeHtml(game.category)}</span>
            <span class="game-rating">⭐ ${game.rating?.toFixed(1) || 'N/A'}</span>
          </div>
          <div class="game-price">$${game.price || 0}</div>
          <button class="buy-btn" data-game-id="${game.id}" data-game-title="${escapeHtml(game.title)}" data-game-price="${game.price || 0}">
            ${game.price > 0 ? 'Buy Now' : 'Free'}
          </button>
        </div>
      `;
      
      const buyBtn = gameCard.querySelector('.buy-btn');
      buyBtn.addEventListener('click', () => purchaseGame(game.id, game.title, game.price));
      
      gamesGrid.appendChild(gameCard);
    });
  }

  async function purchaseGame(gameId, gameTitle, price) {
    if (!currentUser) {
      showNotification('Please create a profile first', 'error');
      return;
    }

    if (price > 0 && (!userProfile || userProfile.walletBalance < price)) {
      showNotification('Insufficient wallet balance', 'error');
      return;
    }

    try {
      const result = await api.purchaseGame({
        username: currentUser,
        gameId,
        paymentMethod: 'WALLET'
      });

      if (result.success) {
        showNotification(`Successfully purchased ${gameTitle}!`, 'success');
        celebrate();
        await loadUserProfile();
        await loadUserPurchases();
      } else {
        showNotification(result.message, 'error');
      }
    } catch (error) {
      showNotification('Failed to purchase game', 'error');
    }
  }

  function filterGames() {
    const category = categoryFilter.value;
    if (!category) {
      renderGames(games);
    } else {
      const filteredGames = games.filter(game => game.category === category);
      renderGames(filteredGames);
    }
  }

  async function searchGames() {
    const query = searchInput.value.trim();
    if (!query) {
      renderGames(games);
      return;
    }

    try {
      const searchResults = await api.searchGames(query);
      renderGames(searchResults);
    } catch (error) {
      console.error('Search failed:', error);
    }
  }

  async function loadUserSubscription() {
    if (!currentUser) return;
    
    try {
      const response = await api.getUserSubscription(currentUser);
      console.log('Subscription response:', response); // Debug log
      if (response && response.id) {
        userSubscription = response;
        updateSubscriptionDisplay();
      } else {
        userSubscription = null;
        updateSubscriptionDisplay();
      }
    } catch (error) {
      console.error('Error loading subscription:', error);
      userSubscription = null;
      updateSubscriptionDisplay();
    }
  }

  function updateSubscriptionDisplay() {
    const subscriptionInfo = $('#subscriptionInfo');
    const cancelBtn = $('#cancelSubscriptionBtn');
    
    // Check for both possible field names (isActive or active)
    const isActive = userSubscription && (userSubscription.isActive || userSubscription.active);
    
    if (userSubscription && isActive) {
      subscriptionInfo.innerHTML = `
        <div class="active-subscription">
          <h3>${userSubscription.subscriptionType} Plan</h3>
          <p>Price: $${userSubscription.monthlyPrice}/month</p>
          <p>Status: ${userSubscription.status}</p>
          <p>Next billing: ${new Date(userSubscription.nextBillingDate).toLocaleDateString()}</p>
        </div>
      `;
      cancelBtn.style.display = 'block';
      $$('.subscribe-btn').forEach(btn => btn.style.display = 'none');
    } else {
      subscriptionInfo.innerHTML = '<p>No active subscription</p>';
      cancelBtn.style.display = 'none';
      $$('.subscribe-btn').forEach(btn => btn.style.display = 'block');
    }
  }

  async function subscribeToPlan(planType) {
    if (!currentUser) {
      showNotification('Please create a profile first', 'error');
      return;
    }

    try {
      const result = await api.createSubscription({
        username: currentUser,
        subscriptionType: planType,
        paymentMethod: 'WALLET'
      });

      console.log('Subscription creation result:', result); // Debug log

      if (result.success) {
        showNotification(`Successfully subscribed to ${planType} plan!`, 'success');
        await loadUserProfile();
        await loadUserSubscription();
      } else {
        showNotification(result.message, 'error');
      }
    } catch (error) {
      console.error('Subscription creation error:', error);
      showNotification('Failed to create subscription', 'error');
    }
  }

  async function cancelSubscription() {
    if (!currentUser) return;

    if (!confirm('Are you sure you want to cancel your subscription?')) {
      return;
    }

    try {
      const result = await api.cancelSubscription(currentUser);
      if (result.success) {
        showNotification('Subscription cancelled successfully', 'success');
        await loadUserProfile();
        await loadUserSubscription();
      } else {
        showNotification(result.message, 'error');
      }
    } catch (error) {
      showNotification('Failed to cancel subscription', 'error');
    }
  }

  async function loadUserPurchases() {
    if (!currentUser) return;
    
    try {
      userPurchases = await api.getUserPurchases(currentUser);
      renderPurchases();
    } catch (error) {
      console.error('Failed to load purchases:', error);
    }
  }

  function renderPurchases() {
    const purchasesList = $('#purchasesList');
    
    if (userPurchases.length === 0) {
      purchasesList.innerHTML = '<p>No games purchased yet</p>';
      return;
    }

    purchasesList.innerHTML = userPurchases.map(purchase => `
      <div class="purchase-item">
        <div class="purchase-info">
          <h4>${escapeHtml(purchase.gameTitle)}</h4>
          <p>Purchased: ${new Date(purchase.purchaseDate).toLocaleDateString()}</p>
          <p>Price: $${purchase.price}</p>
          <p>Status: ${purchase.status}</p>
        </div>
      </div>
    `).join('');
  }

  async function loadAnalytics() {
    try {
      const [topSpenders, topBuyers, storeStats] = await Promise.all([
        api.getTopSpenders(5),
        api.getTopBuyers(5),
        api.getStoreStats()
      ]);

      renderTopSpenders(topSpenders);
      renderTopBuyers(topBuyers);
      renderStoreStats(storeStats);
    } catch (error) {
      console.error('Failed to load analytics:', error);
    }
  }

  function renderTopSpenders(spenders) {
    const container = $('#topSpenders');
    container.innerHTML = spenders.map((user, index) => `
      <div class="stat-item">
        <span class="rank">${index + 1}.</span>
        <span class="name">${escapeHtml(user.username)}</span>
        <span class="value">$${user.totalSpent}</span>
      </div>
    `).join('');
  }

  function renderTopBuyers(buyers) {
    const container = $('#topBuyers');
    container.innerHTML = buyers.map((user, index) => `
      <div class="stat-item">
        <span class="rank">${index + 1}.</span>
        <span class="name">${escapeHtml(user.username)}</span>
        <span class="value">${user.totalGamesPurchased} games</span>
      </div>
    `).join('');
  }

  function renderStoreStats(stats) {
    const container = $('#storeStats');
    container.innerHTML = `
      <div class="stat-item">
        <span class="label">Active Subscriptions:</span>
        <span class="value">${stats.totalActiveSubscriptions}</span>
      </div>
      <div class="stat-item">
        <span class="label">Total Purchases:</span>
        <span class="value">${stats.totalCompletedPurchases}</span>
      </div>
    `;
  }

  function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    document.body.appendChild(notification);
    
    setTimeout(() => {
      notification.remove();
    }, 3000);
  }

  function celebrate() {
    const confetti = document.createElement('div');
    confetti.style.cssText = `
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      pointer-events: none;
      z-index: 1000;
    `;
    
    for (let i = 0; i < 50; i++) {
      const particle = document.createElement('div');
      particle.style.cssText = `
        position: absolute;
        width: 10px;
        height: 10px;
        background: ${['#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#feca57'][Math.floor(Math.random() * 5)]};
        left: ${Math.random() * 100}%;
        top: -10px;
        animation: confetti-fall 3s linear forwards;
      `;
      confetti.appendChild(particle);
    }
    
    document.body.appendChild(confetti);
    setTimeout(() => confetti.remove(), 3000);
  }

  const style = document.createElement('style');
  style.textContent = `
    @keyframes confetti-fall {
      to {
        transform: translateY(100vh) rotate(360deg);
        opacity: 0;
      }
    }
    .notification {
      position: fixed;
      top: 20px;
      right: 20px;
      padding: 12px 20px;
      border-radius: 8px;
      color: white;
      font-weight: 500;
      z-index: 1001;
      animation: slideIn 0.3s ease;
    }
    .notification-success { background: #10b981; }
    .notification-error { background: #ef4444; }
    .notification-info { background: #3b82f6; }
    @keyframes slideIn {
      from { transform: translateX(100%); opacity: 0; }
      to { transform: translateX(0); opacity: 1; }
    }
  `;
  document.head.appendChild(style);

  init();
})();
