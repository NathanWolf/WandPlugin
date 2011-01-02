#!/bin/bash
cd ~/Documents/Code/Eclipse/minecraft/Wand/bin
rm *.jar
jar cvf Wand.jar *.class
cd ..
git add .
