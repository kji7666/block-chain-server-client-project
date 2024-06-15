#!/bin/bash
# By Harris

usage() {
    echo "Usage: $0 <tested_file_name>"
    echo "Note: please enter name without extension name"
    exit 1
}

# Check if the correct number of arguments is provided
if [ "$#" -ne 1 ]; then
    usage
fi

# Compilation step
test_file_name=$1
test_java_file="$test_file_name.java"

echo "Compiling $test_java_file..."
javac "$test_java_file"

# Check if compilation was successful
if [ $? -ne 0 ]; then
    echo "Compilation failed. Please check your Java source code for errors."
    exit 1
fi

# Run the tested class with arguments 7 to 10 and redirect output
for i in {7..10}; do 
    echo "Running $test_file_name with argument $i..."
    java "$test_file_name" "$i" > "timeoutTestResult_${i}Block" 2>&1

    # Check if the Java program ran successfully
    if [ $? -ne 0 ]; then
        echo "Error: Failed to run $test_file_name with argument $i. Check timeoutTestResult_${i}Block for details."
        exit 1  # Exit the script if an error occurs during execution
    else
        echo "$test_file_name with argument $i completed successfully."
    fi
done

echo "All tests completed."
