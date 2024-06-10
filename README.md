# Testing Push-Notifications

This README will guide you through setting up and running the project on your local machine.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Syncing Java Files with Android Studio](#syncing-java-files-with-android-studio)
- [Running the App](#running-the-app)
- [Editing Java files](#editing-java-files)

## Prerequisites

Make sure you have the following installed:

- Node.js and npm
- Android Studio

## Quick Start

Follow these steps to get the project up and running:

1. **Clone the repository:**

   ```sh
    git clone https://github.com/Natansal1/TestingNotifications.git
    cd TestingNotifications
   ```

2. **Install dependencies:**

   - Go into the `app` directory and run:
     ```sh
     cd app
     npm install
     ```
   - Go to the `push-notifications` directory and run:
     ```sh
     cd ../push-notifications
     npm install
     ```
   - Go to the `server` directory and run:
     ```sh
     cd ../server
     npm install
     ```

3. **Start the server:**

   ```sh
   cd ../server
   npm start
   ```

4. **Configure the app:**

   - Go to the `app` directory and open `.env.production` file.
   - Change the `IP` to your PC's IP address to allow the app to communicate with your server.
     Make sure that the android device you are using to run this app is connected to the same WIFI as your PC

5. **Sync the app with Android Studio:**
   ```sh
   cd ../app
   npm run sync
   ```

## Syncing Java Files with Android Studio

In order to sync between Android Studio and the changed Java files, run the following command in the app directory:

```sh
npm run sync
```

## Running the App

1. Open Android Studio.
2. Open your project in Android Studio.
3. Run the app on a connected device.

Now, your app should be up and running, and you can start developing and testing all features!

## Editing JAVA Files

Go to the `push-notifications` directory
Then `/android/src/main/java/com/hilma/plugin`
and there you'll find all the Java files in use. You may edit them as you with
**Don't forget - inorder to sync the changes with Android-Studio please run `npm run sync`**

## Contributing

Feel free to submit issues or pull requests. We appreciate your help in making this app better!

---
