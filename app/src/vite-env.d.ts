/// <reference types="vite/client" />

interface ImportMetaEnv {
  VITE_SERVER_DOMAIN: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
