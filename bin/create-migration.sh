#!/bin/bash

# Check if a description is provided
if [ $# -eq 0 ]; then
    echo "Usage: $0 <description>"
    echo "Example: $0 create_users_table"
    exit 1
fi

# Parse the description from arguments
description=$(echo $1 | tr ' ' '_')

# Generate timestamp in format V{yyyyMMddHHmmss}__
timestamp=$(date +V%Y%m%d%H%M%S)

# Create the filename
filename="${timestamp}__${description}.sql"

# Path to migration directory
migration_dir="src/main/resources/db/migration"

# Create the directory if it doesn't exist
mkdir -p $migration_dir

# Create the migration file
file_path="$migration_dir/$filename"
touch $file_path

echo "-- Flyway migration script" > $file_path
echo "-- Created: $(date)" >> $file_path
echo "" >> $file_path
echo "-- Write your SQL below this line" >> $file_path

echo "Migration file created: $file_path"

# Open the file in the default editor if available
if command -v code &> /dev/null; then
    code $file_path
elif [ -n "$EDITOR" ]; then
    $EDITOR $file_path
fi