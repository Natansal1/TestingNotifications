import React from "react";

import clsx from "clsx";

interface SegmentWrapperProps {
  children: React.ReactNode;
  size?: "small" | "large";
  direction?: "column" | "row";
  className?: string[] | string | { [key: string]: boolean };
}

const SegmentWrapper: React.FC<SegmentWrapperProps> = (props) => {
  const { children, size = "small", direction = "column", className } = props;

  return (
    <div
      className={clsx(className, "segment_wrapper", {
        segment_wrapper_large: size === "large",
        segment_wrapper_row: direction === "row",
      })}
    >
      {children}
    </div>
  );
};

export default SegmentWrapper;
