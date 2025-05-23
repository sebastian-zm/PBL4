#!/usr/bin/env python3
"""
Cross-platform workspace initialization script for DevContainer.
This script prepares the temporary workspace for Java files with rsync-style synchronization.
"""

import os
import shutil
import sys
from pathlib import Path

def main():
    """Main function to create and populate workspace_tmp directory."""
    print("Initializing workspace for DevContainer...")

    # Create base directories for temporary workspace
    tmp_dir = os.path.join(".devcontainer", "workspace_tmp")
    tmp_main_java = os.path.join(tmp_dir, "src", "main", "java")
    tmp_test_java = os.path.join(tmp_dir, "src", "test", "java")

    # Create directory structure
    os.makedirs(tmp_main_java, exist_ok=True)
    os.makedirs(tmp_test_java, exist_ok=True)

    # Source directories
    main_java_dir = os.path.join("src", "main", "java")
    test_java_dir = os.path.join("src", "test", "java")

    # Sync main Java sources if they exist
    if os.path.exists(main_java_dir) and os.path.isdir(main_java_dir):
        try:
            sync_directory(main_java_dir, tmp_main_java)
            print(f"✓ Synced {main_java_dir} to temporary workspace")
        except Exception as e:
            print(f"! Error syncing {main_java_dir}: {e}", file=sys.stderr)
    else:
        print(f"! {main_java_dir} does not exist, skipping")

    # Sync test Java sources if they exist
    if os.path.exists(test_java_dir) and os.path.isdir(test_java_dir):
        try:
            sync_directory(test_java_dir, tmp_test_java)
            print(f"✓ Synced {test_java_dir} to temporary workspace")
        except Exception as e:
            print(f"! Error syncing {test_java_dir}: {e}", file=sys.stderr)
    else:
        print(f"! {test_java_dir} does not exist, skipping")

    print("✓ Workspace initialization complete!")
    return 0

def sync_directory(src, dst):
    """
    Synchronize directory contents recursively, keeping the most recent version of each file.
    Similar to rsync -a behavior. Handles deep package structures like com/example/App.java.

    Args:
        src: Source directory path
        dst: Destination directory path
    """
    src_path = Path(src)
    dst_path = Path(dst)

    # Ensure destination directory exists
    dst_path.mkdir(parents=True, exist_ok=True)

    print(f"  Scanning {src_path} recursively...")

    # Get all files and directories in source recursively
    for src_item in src_path.rglob('*'):
        # Calculate relative path from source root
        rel_path = src_item.relative_to(src_path)
        dst_item = dst_path / rel_path

        if src_item.is_dir():
            # Create directory if it doesn't exist (handles package directories)
            dst_item.mkdir(parents=True, exist_ok=True)
            print(f"  ✓ Directory {rel_path}")
        else:
            # Handle file synchronization
            should_copy = False

            if not dst_item.exists():
                # Destination file doesn't exist, copy it
                should_copy = True
                reason = "new file"
            else:
                # Compare modification times
                src_mtime = src_item.stat().st_mtime
                dst_mtime = dst_item.stat().st_mtime

                if src_mtime > dst_mtime:
                    should_copy = True
                    reason = "source is newer"
                elif dst_mtime > src_mtime:
                    reason = "destination is newer, keeping existing"
                else:
                    reason = "files are same age, keeping existing"

            if should_copy:
                # Ensure parent directory exists (critical for deep package structures)
                dst_item.parent.mkdir(parents=True, exist_ok=True)
                # Copy file with metadata
                shutil.copy2(src_item, dst_item)
                print(f"  → Copied {rel_path} ({reason})")
            else:
                print(f"  - Kept {rel_path} ({reason})")

    # Clean up files in destination that don't exist in source
    cleanup_orphaned_files(src_path, dst_path)

def cleanup_orphaned_files(src_path, dst_path):
    """
    Remove files and directories in destination that don't exist in source.
    Works recursively through deep package structures.

    Args:
        src_path: Source directory Path object
        dst_path: Destination directory Path object
    """
    # Get all items in destination, process files first, then directories
    dst_items = list(dst_path.rglob('*'))

    # Process files first
    for dst_item in dst_items:
        if dst_item.is_file():
            rel_path = dst_item.relative_to(dst_path)
            src_item = src_path / rel_path

            if not src_item.exists():
                # Remove orphaned file
                dst_item.unlink()
                print(f"  × Removed orphaned file {rel_path}")

    # Then process directories (in reverse order to handle nested structures)
    directories = [item for item in dst_items if item.is_dir()]
    directories.sort(key=lambda x: len(x.parts), reverse=True)  # Deepest first

    for dst_item in directories:
        rel_path = dst_item.relative_to(dst_path)
        src_item = src_path / rel_path

        if not src_item.exists():
            try:
                # Only remove if directory is empty
                if not any(dst_item.iterdir()):
                    dst_item.rmdir()
                    print(f"  × Removed empty directory {rel_path}")
            except OSError:
                # Directory not empty or other error, leave it
                pass

if __name__ == "__main__":
    sys.exit(main())
