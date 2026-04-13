$base = Join-Path 'C:\Users\86135\.m2\repository' 'org\apache\tomcat\*'
Write-Host "Base: $base"
$artifacts = Get-ChildItem -Path $base -Directory -ErrorAction SilentlyContinue
Write-Host "Artifacts: $($artifacts.Count)"
foreach ($artifact in $artifacts) {
    Write-Host "Artifact: $($artifact.FullName)"
    $versions = Get-ChildItem -Path $artifact.FullName -Directory -ErrorAction SilentlyContinue
    Write-Host "Versions: $($versions.Count)"
    foreach ($version in $versions) {
        Write-Host "  Version: $($version.FullName)"
        $jars2 = Get-ChildItem -Path $version.FullName -Filter "*.jar" -ErrorAction SilentlyContinue | Where-Object { $_.Name -notmatch "-sources" -and $_.Name -notmatch "-javadoc" }
        Write-Host "  Jars: $($jars2.Count)"
        foreach ($jar in $jars2) {
            Write-Host "    $($jar.FullName)"
        }
    }
}
