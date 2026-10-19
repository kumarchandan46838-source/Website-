#!/data/data/com.termux/files/usr/bin/bash
# ==============================================================================
# YESMIN AI — TERMUX ONE-TOUCH BUILD SCRIPT
# Builds the debug APK directly on your Android phone using Termux!
# ==============================================================================

set -e

CYAN='\033[0;36m'
PURPLE='\033[0;35m'
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${PURPLE}============================================================${NC}"
echo -e "${CYAN}           YESMIN AI — TERMUX APK BUILDER                  ${NC}"
echo -e "${PURPLE}============================================================${NC}"

# Step 1: Check and Install Prerequisites
echo -e "\n${CYAN}[1/5] Checking Termux Build Environment...${NC}"

pkg update -y || true

if ! command -v java >/dev/null 2>&1; then
    echo -e "${PURPLE}Installing OpenJDK 17...${NC}"
    pkg install -y openjdk-17
fi

if ! command -v gradle >/dev/null 2>&1; then
    echo -e "${PURPLE}Installing Gradle...${NC}"
    pkg install -y gradle
fi

# Step 2: Set JAVA_HOME
echo -e "\n${CYAN}[2/5] Configuring Java and Environment Paths...${NC}"
export JAVA_HOME="/data/data/com.termux/files/usr/lib/jvm/java-17-openjdk"
if [ ! -d "$JAVA_HOME" ]; then
    export JAVA_HOME="$(dirname $(dirname $(readlink -f $(which java))))"
fi
echo -e "${GREEN}Using Java at: $JAVA_HOME${NC}"

# Step 3: Make Gradle wrapper executable
echo -e "\n${CYAN}[3/5] Setting file permissions...${NC}"
chmod +x gradlew || true

# Step 4: Build APK
echo -e "\n${CYAN}[4/5] Compiling Yesmin AI (assembleDebug)...${NC}"
echo -e "This might take a couple of minutes on first build..."

if [ -f "./gradlew" ]; then
    ./gradlew assembleDebug --no-daemon
else
    gradle assembleDebug --no-daemon
fi

# Step 5: Locate APK and prompt installation
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK_PATH" ]; then
    echo -e "\n${PURPLE}============================================================${NC}"
    echo -e "${GREEN}🎉 BUILD SUCCESSFUL!${NC}"
    echo -e "${CYAN}APK Location: $(pwd)/$APK_PATH${NC}"
    echo -e "${PURPLE}============================================================${NC}"
    echo -e "\nTo install on your phone right now, run:"
    echo -e "${GREEN}termux-open $APK_PATH${NC}"
    echo -e "or copy to Downloads:"
    echo -e "${GREEN}cp $APK_PATH /sdcard/Download/YesminAI.apk${NC}"
else
    echo -e "\n${RED}Build finished, but APK was not found at standard path.${NC}"
    echo -e "Searching for generated APK files..."
    find . -name "*.apk"
fi
