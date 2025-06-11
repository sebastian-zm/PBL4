#!/usr/bin/env python3
"""
Script to install Git hooks for the project.
"""

import os
import shutil
import stat

def main():
    """Main function to install Git hooks."""
    # Get the directory of this script
    script_dir = os.path.dirname(os.path.abspath(__file__))

    # Get the project root (parent directory of script_dir)
    project_root = os.path.dirname(script_dir)

    # Path to hooks directory
    hooks_dir = os.path.join(project_root, '.git', 'hooks')

    # Ensure hooks directory exists
    if not os.path.exists(hooks_dir):
        os.makedirs(hooks_dir, exist_ok=True)

    # Path to pre-commit script in this repo
    pre_commit_source = os.path.join(script_dir, 'pre-commit.py')

    # Path to install the pre-commit hook
    pre_commit_dest = os.path.join(hooks_dir, 'pre-commit')

    # Copy the hook
    shutil.copy2(pre_commit_source, pre_commit_dest)

    # Make executable
    st = os.stat(pre_commit_dest)
    os.chmod(pre_commit_dest, st.st_mode | stat.S_IEXEC)

    print(f"✓ Pre-commit hook installed to {pre_commit_dest}")
    return 0

if __name__ == "__main__":
    main()