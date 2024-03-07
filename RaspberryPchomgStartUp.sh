#!/bin/bash

# Redirect stderr to error log, and stdout to both terminal and log file
exec 2>> /home/liam1403/Desktop/error_log.txt
exec 1> >(tee /home/liam1403/Desktop/script_output.log)

echo "Script started at $(date)"

# Ensure all necessary environment variables are set
export PATH=/home/liam1403/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/local/games:/usr/games
export DISPLAY=:1

echo "Checking for connected displays using xrandr..."

# Use xrandr to check display connection status
output=$(xrandr --query)

echo "xrandr output:" >> /home/liam1403/Desktop/script_output.log
echo "$output" >> /home/liam1403/Desktop/script_output.log

# Check if a display is connected before proceeding
if echo "$output" | grep -q " connected "; then
  echo "Display connected. Exiting script to prevent proceeding."
  exit 0
else
  echo "No display connected. Proceeding with script."
fi

# Set up a virtual display with Xvfb
# Note: Adjust the screen size (1280x1024x24) as needed
Xvfb :1 -screen 0 1280x1024x24 &
export DISPLAY=:1

# Change directory to where your program is located
cd /home/liam1403/Desktop/project/server_webcam_processing

python3 CHOMG.py > errors.txt
