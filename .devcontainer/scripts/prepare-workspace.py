#!/usr/bin/env python3
"""
Cross-platform workspace initialization script for DevContainer.
This script prepares the temporary workspace for Java files.
"""

import os
import shutil
import sys

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

    # Copy main Java sources if they exist
    if os.path.exists(main_java_dir) and os.path.isdir(main_java_dir):
        # Remove previous content to avoid stale files
        if os.path.exists(tmp_main_java):
            shutil.rmtree(tmp_main_java)
            os.makedirs(tmp_main_java, exist_ok=True)

        try:
            # Copy directory content
            copy_directory(main_java_dir, tmp_main_java)
            print(f"✓ Copied {main_java_dir} to temporary workspace")
        except Exception as e:
            print(f"! Error copying {main_java_dir}: {e}", file=sys.stderr)
    else:
        print(f"! {main_java_dir} does not exist, skipping")

    # Copy test Java sources if they exist
    if os.path.exists(test_java_dir) and os.path.isdir(test_java_dir):
        # Remove previous content to avoid stale files
        if os.path.exists(tmp_test_java):
            shutil.rmtree(tmp_test_java)
            os.makedirs(tmp_test_java, exist_ok=True)

        try:
            # Copy directory content
            copy_directory(test_java_dir, tmp_test_java)
            print(f"✓ Copied {test_java_dir} to temporary workspace")
        except Exception as e:
            print(f"! Error copying {test_java_dir}: {e}", file=sys.stderr)
    else:
        print(f"! {test_java_dir} does not exist, skipping")

    print("✓ Workspace initialization complete!")
    return 0

def copy_directory(src, dst):
    """
    Copy directory contents recursively.

    Args:
        src: Source directory path
        dst: Destination directory path
    """
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
