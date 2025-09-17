(() => {
  const api = {
    daily: (date, size) => fetch(`/api/wordhunt/daily?${toQuery({ date, size })}`, { credentials: 'include' }).then(j),
    leaderboard: (date, limit=50) => fetch(`/api/wordhunt/leaderboard?${toQuery({ date, limit })}`, { credentials: 'include' }).then(j),
    submit: (date, words) => fetch(`/api/wordhunt/submit?${toQuery({ date })}`, {
      method: 'POST', credentials: 'include', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ words })
    }).then(j),
    signin: (username, password) => fetch('/api/auth/signin', {
      method: 'POST', credentials: 'include', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ username, password })
    }).then(j),
    signup: (username, email, password) => fetch('/api/auth/signup', {
      method: 'POST', credentials: 'include', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ username, email, password })
    }).then(j)
  };

  const $ = sel => document.querySelector(sel);
  const j = r => r.json();
  const toQuery = (o) => Object.entries(o).filter(([,v]) => v!=null && v!=='')
    .map(([k,v]) => `${encodeURIComponent(k)}=${encodeURIComponent(v)}`).join('&');
  const todayStr = () => new Date().toISOString().slice(0,10);

  const gridEl = $('#grid');
  const seedVal = $('#seedVal');
  const dateInput = $('#dateInput');
  const sizeSelect = $('#sizeSelect');

  const lbDateInput = $('#lbDateInput');
  const meDateInput = $('#meDateInput');

  const username = $('#username');
  const playerName = document.getElementById('playerName');
  const authStatus = $('#authStatus');

  const refreshDaily = $('#refreshDaily');
  const refreshLb = $('#refreshLb');
  const refreshMe = $('#refreshMe');
  const submitBtn = $('#submitBtn');
  const wordsInput = $('#wordsInput');
  const submitResult = $('#submitResult');
  const confettiCanvas = document.getElementById('confetti');
  const ctx = confettiCanvas.getContext('2d');
  let signedIn = true; // no auth; always allow
  // interactive builder state
  let gridData = [];
  let pathCells = [];
  let queuedWords = [];

  function setDateDefaults(){
    const t = todayStr();
    dateInput.value ||= t;
    lbDateInput.value ||= t;
    meDateInput.value ||= t;
  }

  function renderGrid(grid){
    const size = grid.length;
    gridData = grid;
    gridEl.setAttribute('data-size', String(size));
    gridEl.style.gridTemplateColumns = `repeat(${size},1fr)`;
    gridEl.innerHTML = '';
    for (let r=0;r<size;r++){
      for (let c=0;c<size;c++){
        const div = document.createElement('div');
        div.className = 'cell';
        div.textContent = grid[r][c];
        div.dataset.r = String(r);
        div.dataset.c = String(c);
        div.addEventListener('click', () => onCellClick(r,c,div));
        gridEl.appendChild(div);
      }
    }
  }

  async function loadDaily(){
    const date = dateInput.value || todayStr();
    const size = sizeSelect.value;
    try{
      const res = await api.daily(date, size);
      renderGrid(res.grid);
      seedVal.textContent = String(res.seed);
      staggerCells();
    }catch(e){ console.error(e); }
  }

  async function loadLeaderboard(){
    const date = lbDateInput.value || todayStr();
    try{
      const data = await api.leaderboard(date, 50);
      const tbody = document.querySelector('#leaderboard tbody');
      tbody.innerHTML = '';
      (data.top||[]).forEach((row, i) => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${i+1}</td><td>${escapeHtml(row.username)}</td><td>${row.score}</td><td>${row.wordsFound}</td>`;
        tbody.appendChild(tr);
        tr.style.animationDelay = (i*40)+'ms';
      });
    }catch(e){ console.error(e); }
  }

  async function loadMe(){
    const el = document.getElementById('mePanel');
    const date = meDateInput.value || todayStr();
    const username = currentPlayer();
    try{
      const resp = await fetch(`/api/wordhunt/me?${toQuery({ date, username })}`, { credentials: 'include' });
      if (!resp.ok){
        el.textContent = 'Enter your name to check score.';
        return;
      }
      const me = await resp.json();
      if (me && me.score != null){
        el.textContent = `Score: ${me.score}  •  Words: ${me.wordsFound}  •  ${Array.isArray(me.words)? me.words.join(', ') : ''}`;
      } else {
        el.textContent = 'No submission yet.';
      }
    }catch(e){ el.textContent = 'Enter your name to check score.'; }
  }

  function currentPlayer(){
    const v = (playerName?.value || '').trim();
    return v || 'guest';
  }

  async function doSubmit(){
    submitResult.textContent = '...';
    const date = dateInput.value || todayStr();
    const text = wordsInput.value || '';
    const words = text.split(/\s+/).map(w => w.trim()).filter(Boolean);
    try{
      const resp = await fetch(`/api/wordhunt/submit?${toQuery({ date })}`, { method:'POST', credentials:'include', headers:{'Content-Type':'application/json'}, body: JSON.stringify({ username: currentPlayer(), words })});
      if (resp.status === 401){ submitResult.textContent = 'Please sign in to submit.'; return; }
      const res = await resp.json();
      if (res){
        submitResult.textContent = `${res.accepted? 'Accepted' : 'Rejected'} • Score ${res.score} • Words ${res.wordsFound}`;
        loadLeaderboard();
        loadMe();
        if (res.accepted) {
          celebrate();
        }
      } else {
        submitResult.textContent = 'No response';
      }
    }catch(e){ submitResult.textContent = 'Submit failed'; }
  }

  function escapeHtml(s){
    return String(s).replace(/[&<>"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));
  }

  // Wire events
  // no auth buttons now
  refreshDaily.addEventListener('click', withRipple(loadDaily));
  refreshLb.addEventListener('click', withRipple(loadLeaderboard));
  refreshMe.addEventListener('click', withRipple(loadMe));
  submitBtn.addEventListener('click', withRipple(doSubmit));
  sizeSelect.addEventListener('change', loadDaily);
  dateInput.addEventListener('change', () => { loadDaily(); loadLeaderboard(); loadMe(); });
  lbDateInput.addEventListener('change', loadLeaderboard);
  meDateInput.addEventListener('change', loadMe);

  // Init
  setDateDefaults();
  loadDaily();
  loadLeaderboard();
  document.getElementById('mePanel').textContent = 'Enter your name to check score.';

  // ------- Animations -------
  function staggerCells(){
    const cells = Array.from(document.querySelectorAll('.grid .cell'));
    cells.forEach((cell, i) => {
      cell.style.animationDelay = (i*30)+'ms';
    });
  }

  function withRipple(handler){
    return (ev) => {
      const btn = ev.currentTarget;
      const rect = btn.getBoundingClientRect();
      const span = document.createElement('span');
      span.className = 'ripple';
      const size = Math.max(rect.width, rect.height);
      span.style.width = span.style.height = size+'px';
      span.style.left = (ev.clientX - rect.left - size/2)+'px';
      span.style.top = (ev.clientY - rect.top - size/2)+'px';
      btn.appendChild(span);
      setTimeout(() => span.remove(), 600);
      handler(ev);
    };
  }

  // Confetti
  let confettiPieces = [];
  function celebrate(){
    resizeCanvas();
    confettiPieces = Array.from({length: 140}, () => makePiece());
    let start = performance.now();
    function frame(ts){
      const elapsed = ts - start;
      if (elapsed > 1800) { ctx.clearRect(0,0,confettiCanvas.width,confettiCanvas.height); return; }
      drawFrame();
      requestAnimationFrame(frame);
    }
    requestAnimationFrame(frame);
  }

  function makePiece(){
    const colors = ['#6ea8ff','#9c6eff','#6effc1','#ffd36e','#ff6ea8'];
    return {
      x: Math.random()*confettiCanvas.width,
      y: -10 - Math.random()*40,
      r: 4 + Math.random()*6,
      vx: -1 + Math.random()*2,
      vy: 2 + Math.random()*2.5,
      rot: Math.random()*Math.PI*2,
      vr: -0.2 + Math.random()*0.4,
      color: colors[(Math.random()*colors.length)|0]
    };
  }

  function drawFrame(){
    ctx.clearRect(0,0,confettiCanvas.width,confettiCanvas.height);
    confettiPieces.forEach(p => {
      p.x += p.vx; p.y += p.vy; p.rot += p.vr;
      if (p.y > confettiCanvas.height + 20) { p.y = -10; p.x = Math.random()*confettiCanvas.width; }
      ctx.save();
      ctx.translate(p.x, p.y);
      ctx.rotate(p.rot);
      ctx.fillStyle = p.color;
      ctx.fillRect(-p.r/2, -p.r/2, p.r, p.r);
      ctx.restore();
    });
  }

  function resizeCanvas(){
    confettiCanvas.width = window.innerWidth;
    confettiCanvas.height = window.innerHeight;
  }
  window.addEventListener('resize', resizeCanvas);

  // ------- Interactive builder -------
  const currentWordEl = document.getElementById('currentWord');
  const wordsListEl = document.getElementById('wordsList');
  const undoBtn = document.getElementById('undoBtn');
  const clearWordBtn = document.getElementById('clearWordBtn');
  const addWordBtn = document.getElementById('addWordBtn');

  function onCellClick(r,c,el){
    if (!canSelect(r,c)) return shake(el);
    pathCells.push([r,c]);
    el.classList.add('selected','path');
    renderCurrentWord();
  }

  function canSelect(r,c){
    // Avoid reusing same cell
    if (pathCells.some(([pr,pc]) => pr===r && pc===c)) return false;
    // first pick allowed
    if (pathCells.length===0) return true;
    const [lr, lc] = pathCells[pathCells.length-1];
    return Math.abs(lr-r)<=1 && Math.abs(lc-c)<=1;
  }

  function renderCurrentWord(){
    const letters = pathCells.map(([r,c]) => gridData[r][c]);
    currentWordEl.textContent = letters.length ? letters.join('') : '—';
  }

  function undo(){
    if (!pathCells.length) return;
    const [r,c] = pathCells.pop();
    const el = document.querySelector(`.cell[data-r="${r}"][data-c="${c}"]`);
    if (el){ el.classList.remove('selected','path'); }
    renderCurrentWord();
  }

  function clearWord(){
    while (pathCells.length){ undo(); }
  }

  function addWord(){
    const word = (currentWordEl.textContent || '').trim();
    if (word.length < 3) { bump(currentWordEl); return; }
    queuedWords.push(word);
    renderWordChips();
    clearWord();
  }

  function renderWordChips(){
    wordsListEl.innerHTML = '';
    queuedWords.forEach((w,i) => {
      const chip = document.createElement('div');
      chip.className = 'chip';
      chip.innerHTML = `<span>${escapeHtml(w)}</span><span class="x" title="Remove">✕</span>`;
      chip.querySelector('.x').addEventListener('click', () => {
        queuedWords.splice(i,1);
        renderWordChips();
      });
      wordsListEl.appendChild(chip);
    });
  }

  undoBtn.addEventListener('click', withRipple(undo));
  clearWordBtn.addEventListener('click', withRipple(clearWord));
  addWordBtn.addEventListener('click', withRipple(addWord));

  function shake(el){
    el.style.transition = 'transform .1s';
    el.style.transform = 'translateX(3px)';
    setTimeout(()=>{ el.style.transform = 'translateX(-3px)'; }, 60);
    setTimeout(()=>{ el.style.transform = ''; el.style.transition = ''; }, 160);
  }

  function bump(el){
    el.style.transition = 'transform .12s';
    el.style.transform = 'scale(1.08)';
    setTimeout(()=>{ el.style.transform = ''; el.style.transition = ''; }, 160);
  }

  // Hook queued words to submit
  const originalSubmit = doSubmit;
  async function doSubmit(){
    // merge queued + textarea
    const typed = (wordsInput.value || '').split(/\s+/).map(w=>w.trim()).filter(Boolean);
    const merged = Array.from(new Set([ ...queuedWords, ...typed ]));
    wordsInput.value = merged.join(' ');
    await originalSubmit();
    // Clear queued words after successful submit
    if (submitResult.textContent.includes('Accepted')) {
      queuedWords = [];
      renderWordChips();
    }
  }
})();


