export interface HilmaPushNotificationsPlugin {
    /**
     * Sets the Android settings for push notifications.
     * @param options The Android settings to be set.
     * @returns A promise that resolves when the settings are successfully set.
     */
    setSettings(options: AndroidSettings): Promise<void>;
    /**
     * Sets a specific Android setting for push notifications.
     * @param options The setting key and value to be set.
     * @returns A promise that resolves when the setting is successfully set.
     */
    setSetting<T extends keyof AndroidSettings>(options: {
        key: T;
        value: AndroidSettings[T];
    }): Promise<void>;
    /**
     * Gets the current Android settings for push notifications.
     * @returns A promise that resolves with the current Android settings.
     */
    getSettings(): Promise<AndroidSettings>;
    /**
     * Retrieves all the stored push notifications
     * @typeparam T The type of additional data associated with the notifications.
     * @returns A promise that resolves with the retrieved notifications.
     */
    getNotifications<T extends Record<string, string> = Record<string, string>>(): Promise<{
        notifications: PushMessages<T>[];
    }>;
    /**
     * Clears all stored notifications
     * @returns A promise that resolves when the clear is successfull
     */
    clearStoredNotifications(): Promise<void>;
}
export interface AndroidSettings {
    /**
     * The file name of the ringtone for notifications to play when a notificaiton arrives
     */
    ringtoneFileName: string;
    /**
     * The volume level for notifications.
     * an intiger between 1 and 100
     */
    volume: number;
    /**
     * Whether silent mode override is enabled for notifications.
     */
    overrideSilentEnabled: boolean;
}
/**
 * Interface representing push messages received.
 * @typeparam T The type of additional data associated with the message.
 */
export interface PushMessages<T extends Record<string, string>> {
    /**
     * The timestamp of the arrival of the message.
     */
    timestamp: number;
    /**
     * Additional data associated with the message.
     */
    data: T & {
        should_override_silent: "yes" | "no";
        title: string;
        text: string;
    };
}
