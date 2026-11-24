#!/usr/bin/env bash
# Compila e roda os testes do projeto.
set -e
cd "$(dirname "$0")"
rm -rf out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -Dfile.encoding=UTF-8 -cp out wow.testes.Testes
