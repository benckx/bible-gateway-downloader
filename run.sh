#!/usr/bin/env bash
#
# Launcher for bible-gateway-downloader.
# Builds the fat jar on first run (or when missing), then launches it.
# Any arguments are forwarded to the application, e.g.:
#   ./run.sh                       # interactive mode
#   ./run.sh --version SG21 --book Ezek
#
set -euo pipefail

# Resolve the project directory (location of this script) so it works from anywhere.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

JAR="build/libs/bible-gateway-downloader-all.jar"

if [[ ! -f "$JAR" ]]; then
  echo "Fat jar not found, building it…" >&2
  ./gradlew --quiet shadowJar
fi

exec java -jar "$JAR" "$@"
