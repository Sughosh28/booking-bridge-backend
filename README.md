# Event Booking Backend

This is the backend for the Event Booking project, which can be run using Docker or manually using Java and Maven. It interacts with a pre-built PostgreSQL database hosted on AWS RDS when it is run as container.

## Prerequisites

Before running the project, ensure you have the following installed:

### For Docker Setup:
- **Docker** installed on your system. [Install Docker](https://www.docker.com/get-started)

### For Manual Setup:
- **Java 21**
- **Maven**
- **PostgreSQL Driver** (included in dependencies)

## Running the Project

### Option 1: Running with Docker (Recommended)

Follow these steps to run the backend in Docker:

#### 1. Build the Docker Image
If you haven't already built the Docker image, run the following command:

docker build -t events-booking .

#### 2. Run the Docker Container
Once the image is built, run the container using:


docker run -p 8089:8089 events-booking


#### 3. Run the Frontend
Check out the Booking Bridge Frontend repository on this GitHub account. Clone it to your local machine and start it with:


npm run dev


### Option 2: Running Without Docker (Manual Setup)

#### 1. Clone the Repository

git clone https://github.com/Sughosh28/booking-bridge-backend.git
cd booking-bridge-backend

#### 2. Configure Database Connection
Edit `src/main/resources/application.properties` and update the following details with your **PgSQL credentials**:

spring.datasource.url=jdbc:postgresql://host:5432/yourdbname
spring.datasource.username=yourusername
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update

#### 3. Build the Project

mvn clean install

#### 4. Run the Application

mvn spring-boot:run

or, if you have a built JAR file:

java -jar target/event-booking-backend.jar

### API Access
After running the backend, access the API at:

http://localhost:8089/api/

You can use **Postman** or any REST client to interact with the API.

## Contributing
Feel free to submit issues or contribute by creating pull requests.

Frontend Setup
## Either way you have to use the https://github.com/Sughosh28/booking-bridge-frontend repository to run the frontend

npm run dev


