#!/bin/bash
echo "========================================"
echo "  App Cloner v2.0 - Setup Script"
echo "  Downloading VirtualApp framework..."
echo "========================================"

if [ -d "VirtualApp" ]; then
    echo "VirtualApp already exists, updating..."
    cd VirtualApp && git pull && cd ..
else
    echo "Cloning VirtualApp..."
    git clone https://github.com/nicehash/VirtualApp.git VirtualApp
fi

echo ""
echo "========================================"
echo "  Setup complete!"
echo "  Open project in Android Studio"
echo "  and build: Build - Generate APKs"
echo "========================================"
