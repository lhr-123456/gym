$groups = @(
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
$m2 = "C:\Users\86135\.m2\repository"
$versions = @{}
foreach ($group in $groups) {
    $base = Join-Path $m2 $group
    if (Test-Path $base) {
        Get-ChildItem $base -Directory | ForEach-Object {
            Get-ChildItem $_.FullName -Directory | ForEach-Object {
                $ver = $_.Name
                if (-not $versions.ContainsKey($ver)) {
                    $versions[$ver] = 1
                } else {
                    $versions[$ver] = $versions[$ver] + 1
                }
            }
        }
    }
}
$versions.GetEnumerator() | Sort-Object Name | ForEach-Object { Write-Host "$($_.Key): $($_.Value)" }
