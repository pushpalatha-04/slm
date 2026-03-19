// ── State ──────────────────────────────────────────────
let selectedCourse = null;
let questions      = [];
let timerInterval  = null;
let timeLeft       = 30 * 60;
let quizSubmitted  = false;

// ── DOM refs ───────────────────────────────────────────
const modalOverlay   = document.getElementById('modal-overlay');
const quizLanding    = document.getElementById('quiz-landing');
const quizHeader     = document.getElementById('quiz-header');
const loadingEl      = document.getElementById('loading');
const quizContainer  = document.getElementById('quizContainer');
const submitArea     = document.getElementById('submit-area');
const resultEl       = document.getElementById('result');
const timerEl        = document.getElementById('timer');

// ── Open modal ─────────────────────────────────────────
function openModal() {
  modalOverlay.classList.add('active');
}

// ── Course selection ───────────────────────────────────
function selectCourse(el, course) {
  document.querySelectorAll('.course-card').forEach(c => c.classList.remove('selected'));
  el.classList.add('selected');
  selectedCourse = course;

  const btn = document.getElementById('beginBtn');
  btn.disabled = false;
  btn.textContent = 'Begin ' + course + ' Quiz →';
}

// ── Begin quiz ─────────────────────────────────────────
function beginQuiz() {
  if (!selectedCourse) return;

  // 1. Close modal
  modalOverlay.classList.remove('active');

  // 2. Hide landing, show ONLY the spinner
  quizLanding.style.display   = 'none';
  loadingEl.style.display     = 'block';   // show spinner immediately
  quizContainer.style.display = 'none';
  submitArea.style.display    = 'none';
  resultEl.style.display      = 'none';

  // 3. Update header title and show it
  document.getElementById('quiz-title').textContent = selectedCourse + ' Quiz';
  quizHeader.style.display = 'flex';

  console.log('Fetching quiz for course:', selectedCourse);

  // 4. Call backend
  fetch('http://localhost:8080/api/quiz/generate?course=' + encodeURIComponent(selectedCourse))
    .then(res => {
      if (!res.ok) throw new Error('Server error: ' + res.status);
      return res.json();
    })
    .then(data => {
      console.log('Questions received:', data.length, data);

      if (!data || data.length === 0) {
        throw new Error('Gemini returned 0 questions. Check your API key and parser logs.');
      }

      questions = data;

      // 5. Hide spinner, show questions
      loadingEl.style.display     = 'none';
      quizContainer.style.display = 'block';
      submitArea.style.display    = 'block';

      renderQuestions(data);
      startTimer();
    })
    .catch(err => {
      console.error('Quiz load error:', err);
      loadingEl.innerHTML = `
        <p style="color:#dc2626;font-size:.95rem;line-height:1.8">
          ⚠️ <b>Failed to load quiz</b><br><br>
          <b>Error:</b> ${err.message}<br><br>
          Things to check:<br>
          • Spring Boot is running on port 8080<br>
          • <code>gemini.api.key</code> is set in <code>application.properties</code><br>
          • Check the Spring Boot console for parser errors<br><br>
          <button onclick="location.reload()"
            style="padding:8px 20px;background:#4f46e5;color:#fff;border:none;
                   border-radius:8px;cursor:pointer;font-size:.9rem">
            Try Again
          </button>
        </p>`;
    });
}

// ── Render questions ───────────────────────────────────
function renderQuestions(data) {
  quizContainer.innerHTML = ''; // clear any previous render

  data.forEach((q, i) => {
    const card = document.createElement('div');
    card.className = 'question-card';
    card.id = 'card-' + i;

    const optionsHTML = ['A', 'B', 'C', 'D'].map(opt => `
      <label class="option-label" id="lbl-${i}-${opt}">
        <input type="radio" name="q${i}" value="${opt}">
        <span><b>${opt})</b> ${q['option' + opt] || '—'}</span>
      </label>`).join('');

    card.innerHTML = `<h3>Q${i + 1}. ${q.question}</h3>${optionsHTML}`;
    quizContainer.appendChild(card);
  });

  document.getElementById('question-count').textContent = data.length + ' questions';
}

// ── Timer ──────────────────────────────────────────────
function startTimer() {
  timerEl.textContent = '30:00';
  timerEl.classList.remove('danger');

  timerInterval = setInterval(() => {
    if (quizSubmitted) { clearInterval(timerInterval); return; }

    timeLeft--;
    const m = Math.floor(timeLeft / 60);
    const s = timeLeft % 60;
    timerEl.textContent = m + ':' + (s < 10 ? '0' + s : s);

    if (timeLeft <= 300) timerEl.classList.add('danger'); // red in last 5 min

    if (timeLeft <= 0) {
      clearInterval(timerInterval);
      alert('⏰ Time is up! Submitting your quiz now.');
      submitQuiz();
    }
  }, 1000);
}

// ── Submit & evaluate ──────────────────────────────────
function submitQuiz() {
  if (quizSubmitted) return;
  quizSubmitted = true;
  clearInterval(timerInterval);

  let correct = 0, wrong = 0, skipped = 0;

  questions.forEach((q, i) => {
    const selected = document.querySelector(`input[name="q${i}"]:checked`);

    // Disable all options and highlight correct answer green
    ['A', 'B', 'C', 'D'].forEach(opt => {
      const input = document.querySelector(`input[name="q${i}"][value="${opt}"]`);
      if (input) input.disabled = true;

      const lbl = document.getElementById(`lbl-${i}-${opt}`);
      if (lbl && opt === q.correctAnswer) lbl.classList.add('correct');
    });

    if (!selected) {
      skipped++;
    } else if (selected.value === q.correctAnswer) {
      correct++;
    } else {
      const wrongLbl = document.getElementById(`lbl-${i}-${selected.value}`);
      if (wrongLbl) wrongLbl.classList.add('wrong');
      wrong++;
    }
  });

  // Hide submit button
  document.getElementById('submitQuiz').style.display = 'none';

  // Build result
  const pct = Math.round((correct / questions.length) * 100);
  let emoji = '💪 Keep practising!';
  if (pct >= 80) emoji = '🏆 Excellent work!';
  else if (pct >= 60) emoji = '👍 Good job!';
  else if (pct >= 40) emoji = '📚 Keep studying!';

  document.getElementById('scoreNum').textContent     = correct + ' / ' + questions.length;
  document.getElementById('scoreLabel').textContent   = emoji + ' (' + pct + '%)';
  document.getElementById('correctCount').textContent = correct;
  document.getElementById('wrongCount').textContent   = wrong;
  document.getElementById('skipCount').textContent    = skipped;

  resultEl.style.display = 'block';
  resultEl.scrollIntoView({ behavior: 'smooth' });
}