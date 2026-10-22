$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot
if (-not (Test-Path -LiteralPath '.\pom.xml')) { throw 'Ejecuta este archivo desde la raíz de SpaceHub.' }
if (-not $env:JAVA_HOME) {
    $jdkSpaceHub = Get-ChildItem -LiteralPath 'C:\Program Files\Java' -Directory -Filter 'jdk-*' | Sort-Object Name -Descending | Select-Object -First 1
    if ($jdkSpaceHub) { $env:JAVA_HOME = $jdkSpaceHub.FullName }
}
$credencialSpaceHub = Get-Credential -UserName 'root' -Message 'Credenciales de MySQL / Workbench para SpaceHub'
if ($null -eq $credencialSpaceHub) { Write-Host 'Inicio cancelado.'; return }
$env:SPACEHUB_DB_USER = $credencialSpaceHub.UserName
$env:SPACEHUB_DB_PASSWORD = $credencialSpaceHub.GetNetworkCredential().Password
$credencialSpaceHub = $null
if (-not $env:SPACEHUB_DB_URL) { $env:SPACEHUB_DB_URL = 'jdbc:mysql://localhost:3306/spacehub_db' }
Write-Host "Ejecutando el proyecto de: $PSScriptRoot"
Write-Host 'Abre http://localhost:8080 cuando termine de iniciar. Ctrl+C detiene la aplicación.'
try {
    # Java también escribe avisos en stderr; comprobamos el código de salida al terminar.
    $ErrorActionPreference = 'Continue'
    & .\mvnw.cmd spring-boot:run 2>&1 | ForEach-Object { $_.ToString() }
    $codigoSpaceHub = $LASTEXITCODE
    if ($codigoSpaceHub -ne 0) { Write-Host "La aplicación terminó con código $codigoSpaceHub. Revisa el error anterior." -ForegroundColor Red }
} finally {
    $ErrorActionPreference = 'Stop'
    Remove-Item Env:SPACEHUB_DB_PASSWORD -ErrorAction SilentlyContinue
}
