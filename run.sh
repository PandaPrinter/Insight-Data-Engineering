#!/bin/bash
if  (( $# != 1 )); then
    echo "Please pass your input file name. Eg: ./run.sh tweets.txt"
    exit 1
fi
cd src
echo "Your input file path: tweet_input/$@"
echo "---------Starting Script---------"
javac -cp .:json-simple-1.1.1.jar *.java
java -cp .:json-simple-1.1.1.jar Main "$@"
echo "---------Finished Script---------"
echo "Output has been written to: tweet_output/output.txt"