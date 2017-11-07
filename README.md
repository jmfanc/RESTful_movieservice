# RESTful Movie Service (Spring Boot, Data-Jpa, Security)
Application provides basic features of popular services like imdb.com or filmweb.pl. Apart from simple CRUD operations 
it also allows to rate movies/series, perform complex queries or add cast members.

## Sample requests and responses
Get first page of action or sci-fi movies with duration below 120 mins and released after 2010, sorted by release date.

    GET /shows?page=0&type=MOVIE&genres=ACTION,SCI-FI&duration_lt=120&year_gt=2010&sort=dateReleased
    
    [
        {
            "id": 1,
            "title": "Inception",
            "description": "Sleeping",
            "dateReleased": "2011-02-24",
            "location": "USA",
            "genres": [
                "sci-fi",
                "action"
            ],
            "rating": 0,
            "rateCount": 0,
            "duration": 143,
            "boxoffice": 65000000
        },
        {
            "id": 2,
            "title": "Interstellar",
            "description": "TARS",
            "dateReleased": "2014-10-26",
            "location": "USA",
            "genres": [
                "sci-fi"
            ],
            "rating": 0,
            "rateCount": 0,
            "duration": 169,
            "boxoffice": 672720017
        }
    ]


Get all details about shows rated by user from given day until now with score 8

    GET /users/s1vert/ratings?page=0&from=2017-10-04&rating=8
    
    [
        {
            "id": 1,
            "rating": 8,
            "date": "2017-10-05 15:01:38",
            "show": {
                "id": 1,
                "title": "Inception",
                "description": "Sleeping",
                "dateReleased": "2011-02-24",
                "location": "USA",
                "genres": [
                    "sci-fi",
                    "action"
                ],
                "rating": 8,
                "rateCount": 1,
                "duration": 143,
                "boxoffice": 65000000
            },
            "user": {
                "login": "s1vert",
                "name": "Tomek Stankowski",
                "email": "tomek@tomek.pl",
                "sex": "MALE",
                "joined": "2017-10-05"
            }
        }
    ]
    
Add an actor to the cast of the show

    POST /shows/423/participations/add?person=245
    
    Body
    
    {
        "role" : "ACTOR",
        "info" : "as Pablo Escobar"
    }
    
    
## Implementation
- MySQL database
- JPA Criteria API for complex queries
- secured service with Spring Security and OAuth2
- 3-layered architecture with Controller, Service and Repository
- model for persistence and DTO model for communication with clients



