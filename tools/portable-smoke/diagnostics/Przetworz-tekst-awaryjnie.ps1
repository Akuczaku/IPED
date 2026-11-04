#requires -Version 5.1
[CmdletBinding()]
param([string]$InputPath,[string]$CaseDir,[string]$PackageDir)
$ErrorActionPreference='Stop'
if([string]::IsNullOrWhiteSpace($PackageDir)){$PackageDir=$PSScriptRoot}
$root=(Resolve-Path -LiteralPath $PackageDir).ProviderPath
$app=Join-Path $root 'iped-4.3.1'
if([string]::IsNullOrWhiteSpace($InputPath)){$InputPath=Read-Host 'Wklej sciezke pliku lub folderu danych do przetworzenia'}
$inputSource=(Resolve-Path -LiteralPath $InputPath.Trim().Trim('"')).ProviderPath
if([string]::IsNullOrWhiteSpace($CaseDir)){$CaseDir=Read-Host 'Wklej sciezke NOWEGO folderu wynikowego sprawy'}
if([string]::IsNullOrWhiteSpace($CaseDir)){throw 'Nie podano folderu wynikowego.'}
$output=[IO.Path]::GetFullPath($CaseDir.Trim().Trim('"'))
if(Test-Path -LiteralPath $output){throw 'Folder wynikowy juz istnieje. Wybierz nowy folder; nic nie zostalo nadpisane.'}
if($output.TrimEnd('\') -eq $inputSource.TrimEnd('\')){throw 'Folder wynikowy nie moze byc danymi wejsciowymi.'}
if((Test-Path -LiteralPath $inputSource -PathType Container) -and $output.StartsWith($inputSource.TrimEnd('\')+'\',[StringComparison]::OrdinalIgnoreCase)){throw 'Wybierz folder wynikowy poza danymi wejsciowymi.'}
$java=Join-Path $app 'jre\bin\java.exe'
$python=Join-Path $app 'python'
$jep=Join-Path $python 'Lib\site-packages\jep'
foreach($required in @($java,(Join-Path $app 'iped.jar'),(Join-Path $python 'python39.dll'),(Join-Path $jep 'jep.dll'))){
 if(!(Test-Path -LiteralPath $required -PathType Leaf)){throw "Brak skladnika paczki: $required"}
}
$oldPath=$env:PATH;$oldHome=$env:PYTHONHOME;$oldPythonPath=$env:PYTHONPATH
try{
 $env:PATH="$python;$jep;$(Join-Path $app 'jre\bin');$oldPath"
 $env:PYTHONHOME=$python;$env:PYTHONPATH=$null
 Push-Location -LiteralPath $app
 try{
  $ErrorActionPreference='Continue'
  & $java '-Dfile.encoding=UTF-8' "-Djava.library.path=$jep" "-Diped.root=$app" '-cp' '.\iped.jar;.\lib\*' iped.app.bootstrap.Bootstrap -d $inputSource -o $output -profile portable-text-recovery
  $code=$LASTEXITCODE
 }finally{$ErrorActionPreference='Stop';Pop-Location}
}finally{$env:PATH=$oldPath;$env:PYTHONHOME=$oldHome;$env:PYTHONPATH=$oldPythonPath}
if($code -ne 0){throw "Przetwarzanie zakonczone bledem: $code. Sprawdz dziennik IPED."}
if(!(Test-Path -LiteralPath (Join-Path $output 'iped\index') -PathType Container)){throw 'Nie znaleziono indeksu wynikowego. Sprawdz dziennik IPED.'}
Write-Output "Sprawa utworzona: $output"
Write-Output 'Otworz ja przez Otworz-sprawe.cmd. Sprawdz rowniez koncowe statystyki w dzienniku IPED.'
