import { Router } from "express";
import admin from "firebase-admin";

const DEFAULT_TEST_ALERT = "בדיקה - נסיון התראות";

const notificationRouter = Router();

notificationRouter.post("/test", async (req, res) => {
  const body = req.body;

  try {
    await admin.messaging().send({
      token: body.token,
      data: {
        should_override_silent: "yes",
        title: DEFAULT_TEST_ALERT,
        text: DEFAULT_TEST_ALERT,
        notification_id: "2",
      },
      android: { priority: "high" },
    });

    res.send("done");
  } catch (e) {
    console.error(e);
    res.status(500).send(e);
  }
});

export default notificationRouter;
