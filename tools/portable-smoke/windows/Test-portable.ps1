#requires -Version 5.1
[CmdletBinding()]
param([string]$PackageDir)
if([string]::IsNullOrWhiteSpace($PackageDir)){$PackageDir=$PSScriptRoot}
$ErrorActionPreference='Stop'
$root=(Resolve-Path -LiteralPath $PackageDir).ProviderPath
$app=Join-Path $root 'iped-4.3.1'
$inputDir=Join-Path $root 'test-data'
$python=Join-Path $app 'python'
$jep=Join-Path $python 'Lib\site-packages\jep'
$java=Join-Path $app 'jre\bin\java.exe'
foreach($file in @($java,(Join-Path $app 'iped.jar'),(Join-Path $python 'python39.dll'),(Join-Path $jep 'jep.dll'))){
 if(!(Test-Path -LiteralPath $file -PathType Leaf)){throw "Missing: $file"}
}
if(!(Test-Path -LiteralPath $inputDir -PathType Container)){throw 'Missing test-data'}
$stamp=(Get-Date -Format 'yyyyMMdd-HHmmss')+'-'+[guid]::NewGuid().ToString('N').Substring(0,6)
$caseDir=Join-Path $root ('processing-jep-case-'+$stamp)
$log=Join-Path $root ('processing-jep-'+$stamp+'.log')
$report=Join-Path $root ('processing-jep-'+$stamp+'.json')
if(Test-Path -LiteralPath $caseDir){throw 'Case already exists'}
$oldPath=$env:PATH;$oldHome=$env:PYTHONHOME;$oldPythonPath=$env:PYTHONPATH
try {
 $env:PATH="$python;$jep;$(Join-Path $app 'jre\bin');$oldPath"
 $env:PYTHONHOME=$python
 $env:PYTHONPATH=$null
 Push-Location -LiteralPath $app
 try {
  # Windows PowerShell 5.1 may classify native stderr as ErrorRecord.
  $ErrorActionPreference='Continue'
  & $java '-Dfile.encoding=UTF-8' "-Djava.library.path=$jep" "-Diped.root=$app" '-cp' '.\iped.jar;.\lib\*' iped.app.bootstrap.Bootstrap -d $inputDir -o $caseDir 2>&1 | Tee-Object -FilePath $log
  $code=$LASTEXITCODE
 } finally {$ErrorActionPreference='Stop';Pop-Location}
} finally {$env:PATH=$oldPath;$env:PYTHONHOME=$oldHome;$env:PYTHONPATH=$oldPythonPath}
$logText=Get-Content -LiteralPath $log -Raw
$jepError=[bool]($logText -match '(?i)JepNotFound|JEP.{0,30}not found|Nie znaleziono JEP|UnsatisfiedLinkError|No module named .jep')
$numpyMissing=[bool]($logText -match 'No module named .numpy')
$indexPresent=Test-Path -LiteralPath (Join-Path $caseDir 'iped\index') -PathType Container
$status='REVIEW'
if($code -eq 0 -and $indexPresent -and !$jepError){$status='PROCESSING_COMPLETED_NO_JEP_LOAD_ERROR'}
[ordered]@{status=$status;exit_code=$code;case_dir=$caseDir;index_present=$indexPresent;jep_load_error_in_console=$jepError;numpy_missing_in_console=$numpyMissing;console_log=$log;scope='Synthetic processing startup check; not proof of all Python parsers/AI/OCR/GPU'} | ConvertTo-Json | Set-Content -LiteralPath $report -Encoding UTF8
Write-Output "Report: $report"
Write-Output "Status: $status"
if($code -ne 0 -or !$indexPresent -or $jepError){throw 'Processing check requires review. Preserve log and JSON.'}

& (Join-Path $root 'Otworz-sprawe.ps1') -CaseDir $caseDir


