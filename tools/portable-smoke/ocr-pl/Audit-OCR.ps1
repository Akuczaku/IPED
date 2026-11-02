#requires -Version 5.1
[CmdletBinding()]
param([Parameter(Mandatory=$true)][string]$PackageDir)
$ErrorActionPreference='Stop'
$root=(Resolve-Path -LiteralPath $PackageDir).ProviderPath
$app=Join-Path $root 'iped-4.3.1'
$exe=Join-Path $app 'tools\tesseract\tesseract.exe'
$data=Join-Path $app 'tools\tesseract\tessdata'
if(!(Test-Path -LiteralPath $exe)){throw 'Tesseract executable not found'}
Write-Output '=== IPED OCR configuration ==='
Select-String -LiteralPath (Join-Path $app 'IPEDConfig.txt') -Pattern '^\s*enableOCR\s*='
Select-String -LiteralPath (Join-Path $app 'conf\OCRConfig.txt') -Pattern '^\s*(OCRLanguage|pageSegMode|pdfToImgLib)\s*='
Write-Output '=== Bundled language files ==='
Get-ChildItem -LiteralPath $data -Filter '*.traineddata' | ForEach-Object {$_.Name}
Write-Output '=== Tesseract version ==='
& $exe --version
if($LASTEXITCODE -ne 0){throw 'Tesseract version check failed'}
Write-Output '=== Polish dictionary ==='
Write-Output ('pol.traineddata present: '+(Test-Path -LiteralPath (Join-Path $data 'pol.traineddata')))
Write-Output 'Read-only audit completed. No settings changed.'
