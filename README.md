Smart Learning Management

A full-stack Learning Management System built with Spring Boot,MongoDB Atlas, and  HTML/CSS/JS. Tutors can create courses, add content, generate AI-powered quizzes, track attendance, and chat with students in real time. Students can enroll, learn, submit work, and track their progress.
Live Url:https://slm-b8oo.onrender.com
Tech Stack

 Backend - Java 17 + Spring Boot 
 Database - MongoDB Atlas 
 AI - Google Gemini 2.5 Flash API 
Frontend - HTML, CSS,JavaScript 
 Deployment - Docker + Render 

 Features

 Tutor
Role-based dashboard — tutors and students see completely different UIs after login
Course management — create courses, auto-generate a 6-digit enroll code, delete courses
Content management — add YouTube videos, Google Drive PDF links, coding tests per course
Quiz builder (AI) — enter a topic, Gemini generates 20 MCQ questions automatically and saves them to the course
tudy plan — create a per-course weekly roadmap (week number, topic, description, resource link)
Attendance — bulk mark present/absent for any date, load saved records
Submissions review — view student quiz scores and submitted code, add feedback
Student list — view all enrolled students per course with enrollment date
Real-time chat — group chat or one-on-one with any student via WebSocket
 Student
Enroll — join a course using a 6-digit tutor-provided code
Learn — watch embedded YouTube videos, open PDF links, read problem statements
Quiz — attempt multiple-choice quizzes, see score and correct answers instantly
Coding test— write and submit code for tutor review
Attendance — view personal attendance percentage and record per course
Study plan — view the tutor's weekly plan for each enrolled course
Real-time chat — chat with tutor and classmates

 Project Structure


slm/
├── Dockerfile
└── slmback/
    ├── pom.xml
    └── src/main/
        ├── java/com/example/slmback/
        │   ├── config/
        │   │   ├── CorsConfig.java
        │   │   └── WebSocketConfig.java
        │   ├── controller/
        │   │   ├── AuthController.java
        │   │   ├── AttendanceController.java
        │   │   ├── ChatController.java
        │   │   ├── ContentController.java
        │   │   ├── CourseController.java
        │   │   ├── ProgressController.java
        │   │   ├── QuizController.java
        │   │   ├── StudyPlanController.java
        │   │   └── SubmissionController.java
        │   ├── model/
        │   │   ├── Attendance.java
        │   │   ├── Course.java
        │   │   ├── CourseContent.java
        │   │   ├── Enrollment.java
        │   │   ├── Message.java
        │   │   ├── Question.java
        │   │   ├── StudentProgress.java
        │   │   ├── StudyPlan.java
        │   │   ├── Submission.java
        │   │   └── User.java
        │   ├── repository/         # MongoDB repositories
        │   ├── security/
        │   │   ├── JwtUtil.java
        │   │   └── SecurityConfig.java
        │   ├── service/
        │   │   ├── GeminiService.java
        │   │   └── QuizService.java
        │   └── util/
        │       └── GeminiParser.java
        └── resources/
            ├── application.properties
            └── static/
                ├── login.html
                ├── tutor-dashboard.html
                ├── student-dashboard.html
                └── js/
                    └── auth.js

Prerequisites
- Java 17+
- Maven 3.8+
- MongoDB Atlas account (free tier works)
- Google Gemini API key (free at [aistudio.google.com](https://aistudio.google.com))

 1. Clone the repository

git clone https://github.com/pushpalatha-04/slm.git
cd slm/slmback


 2. Configure `application.properties`

Edit `src/main/resources/application.properties`:

properties
 MongoDB Atlas
spring.data.mongodb.uri=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/<dbname>?retryWrites=true&w=majority

Gemini AI
gemini.api.key=your-gemini-api-key

 Server
server.port=8080


3. Run the backend

./mvnw spring-boot:run

 4. Open the frontend

Open 
http://localhost:8080/login.html

Deployment (Render + Docker)

1. MongoDB Atlas — allow all IPs
In Atlas → Network Access → Add IP → `0.0.0.0/0` (allows Render's dynamic IPs)

 2. Set environment variables on Render
In your Render service → Environment:


SPRING_DATA_MONGODB_URI=mongodb+srv://...
JWT_SECRET=your-secret
JWT_EXPIRATION=86400000
GEMINI_API_KEY=your-key

 3. Update `application.properties` to read from env vars

properties
spring.data.mongodb.uri=${SPRING_DATA_MONGODB_URI}
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}
gemini.api.key=${GEMINI_API_KEY}



 5. Deploy
Push to GitHub → Render auto-builds from your Dockerfile.

Author

Pushpalatha j
