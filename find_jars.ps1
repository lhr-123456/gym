# Find all JARs in Maven repository (3-level depth)
$m2 = "C:\Users\86135\.m2\repository"
$jars = @()

$patterns = @(
    "org\springframework\*",
    "org\apache\tomcat\*",
    "com\baomidou\*",
    "com\mysql\*",
    "com\zaxxer\*",
    "com\github\jsqlparser\*",
    "io\jsonwebtoken\*",
    "cn\hutool\*",
    "org\projectlombok\*",
    "io\netty\*",
    "io\lettuce\*",
    "io\projectreactor\*",
    "org\reactivestreams\*",
    "com\fasterxml\*",
    "ch\qos\*",
    "org\apache\logging\*",
    "org\slf4j\*",
    "jakarta\*",
    "org\hibernate\*",
    "org\jboss\*"
)

foreach ($pattern in $patterns) {
    $base = Join-Path $m2 $pattern
    $artifacts = Get-ChildItem -Path $base -Directory -ErrorAction SilentlyContinue
    foreach ($artifact in $artifacts) {
        # Level 2: version folders (e.g. 9.0.83, 5.3.31)
        $level2 = Get-ChildItem -Path $artifact.FullName -Directory -ErrorAction SilentlyContinue
        foreach ($l2 in $level2) {
            # Level 3: either JARs directly or more subdirs (for tomcat-style artifacts)
            $found = Get-ChildItem -Path $l2.FullName -Filter "*.jar" -ErrorAction SilentlyContinue | Where-Object { $_.Name -notmatch "-sources" -and $_.Name -notmatch "-javadoc" }
            if ($found.Count -eq 0) {
                # No JARs here, might be another level (e.g. tomcat-embed-core/9.0.83/actual-jars)
                $level3 = Get-ChildItem -Path $l2.FullName -Directory -ErrorAction SilentlyContinue
                foreach ($l3 in $level3) {
                    $found2 = Get-ChildItem -Path $l3.FullName -Filter "*.jar" -ErrorAction SilentlyContinue | Where-Object { $_.Name -notmatch "-sources" -and $_.Name -notmatch "-javadoc" }
                    foreach ($jar in $found2) { $jars += $jar.FullName }
                }
            } else {
                foreach ($jar in $found) { $jars += $jar.FullName }
            }
        }
    }
}

$jars = $jars | Sort-Object -Unique
Write-Host "Found $($jars.Count) JARs"
$jars | Out-File -Encoding ASCII "D:\gym\jars_found.txt"

# Check for tomcat
$jars | Select-String "tomcat"
