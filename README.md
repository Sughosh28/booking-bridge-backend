# Event Booking Backend

This is the backend for the Event Booking project, where backend is ran using Docker. It interacts with a database which is alread built in.

## Prerequisites

Before running the Docker container, ensure you have the following:

- **Docker** installed on your system. [Install Docker](https://www.docker.com/get-started)


## Running the Project

Follow these steps to run the backend in Docker:

### 1. Build the Docker image
If you haven't already built the Docker image, you can do so with the following command:


docker build -t event-booking .

Run the Docker Container
After the image is built, run the container using the command below:

docker run -p 8089:8089 event-booking


Then, check out the Booking Bridge Frontend repository on this GitHub account. Clone it to your local machine, run it locally using the following command, and access the backend API.


npm run dev
