import React from "react";
import { createRoot } from "react-dom/client";
import axios from "axios";
import { isCapacitor } from "@hilma/tools";

import App from "./pages/App";
import "./styles/style.scss";

console.log(import.meta.env);

if (!isCapacitor()) {
  import.meta.env.VITE_SERVER_DOMAIN = "http://localhost:8080";
} else {
  axios.interceptors.request.use((req) => {
    req.baseURL = import.meta.env.VITE_SERVER_DOMAIN;
    if (req.url && req.url.startsWith("http://localhost")) {
      req.url = req.url.slice("http://localhost".length);
    }
    return req;
  });
}

axios.interceptors.request.use((req) => {
  if (req.url && req.url.startsWith("/api")) {
    req.baseURL = import.meta.env.VITE_SERVER_DOMAIN;
  }
  return req;
});

axios.defaults.baseURL = import.meta.env.VITE_SERVER_DOMAIN;

const root = createRoot(document.getElementById("root")!);

root.render(<App />);
