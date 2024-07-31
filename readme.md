# GitHub Repositories API

This application provides an API to list all non-fork GitHub repositories of a given user along with the branch names and their last commit SHAs.

## Requirements
- Java 21
- Maven

## Running the application

1. Clone the repository:
    ```sh
    git clone <repository_url>
    ```

2. Navigate to the project directory:
    ```sh
    cd github-repositories
    ```

3. Build the application:
    ```sh
    mvn clean install
    ```

4. Run the application:
    ```sh
    mvn spring-boot:run
    ```

## API Endpoints

### Get User Repositories

- **URL:** `/api/github/repos/{username}`
- **Method:** `GET`
- **Headers:** `Accept: application/json`

#### Success Response
- **Code:** 200 OK
- **Content:**
    ```json
    [
        {
            "name": "repository-name",
            "owner": {
                "login": "owner-login"
            },
            "branches": [
                {
                    "name": "branch-name",
                    "commit": {
                        "sha": "last-commit-sha"
                    }
                }
            ]
        }
    ]
    ```

#### Error Response
- **Code:** 404 Not Found
- **Code:** 500 Internal Server Error
