# Download Maven Wrapper JAR so mvnw.cmd works without Maven installed.
# Run again to replace a bad or corrupted JAR (e.g. "no main manifest attribute").
$ErrorActionPreference = "Stop"
$wrapperDir = Join-Path $PSScriptRoot ".mvn\wrapper"
$jarPath = Join-Path $wrapperDir "maven-wrapper.jar"
$wrapperUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
$minSize = 50000  # real JAR is ~62KB; reject HTML/error pages

if (!(Test-Path $wrapperDir)) {
    New-Item -ItemType Directory -Path $wrapperDir -Force | Out-Null
}

# Remove existing JAR so we always get a fresh copy (fixes corrupted/wrong file)
if (Test-Path $jarPath) {
    Remove-Item $jarPath -Force
    Write-Host "Removed existing maven-wrapper.jar (will re-download)."
}

Write-Host "Downloading Maven Wrapper JAR..."
try {
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    Invoke-WebRequest -Uri $wrapperUrl -OutFile $jarPath -UseBasicParsing
} catch {
    Write-Error "Download failed: $_"
    exit 1
}

$length = (Get-Item $jarPath).Length
if ($length -lt $minSize) {
    Remove-Item $jarPath -Force
    Write-Error "Downloaded file is too small ($length bytes). Likely an error page. Check internet and try again."
    exit 1
}

Write-Host "Done ($length bytes). Use: .\mvnw.cmd compile   and   .\mvnw.cmd javafx:run"
exit 0
