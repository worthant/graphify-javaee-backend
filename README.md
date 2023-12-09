# api

## auth/signup

```http request
http://localhost:8080/javaee-interactive-graph-backend-1.0-SNAPSHOT/api/auth/signup
```
- usage:
  
```json
{
    "username": "user123",
    "password": "12345"
}
```

- returns:

```json
{
    "token": "someJWTtoken"
}
```

## TODO:

- auth/login
- auth/logout
- graph/getAllPoints
- graph/addPoint
- graph/deleteAllPoints
- theme/add
- theme/change
- theme/remove  
...  
...  

## NGINX config

- forwards 32350 to 32318:
> also, it fixes all the CORS headers while doing it  
> and provides routing to SNAPSHOT for shorter route
```bash
curl localhost:32350/api/auth/signup
```