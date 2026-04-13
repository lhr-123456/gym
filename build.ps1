# Build classpath from Maven repository - comprehensive version-matched scan
$m2 = "C:\Users\86135\.m2\repository"
$jars = @()

# Acceptable version patterns for Spring Boot 2.7.x ecosystem
$accepted = @(
    # Spring Boot
    "2.7.18", "2.7.11", "2.7.10", "2.7.9", "2.7.8", "2.7.7", "2.7.6", "2.7.5",
    # Spring Framework
    "5.3.31", "5.3.30", "5.3.29", "5.3.28", "5.3.27", "5.3.26", "5.3.25",
    # Spring Security
    "5.7.11", "5.7.10", "5.7.9", "5.7.8",
    # MyBatis Plus
    "3.5.3", "3.5.10", "3.5.9", "3.5.8", "3.5.7", "3.5.6", "3.5.5", "3.5.4", "3.5.2",
    # MyBatis Spring
    "2.0.7", "2.0.6", "2.0.5", "2.0.4", "2.0.3", "2.0.2", "2.0.1",
    # MyBatis
    "3.5.10", "3.5.9", "3.5.8", "3.5.7", "3.5.6", "3.5.5", "3.5.4", "3.5.3",
    # JWT
    "0.9.1", "0.11.5",
    # Hutool
    "5.8.22", "5.8.21", "5.8.20",
    # Lombok
    "1.18.30", "1.18.28", "1.18.26",
    # MySQL
    "8.0.33", "8.0.31",
    # jsqlparser
    "4.4", "4.3", "4.2", "4.1",
    # HikariCP
    "4.0.3", "4.0.2",
    # Jackson
    "2.13.5", "2.13.4", "2.13.3",
    # Hibernate Validator
    "6.2.5", "6.2.4", "6.2.3",
    # JBoss Logging
    "3.4.3", "3.4.2",
    # Tomcat
    "9.0.83", "9.0.80", "9.0.79", "9.0.75",
    # Netty
    "4.1.101", "4.1.100", "4.1.90",
    # Lettuce
    "6.1.10", "6.1.9", "6.1.8",
    # Reactor
    "3.4.34", "3.4.33",
    # Reactive Streams
    "1.0.4", "1.0.3",
    # SLF4J
    "1.7.36", "1.7.35",
    # Logback
    "1.2.12", "1.2.11",
    # Log4j
    "2.17.2", "2.17.1",
    # Classmate
    "1.5.1", "1.5.0",
    # Jakarta
    "1.3.5", "2.1.1",
    # SnakeYAML
    "1.30", "1.29"
)

function Find-JarsInDir($path, $depth) {
    if ($depth -gt 5) { return }
    $items = Get-ChildItem -Path $path -Force -ErrorAction SilentlyContinue
    foreach ($item in $items) {
        if (-not $item.PSIsContainer) {
            if ($item.Extension -eq ".jar") {
                if ($item.Name -notmatch "-sources" -and $item.Name -notmatch "-javadoc") {
                    $versionFolder = $item.Directory.Name
                    if ($accepted -contains $versionFolder) {
                        $script:jars += $item.FullName
                    }
                }
            }
        } else {
            Find-JarsInDir -path $item.FullName -depth ($depth + 1)
        }
    }
}

# Root groups to scan - comprehensive list
$rootGroups = @(
    "org\springframework",
    "org\apache\tomcat",
    "org\mybatis",
    "com\baomidou",
    "com\mysql",
    "com\zaxxer",
    "com\github\jsqlparser",
    "io\jsonwebtoken",
    "cn\hutool",
    "org\projectlombok",
    "io\netty",
    "io\lettuce",
    "io\projectreactor",
    "org\reactivestreams",
    "com\fasterxml",
    "ch\qos",
    "org\apache\logging",
    "org\slf4j",
    "jakarta",
    "org\hibernate",
    "org\jboss"
)

foreach ($group in $rootGroups) {
    $path = Join-Path $m2 $group
    if (Test-Path $path) {
        Find-JarsInDir -path $path -depth 0
    }
}

$jars = $jars | Sort-Object -Unique
Write-Host "Found $($jars.Count) JARs"

# Build full classpath
$sep = [IO.Path]::PathSeparator
$cp = "D:\gym\target\classes" + $sep + ($jars -join $sep)

# Ensure output dir exists
if (-not (Test-Path "D:\gym\target\classes")) {
    New-Item -ItemType Directory -Path "D:\gym\target\classes" -Force | Out-Null
}

# Write classpath to file
$cp | Out-File -Encoding ASCII "D:\gym\classpath.txt"

# Compile all java files using response file
$javaFiles = Get-ChildItem -Path "D:\gym\src\main\java" -Filter "*.java" -Recurse
Write-Host "Compiling $($javaFiles.Count) Java files..."

# Write source files to response file
$javaFiles.FullName | Out-File -Encoding ASCII "D:\gym\sources.txt"

# Run javac with response file
$javacBin = "C:\Program Files\Java\jdk1.8.0_271\bin"
$javacCmd = "`"$javacBin\javac.exe`" -encoding UTF8 -d `"D:\gym\target\classes`" -classpath `@`"D:\gym\classpath.txt`" -parameters @`"D:\gym\sources.txt`""
Write-Host "Running javac..."
cmd /c $javacCmd > "D:\gym\javac_out.txt" 2>&1
$exit = $LASTEXITCODE
Write-Host "javac exit code: $exit"

if ($exit -ne 0) {
    Get-Content "D:\gym\javac_out.txt" | Select-Object -First 50
    exit 1
}

# Copy application.yml
Copy-Item "D:\gym\src\main\resources\application.yml" "D:\gym\target\classes\application.yml" -Force
Write-Host "Done! application.yml copied."

# Count class files
$classCount = (Get-ChildItem -Path "D:\gym\target\classes" -Recurse -Filter "*.class").Count
Write-Host "Generated $classCount class files."
