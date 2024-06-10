declare module '@capacitor/cli' {
    interface PluginsConfig {
        HilmaPushNotifications: {
            /**
             * The default ringtone to use if none have been set via the `HilmaPushNotifications.setSettings` call
             */
            defaultRingtoneFileName: string;
            /**
             * The default volume to use if none have been set via the `HilmaPushNotifications.setSettings` call
             * Represents an intiger between 1 and 100
             * @default 100 - if has not been set
             */
            defaultVolume?: number;
            /**
             * The default `overrideSilentEnabled` to use if none have been set via the `HilmaPushNotifications.setSettings` call
             * @default false - if has not been set
             */
            defaultOverrideSilentEnabled?: boolean;
            /**
             * The lights that the notifications will show (if available)
             * Should look like: [hex color, number onMS (as string), number offMS (as string)]
             */
            lights?: string[];
            /**
             * The vibration pattern to play when a notificaiton arrives
             */
            vibrationPattern?: number[];
        };
    }
}
