# Termux Se Yesmin AI APK Kaise Banaye (Complete Hindi + English Guide)

Aap apne Android phone me Termux app se seedhe Yesmin AI ka installable APK compile kar sakte hain. Kisi laptop ya computer ki zaroorat nahi hai!

---

## Step 1: Termux Setup & Prerequisites Install Karein

Termux open karein aur ye command run karein:

```bash
pkg update -y
pkg install -y openjdk-17 gradle git termux-tools
```

---

## Step 2: Storage Permission De Aur Project Folder Me Jaayein

Termux ko phone ki storage access de:
```bash
termux-setup-storage
```
(Phone screen par **Allow** tap karein)

Zip file ko extract karein (agar aapne Downloads me rakha hai):
```bash
cd ~
unzip /sdcard/Download/yesmin_ai_android_project.zip -d yesmin_ai_project
cd yesmin_ai_project
```

---

## Step 3: Direct One-Touch Script Se Build Karein

Project folder me ek automated build script included hai:
```bash
chmod +x termux-build.sh gradlew
bash termux-build.sh
```

Ya fir standard Gradle command se compile karein:
```bash
./gradlew assembleDebug --no-daemon
```

---

## Step 4: APK Install Karein

Jab build successful ho jaye (`BUILD SUCCESSFUL`), APK yahan milega:
`app/build/outputs/apk/debug/app-debug.apk`

Ise seedhe install karne ke liye Termux me run karein:
```bash
termux-open app/build/outputs/apk/debug/app-debug.apk
```

Ya fir phone ke Downloads folder me copy karein taaki Files app se install kar sakein:
```bash
cp app/build/outputs/apk/debug/app-debug.apk /sdcard/Download/YesminAI.apk
```

---

## Step 5: Yesmin AI Setup

1. Phone me **Yesmin AI** open karein.
2. Microphone aur Camera permission allow karein.
3. **Settings** screen me jaakar apna **Google Gemini API Key** paste karein aur **Test Connection** tap karein.
4. "Save Key" tap karein.
5. Home screen par aakar **"Start Live Voice Call"** tap karein aur Yesmin ke saath baat karna shuru karein!
