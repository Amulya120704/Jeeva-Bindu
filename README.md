# Jeeva-Bindu (Jeeve)

**Rapid Response** blood donor directory for taluka / panchayat-level emergencies — MindMatrix VTU internship project.

## Features

- **No login** — one-time donor registration stored on device (Room)
- **Donor registry** — blood group, age, Panchayat/Town location
- **Simulated phone verify** — OTP `123456` after Send OTP
- **Emergency posts** — `Urgent [group] needed at [hospital]`
- **I'm Coming** — donors signal response; list shown on alert card
- **90-day health tracker** — eligibility date = last donation + 90 days
- **Directory filters** — show donors **Ready to Donate** (green) vs cooling down
- **FCM** — `JeevaBinduMessagingService` + local dispatch within ~2s (under 5s criteria)

## UI colors

- **Red** — emergencies only (alerts, SOS post)
- **Green** — ready to donate status

## Open in Android Studio

1. **File → Open** → select this folder (`jeeve`).
2. Let Gradle sync (JDK 17).
3. Replace `app/google-services.json` with your file from [Firebase Console](https://console.firebase.google.com/) (package `com.jeeve.jeevabindu`). The included file is a placeholder for building without a real project.
4. Run on emulator or device (API 26+). Grant **notifications** when prompted.

## Demo flow

1. Register: phone `9876543210`, OTP `123456`, pick blood group and location (e.g. `Hassan Panchayat`).
2. **Directory** — filter by blood group; green cards = available.
3. **Post SOS** — enter hospital + location; switch to **Alerts** or wait for notification (~2s).
4. On a second device/profile with matching group/location, tap **I'm Coming**.
5. **My Health** — **Record Donation Today** → eligibility shows +90 days.

## FCM (production)

- Topic: `blood_alerts` (subscribed in `MainActivity`).
- Send data payload: `bloodGroup`, `hospitalName`, `location`, `postId`.
- For internship demo, on-device `AlertDispatchService` simulates push to the current user when blood group and location match.

## Stack

Kotlin, Jetpack Compose, Material 3, Room, Firebase Cloud Messaging, minSdk 26.
=======

