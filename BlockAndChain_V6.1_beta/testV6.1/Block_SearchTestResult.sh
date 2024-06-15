#!/bin/bash
#By Harris

usage() {
    echo "Usage: $0 <search_word>"
    exit 1
}

# Check if the correct number of arguments is provided
if [ "$#" -ne 1 ]; then
    usage
fi

# Assign the search word from the first argument
search_word=$1

# Run the search for each block result file
for i in {1..8}; do
    output_file="blockV5TestResultForCreateing_${i}Block"
    search_result_file="searchResult_${i}_${search_word}.txt"
    
    echo "Searching for '$search_word' in $output_file..."
    
    # Search for the word and print matching lines to terminal
    #grep "$search_word" "$output_file" > >(tee /dev/tty) 
    
    # Write search result to a file
    grep "$search_word" "$output_file" > "$search_result_file"
    
    echo "Search result written to $search_result_file"
    echo "------------------------------------------------------"
done

echo ""
echo "******************************************************"
echo "*             All tasks are completed                *"
echo "******************************************************"

