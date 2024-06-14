#!/bin/bash

# Usage function to display correct script usage
usage() {
    echo "Usage: $0 <tested_file> <result_file> <time_in_minutes>"
    exit 1
}

# Check if the correct number of arguments are provided
if [ "$#" -ne 3 ]; then
    usage
fi

# Assign input arguments to variables
TESTED_FILE=$1
RESULT_FILE=$2
TIME_LIMIT=$3

# Convert time limit from minutes to seconds
TIME_LIMIT_IN_SECONDS=$((TIME_LIMIT * 60))

# Print a message to indicate the start of the script
echo "Running $TESTED_FILE with a time limit of $TIME_LIMIT minutes, output to $RESULT_FILE"

# Run the Java program with a timeout
run_with_timeout() {
  local file=$1
  local output=$2
  local limit=$3

  java "$file" > "$output" &
  local java_pid=$!

  echo "Java process started with PID: $java_pid"

  sleep "$limit" && kill -9 "$java_pid" 2>/dev/null && echo "Java process exceeded time limit and was terminated" &
  local watcher_pid=$!

  wait "$java_pid" 2>/dev/null
  kill -9 "$watcher_pid" 2>/dev/null
}


run_with_timeout "$TESTED_FILE" "$RESULT_FILE" "$TIME_LIMIT_IN_SECONDS"
echo "Executed $TESTED_FILE and saved the result to $RESULT_FILE at $(date)"

