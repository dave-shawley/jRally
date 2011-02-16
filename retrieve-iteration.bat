@ECHO OFF
SETLOCAL EnableExtensions EnableDelayedExpansion
FOR %%I IN (%0) DO SET R=%%~dpI
PUSHD %R%
SET C=build\classes;configs\main
FOR %%I IN (ext-lib\*.jar local-lib\*.jar) DO SET C=!C!;%%I
java -cp "%C%" standup.application.RetrieveStoriesForIteration %*
POPD
ENDLOCAL
