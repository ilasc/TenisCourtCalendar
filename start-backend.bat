@echo off
setlocal
cd /d "%~dp0backend"

rem JDK din Android Studio (daca nu ai JAVA_HOME setat)
if not defined JAVA_HOME (
  if exist "%ProgramFiles%\Android\Android Studio\jbr\bin\java.exe" (
    set "JAVA_HOME=%ProgramFiles%\Android\Android Studio\jbr"
  ) else if exist "%LOCALAPPDATA%\Programs\Android\Android Studio\jbr\bin\java.exe" (
    set "JAVA_HOME=%LOCALAPPDATA%\Programs\Android\Android Studio\jbr"
  )
)

if not defined JAVA_HOME (
  echo EROARE: Nu am gasit Java. Instaleaza JDK 17 sau deschide proiectul in Android Studio.
  pause
  exit /b 1
)

echo JAVA_HOME=%JAVA_HOME%
echo Pornesc API Apulum Tenis pe http://localhost:8080 ...
echo Opreste cu Ctrl+C
echo.
call gradlew.bat run
pause
