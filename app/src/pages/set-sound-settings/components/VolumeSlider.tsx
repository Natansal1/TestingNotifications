import React from "react";

import VolumeMuteRoundedIcon from "@mui/icons-material/VolumeMuteRounded";
import VolumeUpRoundedIcon from "@mui/icons-material/VolumeUpRounded";
import { Slider } from "@mui/material";

interface VolumeSliderProps {
  onChange: (value: number) => void;
  value: number;
}

const VolumeSlider: React.FC<VolumeSliderProps> = (props) => {
  const { onChange, value } = props;

  function handleChange(_event: any, value: number | number[]) {
    const val = typeof value === "number" ? value : value[0];
    //for the direction to be right-to-left
    return onChange(100 - val);
  }

  return (
    <>
      <span className="setting_label">עוצמת הצליל</span>
      <div className="slider_container">
        <VolumeMuteRoundedIcon className="volume_slider_icon" />
        <Slider
          className="volume_slider"
          onChange={handleChange}
          value={Math.abs(100 - value)}
          min={0}
          max={99}
          track="inverted"
          size="medium"
          valueLabelDisplay="auto"
          valueLabelFormat={(x) => 100 - x + "%"}
        />
        <VolumeUpRoundedIcon className="volume_slider_icon" />
      </div>
    </>
  );
};

export default VolumeSlider;
