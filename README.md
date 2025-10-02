# E-Learning System

## What It Does
This dual-module application combines a Spring Boot REST API with a JavaFX desktop client so lecturers can publish assignments, organise student groups, supervise collaborative use-case diagrams, and capture submissions with grading notes. Students authenticate, explore tasks, enrol in groups, and co-edit diagrams that are continuously synchronised with the backend.

## Tech Stack
- Java 17 with Maven Wrapper
- Spring Boot 3.1 (core, web, data JPA, client-side security dependency), Lombok
- JavaFX 21 EA (graphics, controls, FXML)
- Jackson, Reflections, Javatuples
- MySQL 8.x persisted through Hibernate (`ddl-auto=update`)

## Architecture
- **Client (`client/`)**: JavaFX 21 UI with Spring Boot wiring. Controllers coordinate FXML scenes, share state through `GUIApplication`, and issue HTTP calls for authentication, data fetching, enrolment, grading, and diagram persistence.
- **Server (`server/`)**: Spring Boot REST API backed by Spring Data JPA entities (`Aufgabe`, `Gruppe`, `User`, etc.). Controllers expose user, assignment, and group routes; services encapsulate validation and business logic.
- **Persistence**: MySQL schema `e-learning-system`, with Hibernate managing schema evolution (`ddl-auto=update`). Group diagrams are stored as XML strings; other entities map to relational tables with cascading saves.

## Functional Coverage
- **User lifecycle**
  - Register new accounts with client-side validation and backend uniqueness checks on usernames.
  - Log in via the REST API; passwords are verified server-side before the client unlocks navigation.
  - Retrieve full user profiles by username or id, list every user, and run prefix searches across username, first name, and last name to drive the “add member” flow.
- **Assignment management**
  - Create assignments with name, author, start/end dates, descriptive brief, number of groups, per-group capacity, default notes, and free-enrolment flag.
  - Enforce unique assignment names and calculate status (`BEFORE`, `BETWEEN`, `AFTER`) from the schedule; status updates flow through both the API and UI tables.
  - Auto-provision groups when an assignment is created, each initialised with limits, empty rosters, and submission state.
  - Fetch assignment details, render them in the client, and toggle free enrolment directly from the groups dashboard.
- **Group coordination**
  - Display all groups with occupancy counts, member rosters, submission status, and lecturer notes.
  - Allow lecturers to open a members view, remove students, or add new ones selected from global search results.
  - Let students self-enrol when capacity and policies allow; blocked enrolments prompt an optional lecturer-approved group transfer workflow.
  - Persist grades/notes per group, flip submission status between `NICHT_ABGEGEBEN` and `ABGEGEBEN`, and prevent edits once a submission is locked.
  - Expose endpoints to fetch, save, and clear each group’s diagram XML payload together with membership data.
- **Collaborative diagram editor**
  - JavaFX canvas supports adding actors, system boundaries, and use cases, plus connections with selectable arrow heads.
  - Components can be moved, resized, edited in place, or deleted; undo is backed by a state stack.
  - Editor auto-resizes to the viewport, extends the canvas when scrolling hits an edge, and keeps UI listeners in sync.
  - Diagram changes serialize to XML via reflection, are POSTed to the backend on every add/delete/save, and polling (every 200 ms) reloads remote updates when users are not actively editing.
- **Client experience**
  - Scene graph covers login, registration, assignment overview, assignment detail, new assignment wizard, groups hub, member roster, user search, and the use-case editor.
  - Table views surface assignments and groups with selection callbacks that populate shared application state (`GUIApplication`) for downstream screens.
  - Forms guard against invalid dates, negative capacities, empty fields, or password mismatches; feedback is surfaced inline.
  - Role awareness hides lecturer-only actions (grading, free enrolment toggle, member management) from students while exposing enrolment controls for them.
- **Backend services**
  - REST endpoints wrap Spring Data repositories for users, assignments, and groups; DTOs translate complex assignment creation payloads.
  - Services enforce business rules such as unique names, capacity limits, submission locks, and automatic status updates when deadlines pass.
  - Integration tests (JUnit + Spring Boot) cover core service behaviours around assignments, users, and groups.

## API Endpoints
### Users (`/user`)
- `POST /add` — register a new user.
- `POST /login/{username}/{password}` — authenticate and return the matching user.
- `GET /find/{username}` — fetch a user by username.
- `GET /search/{prefix}` — list users whose username or name starts with the prefix.
- `GET /all` — return all users.

### Assignments (`/aufgabe`)
- `POST /add` — create an assignment (accepts DTO with embedded groups).
- `GET /all` — list assignments and refresh status flags.
- `GET /find/{id}` — retrieve a single assignment.
- `GET /{id}/gruppen/all` — fetch all groups for an assignment.
- `POST /{aufgabeId}/abgabe/toggle` — toggle free enrolment for the assignment.

### Groups (`/gruppe`)
- `GET /users/all/{gruppeId}` — list group members.
- `POST /{gruppeId}/add/user/{userId}` — add a user to the group respecting capacity and uniqueness.
- `POST /{gruppeId}/save-drawn-components` — persist the XML diagram created in the client.
- `GET /{gruppeId}/get-drawn-components` — load the latest diagram XML.
- `POST /{gruppeId}/abgabe/toggle` — switch submission status.
- `DELETE /{gruppeId}/delete/user/{userId}` — remove a member from the group.
- `GET /aufgabe/find/{aufgabeId}/user/find/{userId}` — resolve the group for a user within an assignment.
- `POST /{gruppeId}/note/{note}` — attach or update lecturer feedback for the group.
- `POST /{gruppeId1}/changeto/{gruppeId2}/user/{userId}` — migrate a user between groups when allowed.

## Directory Layout
```
.
├── client/        # JavaFX + Spring Boot desktop application
│   ├── pom.xml
│   └── src/main/java/com/example/javafxdemo
│       ├── controller/…
│       └── usecaseeditor/…
├── server/        # Spring Boot REST API and integration tests
│   ├── pom.xml
│   └── src/main/java/de/unirostock/swt23/montag2/elearnsys
│       ├── model/           # JPA entities
│       ├── repo/            # Spring Data repositories
│       ├── service/         # Business logic
│       └── *.java           # REST controllers + DTOs
└── README.md
```

## Minimal Setup
1. Update `server/src/main/resources/application.properties` with real MySQL credentials and ensure the schema exists.
2. Start the backend with `cd server && ./mvnw spring-boot:run`.
3. Launch the JavaFX client with `cd client && ./mvnw spring-boot:run` (adjust `GUIApplication.URL` if the server is remote).
