# Extract classpath from IDE launch args and compile all sources
$launchArgs = $args[0]
$cpMatch = [regex]::Match($launchArgs, '-classpath ""([^""]+)""')
if (-not $cpMatch.Success) {
    Write-Host "ERROR: Could not extract classpath"
    exit 1
}
$classpath = $cpMatch.Groups[1].Value
$outputDir = "D:\gym\target\classes"
$srcDir = "D:\gym\src\main\java"

# Ensure output dir exists
if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
}

# Compile all java files
$javaFiles = Get-ChildItem -Path $srcDir -Filter "*.java" -Recurse
Write-Host "Compiling $($javaFiles.Count) Java files..."
$fileList = $javaFiles.FullName -join " "
$cmd = "javac -encoding UTF8 -d `"$outputDir`" -classpath `"$classpath`" -parameters $fileList"
Write-Host $cmd
cmd /c $cmd
if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed with exit code $LASTEXITCODE"
    exit $LASTEXITCODE
}
Write-Host "Compilation succeeded"

# Copy application.yml to target/classes
$ymlSrc = "D:\gym\src\main\resources\application.yml"
$ymlDest = "D:\gym\target\classes\application.yml"
if (Test-Path $ymlSrc) {
    Copy-Item $ymlSrc $ymlDest -Force
    Write-Host "Copied application.yml to target/classes"
}
