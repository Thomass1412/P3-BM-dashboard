# P3-BM-dashboard
#### Run docker in production
docker compose --profile prod up --build

- "prod" is the profile which runs the backend in docker.
  - spring-boot run does not run the backend in docker. It skips building.
- --build forces a new build.