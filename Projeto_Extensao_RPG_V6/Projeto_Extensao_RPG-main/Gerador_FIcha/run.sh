#!/usr/bin/env bash
# Compila e executa (requer JDK 17+). No Fedora: sudo dnf install java-17-openjdk-devel
set -e
cd "$(dirname "$0")"
rm -rf out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out dnd.App
