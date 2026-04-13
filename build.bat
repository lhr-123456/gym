@echo off
cd /d D:\gym
D:\apache-maven-3.8.6-bin\apache-maven-3.8.6\bin\mvn.cmd clean compile > D:\gym\mvn_log.txt 2>&1
echo Exit code: %ERRORLEVEL% >> D:\gym\mvn_log.txt
