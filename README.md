# api

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
