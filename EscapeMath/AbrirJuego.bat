@echo off
cd /d "%~dp0"
java -cp ".;mysql-connector-j-9.7.0.jar" EscapeMathGUI
pause