@echo off
set JAR_PATH=%~dp0openapi-generator-cli-7.17.0.jar
set SPEC_URL=http://localhost:8080/v3/api-docs
set OUTPUT_DIR=..\frontend\api

java -jar "%JAR_PATH%" generate ^
  -i "%SPEC_URL%" ^
  -g typescript-fetch ^
  -o "%OUTPUT_DIR%" ^
  --additional-properties=useSingleRequestParameter=true,supportsES6=true
