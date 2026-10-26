param([Parameter(ValueFromRemainingArguments = $true)][string[]]$MavenArguments)
$ErrorActionPreference = 'Stop'
$mavenVersion = '3.10.0'
$mavenCommand = Get-Command mvn.cmd -ErrorAction SilentlyContinue
$mavenExecutable = if ($mavenCommand) { $mavenCommand.Source } else { $null }
if (-not $mavenExecutable) {
    $userDirectory = $env:USERPROFILE
    if (-not $userDirectory) { $userDirectory = [Environment]::GetFolderPath('UserProfile') }
    if (-not $userDirectory) { throw 'No se encontro el directorio de usuario para la cache de Maven.' }
    $distributionRoot = Join-Path $userDirectory ".m2/wrapper/dists/apache-maven-$mavenVersion"
    if (Test-Path -LiteralPath $distributionRoot) {
        $cachedMaven = Get-ChildItem -LiteralPath $distributionRoot -Filter mvn.cmd -File -Recurse | Select-Object -First 1
        if ($cachedMaven) { $mavenExecutable = $cachedMaven.FullName }
    }
    if (-not $mavenExecutable) {
        $downloadUrl = "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/$mavenVersion/apache-maven-$mavenVersion-bin.zip"
        $archivePath = Join-Path ([IO.Path]::GetTempPath()) "bennuMaven-$mavenVersion.zip"
        Invoke-WebRequest -Uri $downloadUrl -OutFile $archivePath -UseBasicParsing
        $checksum = ((Invoke-WebRequest -Uri "$downloadUrl.sha512" -UseBasicParsing).Content.Trim() -split '\s+')[0]
        if ((Get-FileHash -LiteralPath $archivePath -Algorithm SHA512).Hash -ne $checksum) {
            throw 'La descarga de Maven no coincide con su checksum SHA512.'
        }
        New-Item -ItemType Directory -Path $distributionRoot -Force | Out-Null
        Expand-Archive -LiteralPath $archivePath -DestinationPath $distributionRoot -Force
        $mavenExecutable = Join-Path $distributionRoot "apache-maven-$mavenVersion/bin/mvn.cmd"
    }
}
Push-Location -LiteralPath $PSScriptRoot
try {
    & $mavenExecutable @MavenArguments
    $mavenExitCode = $LASTEXITCODE
} finally {
    Pop-Location
}
exit $mavenExitCode
