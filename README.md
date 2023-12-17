# api

here is my [openapi docs](https://worthant.github.io/graphify-javaee-backend/)

<details>
<summary><h2><b>auth/signup</b></h2></summary>

```http
POST https://delicate-corgi-brightly.ngrok-free.app/api/auth/signup
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

</details>

<details>
<summary><h2><b>auth/login</b></h2></summary>

```http
POST https://delicate-corgi-brightly.ngrok-free.app/api/auth/login
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

</details>

<details>
<summary><h2><b>auth/logout</b></h2></summary>

```http
POST https://delicate-corgi-brightly.ngrok-free.app/api/auth/logout
```

- currently just returns "User logged out successfully."
// TODO: save user session duration and then display it on admin console
</details>


## TODO:

#### !надо чтобы admin/users возвращал также и id'шники!

1) добавить эндпоинты для админа:
- "removeUser\userId"
- "getUserSessions\userId"
- "getLastUserSessionDuration\userId"
- "getNumberOfUserPoints\userId"
- "changeUserTheme\userId"
2) добавить эндпоинты для юзера:
- "redrawAllPoints"
- "changePassword"
- "changeAvatar
- "theme/add"
- "theme/change"
- "theme/remove"

- all of them should be compatible with the admin user too 

...  

## NGINX config

- forwards 32350 to 32318:
> also, it fixes all the CORS headers while doing it  
> and provides routing to SNAPSHOT for shorter route
```bash
curl localhost:32350/api/auth/signup
```
