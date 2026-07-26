@echo off
cd /d "%~dp0"

set "TARGET=%~dp0JugarEscapeMath.bat"
set "ICON=%~dp0icono_juego.ico"
set "WORKDIR=%~dp0"

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
"$ws = New-Object -ComObject WScript.Shell; ^
$desktop = $ws.SpecialFolders('Desktop'); ^
$shortcutPath = Join-Path $desktop 'Escape Math.lnk'; ^
$s = $ws.CreateShortcut($shortcutPath); ^
$s.TargetPath = '%TARGET%'; ^
$s.WorkingDirectory = '%WORKDIR%'; ^
$s.IconLocation = '%ICON%'; ^
$s.Save(); ^
Write-Host 'Acceso directo creado en:' $shortcutPath"

pause
`