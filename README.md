# P3-BM-dashboard

## Running the backend (developing)
1. Enter the backend directory with `cd /backend`
2. Run the application with `mvn spring-boot:run`
  - Spring boot will run "docker compose up" with the "frontend" profile.
  - This will launch a `mongodb` database container
3. Stop the application by exiting the terminal, or issuing `mvn spring-boot:stop`

## Building and running with Docker Compose
1. Enter the projects root directory
2. Use `docker compose up --build`
   - This will force a `--build` instead of pulling.
   - Docker compose will **built** and start a `spring-boot` container.
   - Docker compose will **pull** and start the `mongodb` container.
3. Use `docker compose down` to stop the containers together.