$pinfo = New-Object System.Diagnostics.ProcessStartInfo
$pinfo.FileName = "D:\apache-maven-3.8.6-bin\apache-maven-3.8.6\bin\mvn.cmd"
$pinfo.Arguments = "clean compile"
$pinfo.WorkingDirectory = "D:\gym"
$pinfo.RedirectStandardOutput = $true
$pinfo.RedirectStandardError = $true
$pinfo.UseShellExecute = $false
$pinfo.CreateNoWindow = $true
$p = New-Object System.Diagnostics.Process
$p.StartInfo = $pinfo
$p.Start() | Out-Null
$stdout = $p.StandardOutput.ReadToEnd()
$stderr = $p.StandardError.ReadToEnd()
$p.WaitForExit()
$stdout | Out-File -Encoding UTF8 "D:\gym\mvn_out.txt"
$stderr | Out-File -Encoding UTF8 "D:\gym\mvn_err.txt"
Write-Host "EXIT: $($p.ExitCode)"
