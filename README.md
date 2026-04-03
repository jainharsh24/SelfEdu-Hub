# Self-Study Planner (Mini Project)

## Introduction
Self-Study Planner is a Spring Boot web app that helps learners generate a personalized study roadmap, track weekly progress, and validate learning with AI-assisted quizzes. It combines roadmap generation, curated learning links, and test logs into a single workflow for structured self-learning.

## Features
- AI-powered roadmap generation based on field, level, and duration.
- Supports custom syllabus topics and highlights them in the plan.
- Weekly breakdown with milestones, subtopics, and progress tracking.
- One-click YouTube resource links for each week.
- AI-generated weekly explanations for quick revision.
- Automatic quiz creation, test-taking, and review flow.
- Test logs dashboard with scores and status.
- PDF export of the complete roadmap.
- Secure login and user-specific roadmaps.

## Screenshots
### Home Page

<!-- Add home page screenshot here -->

### Self-Learning Page

<!-- Add self-learning (roadmap detail) screenshot here -->

### Test Page

<!-- Add test page screenshot here -->

### Test Logs Page

<!-- Add test logs screenshot here -->

## How It Works
- User registers and logs in.
- User fills in field, level, duration, and optional syllabus topics.
- The app generates a roadmap (predefined or AI-generated).
- User saves the roadmap and starts learning week by week.
- Progress is tracked as topics are completed.
- For each week, user can fetch learning links and an AI explanation.
- After completing a week, the app offers a test with MCQs.
- Test results are saved and shown in the test logs.
- Roadmap can be exported as a PDF.

## Installation Guide
1. Install JDK 25 (required by the project) and ensure Java is on your PATH.
2. Configure a database (default is PostgreSQL) and update `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` in `src/main/resources/application.properties`.
3. Set API keys for AI features using `GROQ_API_KEY` and `YOUTUBE_API_KEY`. You can set them as environment variables or place them in a local `.env` file.
4. Run the app:
   Windows: `mvnw.cmd spring-boot:run`
   macOS/Linux: `./mvnw spring-boot:run`
5. Open `http://localhost:8080` in your browser.
