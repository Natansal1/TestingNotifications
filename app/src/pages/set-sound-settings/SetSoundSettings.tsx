import React, { useEffect, useState } from "react";

import { Button } from "@mui/material";

import { HilmaPushNotifications } from "@hilma/push-notifications";

import { RINGTONES } from "./Ringtones.constant";
import OverrideSilentSwitch from "./components/OverrideSilentSwitch";
import SegmentWrapper from "./components/SegmentWrapper";
import SelectRingtone from "./components/SelectRingtone";
import VolumeSlider from "./components/VolumeSlider";
import AudioPlayer from "./components/AudioPlayer";

import { sendTestNotification } from "../../functions/notification.function";
import Loading from "./components/Loading";
import "./set-sound-settings.scss";

const DEFAULT_VOLUME = 100;

const SetSoundSettings: React.FC = () => {
  const [volume, setVolume] = useState<number>(DEFAULT_VOLUME);
  const [ringtoneIndex, setRingtoneIndex] = useState<number>(-1);
  const [overrideSilent, setOverrideSilent] = useState<boolean>(true);

  const [loading, setLoading] = useState(false);

  useEffect(() => {
    initValues();
  }, []);

  if (ringtoneIndex === -1) return null;

  async function saveValues() {
    await HilmaPushNotifications.setSettings({
      overrideSilentEnabled: overrideSilent,
      volume: volume,
      ringtoneFileName: RINGTONES[ringtoneIndex].name,
    });
  }

  async function initValues() {
    const { overrideSilentEnabled, ringtoneFileName, volume } =
      await HilmaPushNotifications.getSettings();

    const ringtoneIndex = RINGTONES.findIndex(
      (v) => v.name === ringtoneFileName
    );

    setRingtoneIndex(ringtoneIndex === -1 ? 0 : ringtoneIndex);
    setVolume(volume);
    setOverrideSilent(overrideSilentEnabled);
  }

  async function handleTestClick() {
    setLoading(true);
    await saveValues();
    await sendTestNotification();
    setLoading(false);
  }

  return (
    <>
      <div className="sound_settings_page">
        <div className="sound_settings">
          <SegmentWrapper size="large">
            <SelectRingtone
              value={ringtoneIndex}
              onChange={(val) => setRingtoneIndex(val)}
            />
            <AudioPlayer src={RINGTONES[ringtoneIndex].src} volume={volume} />
          </SegmentWrapper>
          <SegmentWrapper>
            <VolumeSlider onChange={(val) => setVolume(val)} value={volume} />
          </SegmentWrapper>
          <SegmentWrapper direction="row" className="override_switch_container">
            <OverrideSilentSwitch
              value={overrideSilent}
              onChange={(val) => setOverrideSilent(val)}
            />
          </SegmentWrapper>
          <SegmentWrapper className="test_notification_container">
            <Button className="send_notification_btn" onClick={handleTestClick}>
              שלח התראת ניסיון
            </Button>
          </SegmentWrapper>
        </div>
      </div>
      <Loading open={loading} />
    </>
  );
};

export default SetSoundSettings;
