import React, { useEffect, useRef } from "react";

interface AudioPlayerProps {
  src: string;
  volume: number;
}

const AudioPlayer: React.FC<AudioPlayerProps> = (props) => {
  const { src, volume } = props;

  const ref = useRef<HTMLAudioElement>(null);

  useEffect(() => {
    if (ref.current) {
      ref.current.volume = volume / 100;
    }
  }, [volume]);

  return (
    <audio
      ref={ref}
      onLoad={(e) => (e.currentTarget.volume = volume / 100)}
      className="audio_player"
      src={src}
      controls
    />
  );
};

export default AudioPlayer;
