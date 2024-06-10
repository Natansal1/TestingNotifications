import * as express from "express";
import notificationRouter from "./routes/notificationRouter";
import admin from "firebase-admin";
import * as ServiceAccount from "./push-notification-test-a-bca6c-firebase-adminsdk-l2mmb-e5b7a10b9b.json";
import * as bodyParser from "body-parser";
import * as cors from "cors";

async function bootstrap() {
  const app = express();
  app.use(bodyParser.json());
  app.use(cors());
  app.use("/api/notifications", notificationRouter);
  app.all("*", (_r, r) => r.status(404).send("Nope"));

  admin.initializeApp({
    credential: admin.credential.cert(ServiceAccount as any),
  });

  app.listen(8080);
}

bootstrap();
