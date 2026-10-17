#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 2 ]]; then
  echo "Usage: ./deploy.sh <ec2-public-ip> <path-to-private-key.pem>" >&2
  exit 2
fi

host=$1
key_file=$2
image=java-forge/aws-ec2-api:latest
project_dir=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)

if [[ ! -f "$key_file" ]]; then
  echo "SSH private key not found: $key_file" >&2
  exit 2
fi

if ! command -v docker >/dev/null 2>&1 || ! command -v ssh >/dev/null 2>&1; then
  echo "Docker and OpenSSH client must be installed locally." >&2
  exit 2
fi

echo "Building $image ..."
docker build --tag "$image" "$project_dir"

echo "Transferring image and starting the API on $host ..."
docker save "$image" \
  | gzip -c \
  | ssh -i "$key_file" \
      -o IdentitiesOnly=yes \
      -o StrictHostKeyChecking=accept-new \
      "ubuntu@$host" \
      'set -eu; gunzip -c | sudo docker load; (sudo docker rm --force java-forge-api >/dev/null 2>&1 || true); sudo docker run --detach --restart unless-stopped --name java-forge-api --publish 8080:8080 java-forge/aws-ec2-api:latest'

echo "Deployment command completed. Check http://$host:8080/health"
