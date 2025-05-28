#!/usr/bin/env python3
"""
Pre-commit hook to sync Java files from Docker container volumes back to the host.
This ensures that modified Java files in volumes are properly committed.
"""

import os
import subprocess
import sys
import shutil

def main():
    """Main function to sync Java files before commit."""
    # Check if we're in a repository with a devcontainer configuration
    if not os.path.exists('.devcontainer'):
        print("No .devcontainer directory found, skipping sync")
        return 0

    print("Syncing Java files from container volumes to host...")

    # Check if Docker is available
    try:
        subprocess.run(["docker", "--version"], check=True, capture_output=True)
    except (subprocess.SubprocessError, FileNotFoundError):
        print("Docker not available, skipping sync")
        return 0

    # Get container ID
    try:
        container_id = subprocess.run(
            ["docker", "compose", "ps", "-q", "app"],
            check=True, capture_output=True, text=True
        ).stdout.strip()
    except subprocess.SubprocessError:
        # Try the older docker-compose command if docker compose fails
        try:
            container_id = subprocess.run(
                ["docker-compose", "ps", "-q", "app"],
                check=True, capture_output=True, text=True
            ).stdout.strip()
        except subprocess.SubprocessError:
            print("Could not get container ID, skipping sync")
            return 0

    # If no container is running, exit
    if not container_id:
        print("No container running, skipping sync")
        return 0

    # Ensure tmp directories exist
    os.makedirs('.devcontainer/workspace_tmp/src/main/java', exist_ok=True)
    os.makedirs('.devcontainer/workspace_tmp/src/test/java', exist_ok=True)

    # Sync main Java files
    print("Syncing main Java files...")
    sync_directory(container_id, '/workspace/src/main/java/', '.devcontainer/workspace_tmp/src/main/java/')

    # Sync test Java files
    print("Syncing test Java files...")
    sync_directory(container_id, '/workspace/src/test/java/', '.devcontainer/workspace_tmp/src/test/java/')

    # Copy from temp directory to actual source directories
    print("Copying to source directories...")

    # Create source directories if they don't exist
    os.makedirs('src/main/java', exist_ok=True)
    os.makedirs('src/test/java', exist_ok=True)

    # Sync main Java files from tmp to source
    copy_directory('.devcontainer/workspace_tmp/src/main/java', 'src/main/java')

    # Sync test Java files from tmp to source
    copy_directory('.devcontainer/workspace_tmp/src/test/java', 'src/test/java')

    print("✓ Java files synced successfully!")
    return 0

def sync_directory(container_id, container_path, host_path):
    """
    Sync a directory from container to host using docker cp.

    Args:
        container_id: Docker container ID
        container_path: Path in the container
        host_path: Path on the host
    """
    try:
        # Clear the target directory first to avoid stale files
        if os.path.exists(host_path):
            shutil.rmtree(host_path)
        os.makedirs(host_path, exist_ok=True)

        # Use docker cp to copy files from container to host
        # Note: docker cp container_id:/path/. /host/path/ copies contents without the directory itself
        source_path = f"{container_id}:{container_path}."
        result = subprocess.run(
            ["docker", "cp", source_path, host_path],
            check=True, capture_output=True
        )
        return True
    except subprocess.SubprocessError as e:
        stderr = e.stderr.decode('utf-8') if hasattr(e, 'stderr') and e.stderr else ''
        print(f"Error syncing {container_path}: {stderr}", file=sys.stderr)
        return False

def copy_directory(src, dst):
    """
    Copy directory contents recursively.

    Args:
        src: Source directory path
        dst: Destination directory path
    """
    if not os.path.exists(src):
        return

    if not os.path.exists(dst):
        os.makedirs(dst)

    for item in os.listdir(src):
        s = os.path.join(src, item)
        d = os.path.join(dst, item)

        if os.path.isdir(s):
            copy_directory(s, d)
        else:
            # Copy file
            shutil.copy2(s, d)

if __name__ == "__main__":
    sys.exit(main())
