#!/bin/bash
echo "Verificando portas dos microsserviços..."

ports=(8080 8081 8082 8083 8084)
names=("API Gateway" "User Service" "Messaging Service" "Notification Service" "Calling Service")

for i in "${!ports[@]}"; do
  port="${ports[$i]}"
  name="${names[$i]}"
  if ss -tulpn | grep -q ":$port "; then
    echo "🟢 ONLINE: $name (Porta $port)"
  else
    echo "🔴 OFFLINE: $name (Porta $port)"
  fi
done
