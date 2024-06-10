import React from "react";
import { CircularProgress } from "@mui/material";
import "./loading.scss";

interface LoadingProps {
  open?: boolean;
}

const Loading: React.FC<LoadingProps> = (props) => {
  const { open } = props;

  if (!open) return null;

  return (
    <div className="loading">
      <CircularProgress size={100} variant="indeterminate" />
    </div>
  );
};

export default Loading;
