import React from "react";

import { Radio, RadioGroup, FormControlLabel } from "@mui/material";

interface OverrideSilentSwitchProps {
  value: boolean;
  onChange: (val: boolean) => void;
}

const OverrideSilentSwitch: React.FC<OverrideSilentSwitchProps> = (props) => {
  const { value, onChange } = props;

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    onChange(event.target.value === "true");
  };

  return (
    <>
      <RadioGroup
        className="silent_mode_radio_btns"
        name="override-silent"
        value={value.toString()}
        onChange={handleChange}
      >
        <FormControlLabel value="false" control={<Radio />} label="התראה ללא צליל במצב שקט" />
        <FormControlLabel value="true" control={<Radio />} label="התראה עם צליל במצב שקט" />
      </RadioGroup>
    </>
  );
};

export default OverrideSilentSwitch;
