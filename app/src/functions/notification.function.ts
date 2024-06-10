import { FCM } from "@capacitor-community/fcm";
import { PushNotifications } from "@capacitor/push-notifications";
import axios, { AxiosError } from "axios";
import { isCapacitor } from "@hilma/tools";

export async function register() {
  const res = await PushNotifications.checkPermissions();
  if (res.receive !== "granted") {
    await PushNotifications.requestPermissions();
  }
  PushNotifications.register();
}

export async function sendTestNotification() {
  const { token } = isCapacitor() ? await FCM.getToken() : { token: "test" };
  try {
    await axios.post("/api/notifications/test", {
      token,
    });
    return "success";
  } catch (e) {
    if (e instanceof AxiosError) {
      alert(formatObject(e.response?.data));
    }
  }
}

function formatObject(obj?: Record<string, string>) {
  if (!obj) return "Error on sending request";
  let result = "";
  for (const [key, value] of Object.entries(obj)) {
    result += `\n${key}: ${value}`;
  }
  return result;
}
