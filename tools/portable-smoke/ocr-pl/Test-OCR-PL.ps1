#requires -Version 5.1
[CmdletBinding()]
param([Parameter(Mandatory=$true)][string]$PackageDir)
$ErrorActionPreference='Stop'
$root=(Resolve-Path -LiteralPath $PackageDir).ProviderPath
$app=Join-Path $root 'iped-4.3.1'
$java=Join-Path $app 'jre\bin\java.exe'
$python=Join-Path $app 'python'
$jep=Join-Path $python 'Lib\site-packages\jep'
$model=Join-Path $PSScriptRoot 'model\pol.traineddata'
$target=Join-Path $app 'tools\tesseract\tessdata\pol.traineddata'
$expected='c4476cdbc0e33d898d32345122b7be1cbf85ace15f920f06c7714756e1ef79b2'
if((Get-FileHash -LiteralPath $model).Hash.ToLower() -ne $expected){throw 'Polish model checksum mismatch'}
if((Test-Path -LiteralPath $target) -and (Get-FileHash -LiteralPath $target).Hash.ToLower() -ne $expected){throw 'Different Polish model already installed; review required'}
$config=Join-Path $app 'IPEDConfig.txt'
$ocrConfig=Join-Path $app 'conf\OCRConfig.txt'
$original=[IO.File]::ReadAllBytes($config)
$originalOcr=[IO.File]::ReadAllBytes($ocrConfig)
$utf8=New-Object Text.UTF8Encoding($false,$true)
function Replace-Setting([string]$Text,[string]$Key,[string]$Value){
 $pattern='(?m)^(\s*'+[regex]::Escape($Key)+'\s*=\s*)[^\r\n]*'
 if([regex]::Matches($Text,$pattern).Count -ne 1){throw "Expected one setting: $Key"}
 return [regex]::Replace($Text,$pattern,('${1}'+$Value))
}
$newConfig=Replace-Setting ($utf8.GetString($original)) 'enableOCR' 'true'
$newOcr=Replace-Setting ($utf8.GetString($originalOcr)) 'OCRLanguage' 'pol'
$newOcr=Replace-Setting $newOcr 'pageSegMode' '6'
$newOcr=Replace-Setting $newOcr 'skipKnownFiles' 'false'
$run=Join-Path $PSScriptRoot ('RUN-'+(Get-Date -Format 'yyyyMMdd-HHmmss')+'-'+[guid]::NewGuid().ToString('N').Substring(0,6))
New-Item -ItemType Directory -Path $run | Out-Null
[IO.File]::WriteAllBytes((Join-Path $run 'IPEDConfig.original.txt'),$original)
[IO.File]::WriteAllBytes((Join-Path $run 'OCRConfig.original.txt'),$originalOcr)
$caseDir=Join-Path $run 'CASE'
$log=Join-Path $run 'console.log'
$oldPath=$env:PATH;$oldHome=$env:PYTHONHOME;$oldPythonPath=$env:PYTHONPATH;$oldTess=$env:TESSDATA_PREFIX
$added=$false
try {
 if(!(Test-Path -LiteralPath $target)){Copy-Item -LiteralPath $model -Destination $target;$added=$true}
 [IO.File]::WriteAllText($config,$newConfig,$utf8)
 [IO.File]::WriteAllText($ocrConfig,$newOcr,$utf8)
 $env:PATH="$python;$jep;$(Join-Path $app 'jre\bin');$oldPath"
 $env:PYTHONHOME=$python;$env:PYTHONPATH=$null
 $env:TESSDATA_PREFIX=Split-Path -Parent $target
 Push-Location -LiteralPath $app
 try {
  $ErrorActionPreference='Continue'
  & $java '-Dfile.encoding=UTF-8' "-Djava.library.path=$jep" "-Diped.root=$app" '-cp' '.\iped.jar;.\lib\*' iped.app.bootstrap.Bootstrap -d (Join-Path $PSScriptRoot 'INPUT') -o $caseDir 2>&1 | Tee-Object -FilePath $log
  $code=$LASTEXITCODE
 } finally {$ErrorActionPreference='Stop';Pop-Location}
} finally {
 [IO.File]::WriteAllBytes($config,$original)
 [IO.File]::WriteAllBytes($ocrConfig,$originalOcr)
 if($added -and (Test-Path -LiteralPath $target)){Remove-Item -LiteralPath $target}
 $env:PATH=$oldPath;$env:PYTHONHOME=$oldHome;$env:PYTHONPATH=$oldPythonPath;$env:TESSDATA_PREFIX=$oldTess
 Write-Output 'Original OCR configuration and environment restored.'
}
Write-Output "Case: $caseDir"
Write-Output "Console log: $log"
if($code -ne 0){throw "IPED processing failed: $code"}
& (Join-Path $python 'python.exe') (Join-Path $PSScriptRoot 'check_ocr.py') $caseDir (Join-Path $run 'ocr-report.json')
if($LASTEXITCODE -ne 0){throw 'OCR results require review; retain report and full IPED log.'}
