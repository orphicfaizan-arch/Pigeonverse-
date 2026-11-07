const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read pigeons or users", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("pigeons").get());
  await assertFails(unauthDb.collection("users").get());
});

test("Authenticated user: can create pigeon listing with valid schema", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("pigeons").doc("pigeon_1").set({
      sellerId: ALICE_UID,
      sellerName: "Alice Loft",
      title: "Champion Racing Homer",
      breed: "Racing Homer",
      price: 450,
      status: "available",
      createdAt: new Date(),
    })
  );
});

test("Cross-user: Bob cannot update Alice's pigeon listing", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("pigeons").doc("pigeon_1").set({
      sellerId: ALICE_UID,
      title: "Champion Racing Homer",
      breed: "Racing Homer",
      price: 450,
      status: "available",
      createdAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("pigeons").doc("pigeon_1").update({
      price: 200,
      updatedAt: new Date(),
    })
  );
});

test("Owner: Alice can update her pigeon listing", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("pigeons").doc("pigeon_1").set({
      sellerId: ALICE_UID,
      title: "Champion Racing Homer",
      breed: "Racing Homer",
      price: 450,
      status: "available",
      createdAt: now,
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("pigeons").doc("pigeon_1").update({
      price: 500,
      updatedAt: new Date(),
    })
  );
});
