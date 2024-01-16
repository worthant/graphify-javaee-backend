<!-- Here is the main logo and name of your project -->

<p align="center">
  <a href="resources/MVC.png">
    <picture>
      <img src="https://github.com/worthant/graphify-angular-frontend/assets/43885024/970c9fdb-ded9-4942-92ec-7e93495d69ce" height="150">
    </picture>
    <h1 align="center">Graphify</h1>
  </a>
</p>

<!-- Here are some cool labels for your project, deledte those, that you don't need -->

<p align="center">
   <a aria-label="Translation ro russian" href="./README_RU.md">
      <img alt="" src="https://img.shields.io/badge/translation-RU-007FFF?style=for-the-badge&labelColor=000000&color=007FFF">
   </a>
<a aria-label="Java EE 10" href="https://javaee.github.io/javaee-spec/">
   <img alt="" src="https://img.shields.io/badge/Java_EE_10-4FD1C5?style=for-the-badge&labelColor=000000&color=4FD1C5">
</a>
<a aria-label="JetBrains Runtime 17" href="https://www.jetbrains.com/lp/jbr/">
   <img alt="" src="https://img.shields.io/badge/jbr_17-22D3EE?style=for-the-badge&logo=jetbrains&labelColor=000000&color=22D3EE">
</a>
<a aria-label="Wildfly 30.0.1" href="https://www.wildfly.org/">
   <img alt="" src="https://img.shields.io/badge/Wildfly_30.0.1-1ff2d6?style=for-the-badge&labelColor=000000&color=1ff2d6">
</a>
    <a aria-label="PostgreSQL" href="https://www.postgresql.org/">
   <img alt="" src="https://img.shields.io/badge/PostgreSQL-007FFF?style=for-the-badge&logo=postgresql&labelColor=000000&color=007FFF">
</a>
<a aria-label="Maven Project" href="https://maven.apache.org/">
   <img alt="" src="https://img.shields.io/badge/Maven_Project-C71A36?style=for-the-badge&logo=apache-maven&labelColor=000000&color=C71A36">
</a>

</p>

### Notes

- here are my [openapi docs](https://worthant.github.io/graphify-javaee-backend/)
- here is my [graphify-angular-frontend](https://github.com/worthant/graphify-angular-frontend)

## Nginx config on my server

```nginx
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log notice;
pid /run/nginx.pid;

# Load dynamic modules. See /usr/share/doc/nginx/README.dynamic.
include /usr/share/nginx/modules/*.conf;

events {
    worker_connections 1024;
}

http {
    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile            on;
    tcp_nopush          on;
    keepalive_timeout   65;
    types_hash_max_size 4096;

    include             /etc/nginx/mime.types;
    default_type        application/octet-stream;

    # Load modular configuration files from the /etc/nginx/conf.d directory.
    # See http://nginx.org/en/docs/ngx_core_module.html#include
    # for more information.
    include /etc/nginx/conf.d/*.conf;

    server {
    listen 7777;
    server_name localhost;

    # Default location for Web Interface
    location / {
        proxy_pass http://localhost:9090;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }

    # API service
    location /api/ {
        proxy_pass http://localhost:32350/javaee-interactive-graph-backend-1.0-SNAPSHOT/api/;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
    }
}
}

```

## Ngrok config on my server

- just forwards 7777 port of localhost to ngrok's static domain
- i am sending requests to this domain and nginx routes them to uni server through port forwarding    

## Port forwarding on my home server

- bashrc
```bash
sshl() {
  if [ $# -eq 0 ]; then
    ssh -L 32350:localhost:32350 s368090@se.ifmo.ru -p 2222
    elif [ $# -eq 1 ]; then
        ssh -L ${1}:localhost:${1} s368090@se.ifmo.ru -p 2222
    elif [ $# -eq 2 ]; then
        ssh -L ${1}:localhost:${2} s368090@se.ifmo.ru -p 2222
    else
        echo "Invalid number of arguments. Please provide either 0 (port 32318), 1 (port:port) or 2 (port1:port2)."
    fi
}
```

## Nginx config on uni server

- forwards 32350 to 32318:
> provides routing to SNAPSHOT for shorter route
```bash
curl localhost:32350/api/auth/signup
```
