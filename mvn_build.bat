@echo off
chcp 65001 >nul
cd /d D:\gym
call D:\apache-maven-3.8.6-bin\apache-maven-3.8.6\bin\mvn.cmd clean compile
