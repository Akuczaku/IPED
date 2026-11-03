#requires -Version 5.1
[CmdletBinding()]
param()
$ErrorActionPreference='Stop'
$root=$PSScriptRoot
$inputDir=Join-Path $root 'DANE-TESTOWE\INPUT'
if(!(Test-Path -LiteralPath $inputDir -PathType Container)){throw 'Brak DANE-TESTOWE\INPUT. Rozpakuj cala paczke.'}
$caseDir=Join-Path $root ('SPRAWA-TESTOWA-'+(Get-Date -Format 'yyyyMMdd-HHmmss')+'-'+[guid]::NewGuid().ToString('N').Substring(0,6))
& (Join-Path $root 'Przetworz-dane.ps1') -PackageDir $root -InputPath $inputDir -CaseDir $caseDir
Write-Host "Otwieram sprawe: $caseDir"
& (Join-Path $root 'Otworz-sprawe.ps1') -CaseDir $caseDir
