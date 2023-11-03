# P3-BM-dashboard

# Getting started
## Pre-requisites for running
- **Java 21 installed**
  - Confirm with `java -version`
```
PS C:\Users\olive\Documents\Programming\P3-BM-dashboard\backend> java -version
openjdk version "21.0.1" 2023-10-17 LTS
OpenJDK Runtime Environment Temurin-21.0.1+12 (build 21.0.1+12-LTS)
OpenJDK 64-Bit Server VM Temurin-21.0.1+12 (build 21.0.1+12-LTS, mixed mode, sharing)
```
- **Maven installed**
  - Confirm with `mvn -version`
```
PS C:\Users\olive\Documents\Programming\P3-BM-dashboard\backend> mvn -version
Apache Maven 3.9.4 (dfbb324ad4a7c8fb0bf182e6d91b0ae20e3d2dd9)
Maven home: C:\Program Files\Maven\apache-maven-3.9.4
Java version: 21.0.1, vendor: Eclipse Adoptium, runtime: C:\Program Files\Eclipse Adoptium\jdk-21.0.1.12-hotspot
Default locale: en_US, platform encoding: UTF-8
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"
```

## Running the backend (developing)
1. Enter the backend directory with `cd /backend`
2. Run the application with `mvn spring-boot:run`
  - Spring boot will run ``docker compose up`` with the "frontend" profile.
  - This will launch a `mongodb` database container **only** (Don't build the Java backend docker container, since it will launch locally)
3. Stop the application by exiting the terminal, or issuing `mvn spring-boot:stop`

## Building and running with Docker Compose
1. Enter the projects root directory
2. Use `docker compose up --build`
   - This will force a `--build` instead of pulling.
   - Docker compose will **built** and start a `spring-boot` container.
   - Docker compose will **pull** and start the `mongodb` container.
3. Use `docker compose down` to stop the containers together.