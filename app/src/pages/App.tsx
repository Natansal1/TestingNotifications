import React, { useEffect } from "react";
import SetSoundSettings from "./set-sound-settings/SetSoundSettings";
import { register } from "../functions/notification.function";
import "../styles/app.scss";

const App: React.FC = () => {
  useEffect(() => {
    register();
  });

  return (
    <div className="app">
      <h1 className="title">Testing Notifications</h1>
      <div className="main_container">
        <SetSoundSettings />
      </div>
    </div>
  );
};

export default App;
