#!/bin/bash
set -e

echo "=> Syncing Java source files to volumes..."

# Create source directories if they don't exist
mkdir -p /workspace/src/main/java
mkdir -p /workspace/src/test/java
mkdir -p /workspace/target

# Ensure proper ownership
sudo chown -R ubuntu:ubuntu /workspace/src /workspace/target

# Sync Java files from host to volumes if they exist on host
if [ -d "/workspace_host/src/main/java" ]; then
  echo "  Syncing main Java source files..."
  rsync -au /workspace/src/main/java/ /workspace_host/src/main/java/
  rsync -au /workspace_host/src/main/java/ /workspace/src/main/java/
fi

if [ -d "/workspace_host/src/test/java" ]; then
  echo "  Syncing test Java source files..."
  rsync -au /workspace/src/test/java/ /workspace_host/src/test/java/
  rsync -au /workspace_host/src/test/java/ /workspace/src/test/java/
fi

echo "✅ Java source files synced"
