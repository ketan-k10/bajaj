#!/usr/bin/env bash
set -euo pipefail
mvn clean package -DskipTests
echo "Built. Run: java -jar target/webhook-server-client-1.0.0.jar"
