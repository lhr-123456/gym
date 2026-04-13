$m2 = "C:\Users\86135\.m2\repository"
$accepted = @("9.0.83")
$base = Join-Path $m2 "org\apache\tomcat\*"
$artifacts = Get-ChildItem -Path $base -Directory
foreach ($artifact in $artifacts) {
    Write-Host "Artifact: $($artifact.Name)"
    $level2 = Get-ChildItem -Path $artifact.FullName -Directory
    foreach ($l2 in $level2) {
        $version = $l2.Name
        Write-Host "  Version: [$version]"
        $matches = $accepted -contains $version
        Write-Host "  Matches: $matches"
    }
}
