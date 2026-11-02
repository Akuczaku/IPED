#requires -Version 5.1
[CmdletBinding()]
param([Parameter(Mandatory=$true)][string]$IpedDir)
$ErrorActionPreference='Stop'
$app=(Resolve-Path -LiteralPath $IpedDir).ProviderPath
$java=Join-Path $app 'jre\bin\java.exe'
$python=Join-Path $app 'python'
$jep=Join-Path $python 'Lib\site-packages\jep'
$jar=Join-Path $app 'lib\jep-4.0.3.jar'
foreach($p in @($java,$jar,(Join-Path $python 'python39.dll'),(Join-Path $jep 'jep.dll'),(Join-Path $PSScriptRoot 'JepPortableTest.class'))){
 if(!(Test-Path -LiteralPath $p -PathType Leaf)){throw "Missing: $p"}
}
$result=Join-Path $PSScriptRoot ('wynik-jep-'+(Get-Date -Format 'yyyyMMdd-HHmmss')+'.json')
$oldPath=$env:PATH
$oldHome=$env:PYTHONHOME
$oldPythonPath=$env:PYTHONPATH
try {
 $env:PATH="$python;$jep;$(Join-Path $app 'jre\bin');$oldPath"
 $env:PYTHONHOME=$python
 $env:PYTHONPATH=$null
 & $java '-Dfile.encoding=UTF-8' "-Djava.library.path=$jep" '-cp' "$PSScriptRoot;$jar" JepPortableTest $app $result
 $code=$LASTEXITCODE
 if($code -ne 0){throw "JEP test failed, exit code: $code"}
 Write-Output "Report: $result"
} finally {
 $env:PATH=$oldPath
 $env:PYTHONHOME=$oldHome
 $env:PYTHONPATH=$oldPythonPath
}
