#!/bin/bash
#By Harris

# Compilation step
echo "Compiling BlockTestV5.java..."
javac BlockTestV5.java

# Check if compilation was successful
if [ $? -ne 0 ]; then
    echo "Compilation failed. Please check your Java source code for errors."
    exit 1
fi

# Run the BlockTestV5 class with arguments 1 to 8 and redirect output
for i in {1..8}; do 
    echo "Running BlockTestV5 with argument $i..."
    java BlockTestV5 $i > blockV5TestResultForCreateing_${i}Block 2>&1

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
