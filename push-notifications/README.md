# @hilma/push-notifications

A Push-Notifications Plugin, meant to work with [@capacitor/push-notifications](https://capacitorjs.com/docs/apis/push-notifications) and [@capacitor-community/fcm](https://www.npmjs.com/package/@capacitor-community/fcm).
added optionality to override silent mode, set the push-volume from the client-side and more.

**!!!Implemented for Android only!!!** - for IOS please refer to [critical-alerts](https://developer.apple.com/documentation/usernotifications/unauthorizationoptions/criticalalert)

## Install

```bash
npm install @hilma/push-notifications
npx cap sync
```

## API

<docgen-index>

- [Manifest Config](#declare-the-permission---manifest-config)
- [Global Settings (Config)](#global-settings)
- [Project Variables](#project-variables)
- [Ringotne Files](#ringtone-files)
- [`setSettings(...)`](#setsettings)
- [`setSetting(...)`](#setsetting)
- [`getSettings()`](#getsettings)
- [`getNotifications()`](#getnotifications)
- [`clearStoredNotifications()`](#clearstorednotifications)
- [Interfaces](#interfaces)
- [Type Aliases](#type-aliases)
- [Server Api + Example](#server-api)

</docgen-index>

---

<docgen-api>

## Declare the permission - Manifest config

For the plugin to work, add this section into your `AndroidManifest.xml` file above the `application` tag
** Also - required! ** -> Add the notification-icon to your manifest meta-data (--Will crash the app if not present)

```XML

<manifest ...>
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/> <!--  This will allow the plugin to launch a custom notification to the user -->
    <uses-permission android:name="android.permission.ACCESS_NOTIFICATION_POLICY" /> <!--  This will allow the plugin to listen for incoming data messages -->
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" /> <!--  This will allow the plugin to disable silent mode -->
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" /> <!--  This will allow the plugin to listen to incoming messages even if the app is killed -->
    <uses-permission android:name="android.permission.VIBRATE" />   <!-- optional - only if your notifications require vibrations -->
    <application ...>
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_icon"
            android:resource="@path/to/icon/here" /> <!-- Adding the desired notification icon -->
        ...
    </application>
</manifest>

```

---

## Project Variables
This plugin will use the following project variables (defined in your app's variables.gradle file):

- `firebaseMessagingVersion` version of com.google.firebase:firebase-messaging (default: 23.3.1)
- `gsonVersion` version of com.google.code.gson:gson (default: 2.8.9)
- `firebaseMessagingDirectboot` version of com.google.firebase:firebase-messaging-directboot (default: 20.2.0)
- `firebaseBomVersion` version of com.google.firebase:firebase-bom (default: 31.0.0)

---

## Global Settings

You can configure the way the plugin behaves initialy in `capacitor.config.json` or `capacitor.config.ts`

| Prop                             | Type     | Description                                                                                                                                                                   |
| -------------------------------- | -------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **defaultRingtoneFileName**      | string   | @Required, The default ringtone to use if none have been set via the `HilmaPushNotifications.setSettings` call                                                                |
| **defaultVolume**                | int      | The default volume to use if none have been set via the `HilmaPushNotifications.setSettings` call, Represents an intiger between 1 and 100 @default 100 - if has not been set |
| **defaultOverrideSilentEnabled** | boolean  | The default `overrideSilentEnabled` to use if none have been set via the `HilmaPushNotifications.setSettings` call @default false - if has not been set                       |
| **lights**                       | string[] | The lights that the notifications will show (if available) Should look like: Should look like: [hex color, number onMS (as string), number offMS (as string)]                 |
| **vibrationPattern**             | string[] | The vibration pattern to play when a notificaiton arrives                                                                                                                     |

## Examples

### in `capacitor.config.json`

```json
{
  "plugins": {
    "HilmaPushNotifications": {
      "defaultRingtoneFileName": "MyCustomRingtone",
      "defaultVolume": 100,
      "defaultOverrideSilentEnabled": true,
      "lights": ["FFFFF", "300", "100"],
      "vibrationPattern": ["1000", "500", "1000", "500"]
    }
  }
}
```

### in `capacitor.config.ts`

```typescript
/// <reference types="@hilma/push-notifications" />

import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  plugins: {
    HilmaPushNotifications: {
      defaultRingtoneFileName: 'MyCustomRingtone',
      defaultVolume: 100,
      defaultOverrideSilentEnabled: true,
      lights: ['FFFFF', '300', '100'],
      vibrationPattern: ['1000', '500', '1000', '500'],
    },
  },
};

export default config;
```

---

## Ringtone files
You should save your ringtone files in `android/app/src/main/res/raw`
Your ringtone files must be of `.mp3` file type

---

### setSettings(...)

```typescript
setSettings(options: AndroidSettings) => Promise<void>
```

Sets the Android settings for push notifications.

| Param         | Type                                                        | Description                     |
| ------------- | ----------------------------------------------------------- | ------------------------------- |
| **`options`** | <code><a href="#androidsettings">AndroidSettings</a></code> | The Android settings to be set. |

---

### setSetting(...)

```typescript
setSetting<T extends keyof AndroidSettings>(options: { key: T; value: AndroidSettings[T]; }) => Promise<void>
```

Sets a specific Android setting for push notifications.

| Param         | Type                                                                               | Description                          |
| ------------- | ---------------------------------------------------------------------------------- | ------------------------------------ |
| **`options`** | <code>{ key: T; value: <a href="#androidsettings">AndroidSettings</a>[T]; }</code> | The setting key and value to be set. |

---

### getSettings()

```typescript
getSettings() => Promise<AndroidSettings>
```

Gets the current Android settings for push notifications.

**Returns:** <code>Promise&lt;<a href="#androidsettings">AndroidSettings</a>&gt;</code>

---

### getNotifications()

```typescript
getNotifications<T extends Record<string, string> = Record<string, string>>() => Promise<{ notifications: PushMessages<T>[]; }>
```

Retrieves all the stored push notifications

**Returns:** <code>Promise&lt;{ notifications: <a href="#pushmessages">PushMessages</a>&lt;T&gt;[]; }&gt;</code>

---

### clearStoredNotifications()

```typescript
clearStoredNotifications() => Promise<void>
```

Clears all stored notifications

---

### Interfaces

#### AndroidSettings

| Prop                        | Type                 | Description                                                                         |
| --------------------------- | -------------------- | ----------------------------------------------------------------------------------- |
| **`ringtoneFileName`**      | <code>string</code>  | The file name of the ringtone for notifications to play when a notificaiton arrives |
| **`volume`**                | <code>number</code>  | The volume level for notifications. an intiger between 1 and 100                    |
| **`overrideSilentEnabled`** | <code>boolean</code> | Whether silent mode override is enabled for notifications.                          |

#### PushMessages

Interface representing push messages received.

| Prop            | Type                                                                                                  | Description                                  |
| --------------- | ----------------------------------------------------------------------------------------------------- | -------------------------------------------- |
| **`timestamp`** | <code>number</code>                                                                                   | The timestamp of the arrival of the message. |
| **`data`**      | <code>T & {<br>should_override_silent: "yes" \| "no";<br>title: string;<br>text: string;<br>};</code> | Additional data associated with the message. |

### Type Aliases

#### Record

Construct a type with a set of properties K of type T

<code>{
[P in K]: T;
}</code>

</docgen-api>

<docgen-server-api>

---

## Server API

This plugin works with what's called [Data messages](https://firebase.google.com/docs/cloud-messaging/concept-options)
That means that the notification sent to the client is a silent notification, not visible by default to the client and automaticaly sent to the app itself.
The HilmaPushNotificationsPlugin intercepts this data message and fires a notification localy based on the data recieved from firebase.

| Field                        | Type                                     | Description                                                                                   |
| ---------------------------- | ---------------------------------------- | --------------------------------------------------------------------------------------------- |
| **`should_override_silent`** | <code>"yes" \| "no" </code>              | Should this message override silent mode in the client @Default "no"                          |
| **`title`**                  | <code>string</code>                      | The title of the push notification                                                            |
| **`text`**                   | <code>string</code>                      | The body (inner text) of the push notification                                                |
| **`notification_id`**        | <code>number //between 1 and 1000</code> | The ID of the push notification - if non is present than one is automaticaly generated        |
| **`[key: string]: string`**  | <code>string</code>                      | Other optional data to be retrived by the app using [`getNotifications()`](#getnotifications) |

### Example: using [firebase-admin](https://www.npmjs.com/package/firebase-admin)

```typescript
import * as admin from 'firebase-admin';

async function sendMessageToToken(token: string) {
  return await admin.messaging().send({
    token,
    data: {
      should_override_silent: 'yes', //inorder to override silent mode
      title: 'Your title',
      text: 'your Text',
      notificationId: 100, // optional
      otherRelevantData: 'What ever data you need',
    },
    android: {
      priority: 'high',
      //note that we do not add the `notification` field inorder to keep this a data message
    },
    apns: {
      //! Sending a normal notification for ios, as the plugin is for android only !
      payload: {
        aps: {
          alert: { title: 'Your title', subtitle: 'Your text' },
          badge: 1,
          sound: {
            name: 'your-ringtone.caf',
            critical: true, //inorder to send this message as a critical alert
            volume: 1, //set volume to the maximum
          },
        },
      },
    },
  });
}
```

</docgen-server-api>
