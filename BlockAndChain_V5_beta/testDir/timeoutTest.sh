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

# Run the BlockTest class with arguments 1 to 8 and redirect output
for i in {7..10}; do 
    echo "Running BlockTest with argument $i..."
    java BlockTestV5 $i > timeoutTestResult_${i}Block 2>&1

    # Check if the Java program ran successfully
    if [ $? -ne 0 ]; then
        echo "Error: Failed to run BlockTest with argument $i. Check timeoutTestResult_${i}Block for details."
    else
        echo "BlockTest with argument $i completed successfully."
    fi
done

echo "All tests completed."