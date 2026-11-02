#requires -Version 5.1
[CmdletBinding()]
param([string]$CaseDir)
$ErrorActionPreference='Stop'
if([string]::IsNullOrWhiteSpace($CaseDir)){$CaseDir=Read-Host 'Wklej sciezke istniejacej sprawy IPED (folder zawierajacy iped)'}
$CaseDir=(Resolve-Path -LiteralPath $CaseDir.Trim('"')).ProviderPath
if(!(Test-Path -LiteralPath (Join-Path $CaseDir 'iped\index') -PathType Container)){throw 'Brak iped\index. Wybierz folder sprawy, nie folder programu.'}
$app=Join-Path $PSScriptRoot 'iped-4.3.1'
$python=Join-Path $app 'python'
$jep=Join-Path $python 'Lib\site-packages\jep'
foreach($required in @((Join-Path $python 'python39.dll'),(Join-Path $jep 'jep.dll'))){
 if(!(Test-Path -LiteralPath $required -PathType Leaf)){throw "Missing bundled runtime: $required"}
}
$oldPath=$env:PATH
$oldHome=$env:PYTHONHOME
$oldPythonPath=$env:PYTHONPATH
$list=Join-Path $env:TEMP ('IPED-case-list-'+[guid]::NewGuid().ToString('N')+'.txt')
[IO.File]::WriteAllText($list,$CaseDir+[Environment]::NewLine,(New-Object Text.UTF8Encoding($false)))
try {
 $env:PATH="$python;$jep;$(Join-Path $app 'jre\bin');$oldPath"
 $env:PYTHONHOME=$python
 $env:PYTHONPATH=$null
 Push-Location -LiteralPath $app
 try {
  & '.\jre\bin\java.exe' '-Dfile.encoding=UTF-8' "-Djava.library.path=$jep" "-Diped.root=$app" -cp '.\iped.jar;.\lib\*' iped.app.bootstrap.BootstrapUI -multicases $list
  if($LASTEXITCODE -ne 0){throw "IPED exit code: $LASTEXITCODE"}
 } finally {Pop-Location}
} finally {
 $env:PATH=$oldPath
 $env:PYTHONHOME=$oldHome
 $env:PYTHONPATH=$oldPythonPath
 if(Test-Path -LiteralPath $list){Remove-Item -LiteralPath $list}
}

