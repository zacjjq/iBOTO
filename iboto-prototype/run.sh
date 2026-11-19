#!/bin/sh
# Compiles and starts iBOTO! (needs a JDK 8 or newer: `javac -version`)
cd "$(dirname "$0")"
mkdir -p out
javac -d out $(find src -name '*.java') && java -cp out iboto.ui.IbotoApp
