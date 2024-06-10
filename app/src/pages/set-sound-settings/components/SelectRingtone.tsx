import React from "react";

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { MenuItem, Select } from "@mui/material";
import { RINGTONES } from "../Ringtones.constant";

interface SelectRingtoneProps {
  value: number;
  onChange: (value: number) => void;
}

const SelectRingtone: React.FC<SelectRingtoneProps> = (props) => {
  const { onChange, value } = props;

  return (
    <>
      <span className="setting_label">בחירת צליל</span>
      <Select
        className="sound_select"
        IconComponent={ExpandMoreIcon}
        value={value.toString()}
        onChange={(e) => onChange(parseInt(e.target.value))}
      >
        {RINGTONES.map((item, index) => (
          <MenuItem key={"item" + index} value={index}>
            {item.text}
          </MenuItem>
        ))}
      </Select>
    </>
  );
};

export default SelectRingtone;
