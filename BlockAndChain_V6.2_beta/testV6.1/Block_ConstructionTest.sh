#!/bin/bash
#By Harris

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

# Run the BlockTestV5 class with arguments 1 to 8 and redirect output
for i in {1..8}; do 
    echo "Running BlockTestV5 with argument $i..."
    java $test_file_name $i > ${test_file_name}TestResultForCreateing_${i}Block 2>&1

    # Check if the Java program ran successfully
    if [ $? -ne 0 ]; then
        echo "Error: Failed to run BlockTestV5 with argument $i. Check blockV5TestResultForCreateing_${i}Block for details."
    else
        echo "BlockTestV5 with argument $i completed successfully."
    fi
done

echo "All tests completed."








# # Function to clear the line
# clear_line() {
#     printf "\r\033[K"
# }

# # Function to display loading animation
# loading_animation() {
#     local dots="."
#     local delay=0.5
#     local i=0

#     while true; do
#         printf "%s\b" "$dots"
#         sleep "$delay"

#         # Update dots
#         case "$dots" in
#             ".") dots="..";;
#             "..") dots="...";;
#             "...") dots=".";;
#         esac
#     done
# }

# # Compilation step
# echo "Compiling BlockTest.java..."
# javac BlockTestV5.java

# # Check if compilation was successful
# if [ $? -ne 0 ]; then
#     echo "Compilation failed. Please check your Java source code for errors."
#     exit 1
# fi

# # Run the BlockTest class with arguments 1 to 7 and redirect output
# for i in {1..8}; do 
#     echo "Running BlockTest with argument $i..."
#     loading_animation &  # Start loading animation
#     loading_pid=$!

#     java BlockTestV5 $i > "blockV5TestResultForCreating_${i}Block" 2>&1

#     # Kill the loading animation
#     kill "$loading_pid" >/dev/null 2>&1
#     clear_line

#     # Check if the Java program ran successfully
#     if [ $? -ne 0 ]; then
#         echo "Error: Failed to run BlockTest with argument $i. Check blockV5TestResultForCreating_${i}Block for details."
#     else
#         echo "BlockTest with argument $i completed successfully."
#     fi
# done

# echo "All tests completed."
