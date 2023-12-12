#!/bin/bash

echo "Deploying to Helios"

## Remove existing deployment
ssh -p 2222 s368090@se.ifmo.ru "rm -rf wildfly-30.0.1.Final/standalone/deployments/javaee-interactive-graph-backend-1.0-SNAPSHOT.war"
# add new deployment
scp -P 2222 ./target/javaee-interactive-graph-backend-1.0-SNAPSHOT.war s368090@se.ifmo.ru:wildfly-30.0.1.Final/standalone/deployments