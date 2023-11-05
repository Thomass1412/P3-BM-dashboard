# P3-BM-dashboard
Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin ligula sem, consectetur a risus euismod, finibus eleifend augue. Suspendisse mauris arcu, vestibulum sit amet libero pellentesque, commodo commodo tellus. Donec sit amet ex odio. In dictum egestas metus, non pellentesque arcu. Mauris eros velit, euismod pulvinar vehicula et, faucibus eget velit. Maecenas accumsan mattis vestibulum. Curabitur vitae nibh eros. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. 

# Getting started
## Installing dependencies and tooling

### 1. Install Java 21
- Confirm your current version with `java -version`

It should be version ``21.x.x``
```
PS C:\Users\olive\Documents\Programming\P3-BM-dashboard\backend> java -version
openjdk version "21.0.1" 2023-10-17 LTS
OpenJDK Runtime Environment Temurin-21.0.1+12 (build 21.0.1+12-LTS)
OpenJDK 64-Bit Server VM Temurin-21.0.1+12 (build 21.0.1+12-LTS, mixed mode, sharing)
```

Otherwise download and install [here](https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.1+12/OpenJDK21U-jdk_x64_windows_hotspot_21.0.1_12.msi).

1. Use the wizard to install the JDK.
   - **important!** choose "Set JAVA_HOME variable" in the installer!

### 2. Install Maven
1. Download, unpack and add to "PATH"
   - [Installing Apache Maven](https://maven.apache.org/install.html)
2. Confirm correct version is installed
   - `mvn -version`

```
PS C:\Users\olive\Documents\Programming\P3-BM-dashboard\backend> mvn -version
Apache Maven 3.9.4 (dfbb324ad4a7c8fb0bf182e6d91b0ae20e3d2dd9)
Maven home: C:\Program Files\Maven\apache-maven-3.9.4
Java version: 21.0.1, vendor: Eclipse Adoptium, runtime: C:\Program Files\Eclipse Adoptium\jdk-21.0.1.12-hotspot
Default locale: en_US, platform encoding: UTF-8
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"
```

### 3. Install Docker Desktop

> [!WARNING]
> Spring is broken with the *latest* docker compose version ``2.23.0``, and **will not run**.
> 
> Instead of downloading latest, use version `4.23.0` of Docker Desktop [here](https://docs.docker.com/desktop/release-notes/#4230)
> 
> Confirm with `docker-compose --version` that you are running version `2.20.3`
> 
> Issue on Stack overflow [here](https://stackoverflow.com/questions/77385146/springboot-docker-error-cannot-invoke-dockercliinspectresponse-hostconfig-b)

### 4. Install MongoDB Compass
MongoDB Compass allows you to browse the database.

1. Download and install [here](https://www.mongodb.com/products/tools/compass)
2. Check the `docker-compose.yaml` in the project root for database password and user (Authorization)
```
- 'MONGO_INITDB_DATABASE=dashboard-db'
- 'MONGO_INITDB_ROOT_PASSWORD=secret'
- 'MONGO_INITDB_ROOT_USERNAME=root'
```
3. Connect via `mongodb://root:secret@localhost:27017/?authMechanism=DEFAULT`

# Running and building

## All run commands
This are the commands to run the application, with either Spring dev server, Vite dev server or as a whole with `docker compose`

| Component      | Directory | Command                                  | Frontend                          | Backend                           | Database                        | Nginx                           |
| -------------- | --------- | ---------------------------------------- | --------------------------------- | --------------------------------- | ------------------------------- | ------------------------------- |
| Java backend   | /backend  | mvn spring-boot:run                      | Build docker `:white_check_mark:` | Dev server `:heavy_check_mark:`   | Run docker `:white_check_mark:` | Run docker `:white_check_mark:` |
| Vue Frontend   | /frontend | npm run dev                              | Dev server `:heavy_check_mark:`   | Build docker `:white_check_mark:` | Run docker `:white_check_mark:` | Run docker `:white_check_mark:` |
| Docker compose | /         | docker compose --profile prod up --build | Build docker `:white_check_mark:` | Build docker `:white_check_mark:` | Run docker `:white_check_mark:` | Run docker `:white_check_mark:` |

## Running the backend (dev server)
To start Spring for backend development:

1. Enter the backend directory with `cd /backend`
2. Run the application with `mvn spring-boot:run`
   - Spring boot will run ``docker compose up``
   - This will launch a `mongodb` database container **only** (Don't build the Java backend docker container, since it will launch locally)
3. Stop the application by exiting the terminal, or issuing `mvn spring-boot:stop`

## Running the frontend (dev server)
To start Vite / Vue for frontend development:

1. Enter the frontend directory with `cd /frontend`
2. Run the dev server with `npm run dev`

## Building and running the whole stack with Docker Compose
To build frontend, and backend and start the whole stack. Mostly for deployment.

1. Enter the projects root directory
2. Use `docker compose --profile prod up --build`
   - This will force a `--build` instead of pulling.
   - The `--profile prod` enables the `spring-boot` container to be build (Not used when using `mvn spring-boot:run`, creates duplicate).
   - Docker compose will **built** and start a `spring-boot` container.
   - Docker compose will **built** and start a `frontend` container.
   - Docker compose will **pull** and start the `mongodb` container.
   - Docker compose will **nginx** and start the `nginx` container.
3. Connect to the `nginx` reverse proxy on `http://localhost` or `http://localhost/api/v1`
3. Use `docker compose down` to stop the containers together.




# Frontend notes

## Creating vue app

1. Enter project root `cd /P3-BM-dashboard`
2. Use `npm create vue@latest` to create a vue project

https://github.com/vuejs/create-vue

Run:
 `npm create vue@latest`

 ```
 PS C:\Users\Oliver\Documents\Programming\P3-BM-dashboard> npm create vue@latest

 Vue.js - The Progressive JavaScript Framework

√ Project name: ... frontend
√ Add TypeScript? ... No / Yes
√ Add JSX Support? ... No / Yes
√ Add Vue Router for Single Page Application development? ... No / Yes
√ Add Pinia for state management? ... No / Yes
√ Add Vitest for Unit Testing? ... No / Yes
√ Add an End-to-End Testing Solution? » No
√ Add ESLint for code quality? ... No / Yes
√ Add Prettier for code formatting? ... No / Yes

Scaffolding project in C:\Users\Oliver\Documents\Programming\P3-BM-dashboard\frontend...

Done. Now run:

  cd frontend
  npm install
  npm run format
  npm run dev
 ```


 ```
# ping backend
PING backend (172.20.0.3) 56(84) bytes of data.
64 bytes from backend.p3-bm-dashboard_backnet (172.20.0.3): icmp_seq=1 ttl=64 time=0.101 ms
64 bytes from backend.p3-bm-dashboard_backnet (172.20.0.3): icmp_seq=2 ttl=64 time=0.042 ms
64 bytes from backend.p3-bm-dashboard_backnet (172.20.0.3): icmp_seq=3 ttl=64 time=0.050 ms
^C
--- backend ping statistics ---
3 packets transmitted, 3 received, 0% packet loss, time 2044ms
rtt min/avg/max/mdev = 0.042/0.064/0.101/0.026 ms


# ping frontend
ping: frontend: Temporary failure in name resolution
 ```