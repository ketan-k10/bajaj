
# Webhook Server + Client (Spring Boot)

This project contains a Spring Boot application that implements **both**:
- A server exposing two endpoints:
  - `POST /hiring/generateWebhook/JAVA` — accepts `{name, regNo, email}` and returns a JSON `{webhook, accessToken}` where `webhook` points to the `testWebhook` endpoint and `accessToken` is a signed JWT.
  - `POST /hiring/testWebhook/JAVA` — accepts `{ finalQuery }` and requires a valid `Authorization` header containing the JWT returned earlier. It validates the JWT and returns a confirmation JSON.
- A startup client (runs automatically at application startup) that:
  - Calls the `generateWebhook` endpoint (configurable URL).
  - Reads `app.finalQuery` (from `application.yml`) and submits it to the returned `webhook` using the returned JWT in the `Authorization` header.

This matches the flow you described. The project is self-contained and ready to build.

## What you get in this zip
- Full source code (Java + Maven pom)
- `application.yml` with configuration
- `build-and-run.sh` — helper script (unix) to build and run the app
- `README.md` (this file)

## How to build and run (detailed)

### Prerequisites
- Java 17 JDK installed and `java`/`javac` on PATH
- Maven installed (`mvn`) — required to build the JAR
- (Optional) Git if you want to push to GitHub

### Build
```bash
# from project root (where pom.xml is)
mvn clean package -DskipTests
```
This produces a runnable jar at `target/webhook-server-client-1.0.0.jar`

### Run
You can run the jar directly:
```bash
java -jar target/webhook-server-client-1.0.0.jar
```
By default the embedded server listens on port 8080. The startup runner executes and performs the client flow (calls generateWebhook and then posts your query).

### Configuration
Edit `src/main/resources/application.yml` before building (or pass overrides via `-D` or environment variables). The important properties:

```yaml
app:
  # Where the startup client will call to get webhook. Use the locally hosted endpoint for testing:
  generateWebhookUrl: "http://localhost:8080/hiring/generateWebhook/JAVA"

  # Put your final SQL query here (or provide a file path)
  finalQuery: "SELECT * FROM users WHERE id = 1;"
  # finalQueryFilePath: "/absolute/path/to/query.sql"

  # JWT secret used by server to sign tokens and by server to validate them when testWebhook is hit.
  jwtSecret: "very-strong-secret-change-me"

  # Authorization header prefix. Use empty string to send raw token, or "Bearer " to send Bearer <token>
  authHeaderPrefix: ""
```

### Testing manually with curl
1. Call `generateWebhook`:
```bash
curl -s -X POST http://localhost:8080/hiring/generateWebhook/JAVA \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","regNo":"REG12347","email":"john@example.com"}' | jq
```

2. Use the returned `accessToken` and `webhook` to POST your finalQuery:
```bash
curl -s -X POST "http://localhost:8080/hiring/testWebhook/JAVA" \
  -H "Content-Type: application/json" \
  -H "Authorization: eyJhbGciOi..." \
  -d '{"finalQuery":"SELECT 1"}' | jq
```

## Submission checklist (how you should prepare):

1. Create a public GitHub repo and push the full project.
2. Build the project locally: `mvn clean package -DskipTests` and obtain the JAR `target/webhook-server-client-1.0.0.jar`
3. Upload the JAR to GitHub (release or raw file) so it is downloadable via a raw URL.
4. Fill the submission form: https://forms.office.com/r/ZbcqfgSeSw
   - Include the GitHub repo URL
   - Include the public downloadable JAR link
   - Include any notes or instructions

If you want, I can prepare a `git push` script and help craft the GitHub README content for your repo. 
