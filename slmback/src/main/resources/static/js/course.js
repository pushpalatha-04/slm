const courseModal = document.getElementById("courseModal");
const closeCourse = document.getElementById("closeCourse");
const featureLinks = document.querySelectorAll(".feature-link");
const featureContents = document.querySelectorAll(".feature-content");

// Open modal from any course card button
document.querySelectorAll(".openCourse").forEach(btn => {
  btn.onclick = () => {
    courseModal.style.display = "flex";
    unlockAssessment(false); // reset assessment lock
  };
});

// Close modal
closeCourse.onclick = () => courseModal.style.display = "none";

// Sidebar feature switching
featureLinks.forEach(link => {
  link.onclick = function() {
    if(link.classList.contains("locked")) return; // ignore if locked
    featureLinks.forEach(l => l.classList.remove("active"));
    link.classList.add("active");
    const target = link.dataset.feature;
    featureContents.forEach(c => {
      c.style.display = (c.id === target) ? "block" : "none";
    });
  };
});

// Simple JS code runner for practice coding
document.getElementById("runCode").onclick = function() {
  const code = document.getElementById("codeArea").value;
  try {
    const result = eval(code);
    document.getElementById("outputArea").innerText = result;
  } catch(e) {
    document.getElementById("outputArea").innerText = e;
  }
}

// Unlock assessment after videos completed
function unlockAssessment(status) {
  const assessment = document.querySelector(".feature-link.locked");
  if(status){
    assessment.classList.remove("locked");
    assessment.innerText = "Major Assessment";
  } else {
    assessment.classList.add("locked");
    assessment.innerText = "Major Assessment 🔒";
  }
}