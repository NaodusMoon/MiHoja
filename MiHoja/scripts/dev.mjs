import { existsSync } from "node:fs";
import { join } from "node:path";
import { spawn, spawnSync } from "node:child_process";

const rootDir = process.cwd();
const frontendDir = join(rootDir, "frontend");
const isWindows = process.platform === "win32";
const npmCommand = isWindows ? "npm.cmd" : "npm";
const mvnwCommand = join(rootDir, isWindows ? "mvnw.cmd" : "mvnw");

function run(command, args, options) {
  if (isWindows) {
    return spawn("cmd.exe", ["/d", "/c", "call", command, ...args], options);
  }

  return spawn(command, args, options);
}

function runSync(command, args, options) {
  if (isWindows) {
    return spawnSync("cmd.exe", ["/d", "/c", "call", command, ...args], options);
  }

  return spawnSync(command, args, options);
}

const frontendNodeModules = join(frontendDir, "node_modules");
if (!existsSync(frontendNodeModules)) {
  console.log("[frontend] Instalando dependencias...");
  const install = runSync(npmCommand, ["install"], {
    cwd: frontendDir,
    stdio: "inherit"
  });

  if (install.status !== 0) {
    process.exit(install.status ?? 1);
  }
}

const children = [
  run(mvnwCommand, ["-Dmaven.test.skip=true", "spring-boot:run"], {
    cwd: rootDir,
    env: process.env,
    stdio: "inherit"
  }),
  run(npmCommand, ["--prefix", "frontend", "run", "dev"], {
    cwd: rootDir,
    env: {
      ...process.env,
      API_BASE_URL: process.env.API_BASE_URL ?? "http://localhost:8080",
      NEXT_PUBLIC_API_BASE_URL:
        process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080"
    },
    stdio: "inherit"
  })
];

let shuttingDown = false;

function stopChildren() {
  if (shuttingDown) {
    return;
  }
  shuttingDown = true;

  for (const child of children) {
    if (child.pid && !child.killed) {
      if (isWindows) {
        spawn("taskkill", ["/pid", String(child.pid), "/t", "/f"], {
          stdio: "ignore"
        });
      } else {
        child.kill("SIGTERM");
      }
    }
  }
}

for (const child of children) {
  child.on("exit", (code, signal) => {
    if (!shuttingDown) {
      if (code !== 0) {
        console.error(
          `Proceso detenido con codigo ${code ?? "desconocido"}${signal ? ` (${signal})` : ""}.`
        );
      }
      stopChildren();
      process.exit(code ?? 0);
    }
  });
}

process.on("SIGINT", () => {
  stopChildren();
  process.exit(0);
});

process.on("SIGTERM", () => {
  stopChildren();
  process.exit(0);
});
