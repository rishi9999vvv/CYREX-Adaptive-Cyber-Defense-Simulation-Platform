# Download and unpack Maven to .mvn\maven so mvnw.cmd can run without the wrapper JAR.
$ErrorActionPreference = "Stop"
$projectRoot = $PSScriptRoot
$mavenDir = Join-Path $projectRoot ".mvn\maven"
$zipPath = Join-Path $env:TEMP "apache-maven-3.9.6-bin.zip"
$mavenZipUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip"

if (Test-Path (Join-Path $mavenDir "bin\mvn.cmd")) {
    Write-Host "Local Maven already exists at .mvn\maven. OK."
    exit 0
}

$mavenParent = Split-Path $mavenDir -Parent
if (!(Test-Path $mavenParent)) { New-Item -ItemType Directory -Path $mavenParent -Force | Out-Null }

Write-Host "Downloading Maven 3.9.6..."
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
Invoke-WebRequest -Uri $mavenZipUrl -OutFile $zipPath -UseBasicParsing

Write-Host "Extracting..."
Expand-Archive -Path $zipPath -DestinationPath $mavenParent -Force
$extracted = Join-Path $mavenParent "apache-maven-3.9.6"
if (!(Test-Path $extracted)) {
    Write-Error "Expected folder apache-maven-3.9.6 not found after extract."
    exit 1
}
Rename-Item -Path $extracted -NewName "maven" -Force
Remove-Item $zipPath -Force -ErrorAction SilentlyContinue

Write-Host "Done. Use: .\mvnw.cmd compile   and   .\mvnw.cmd javafx:run"
exit 0
