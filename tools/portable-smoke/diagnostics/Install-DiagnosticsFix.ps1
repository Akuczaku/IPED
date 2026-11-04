#requires -Version 5.1
[CmdletBinding()]
param([Parameter(Mandatory=$true)][string]$PackageDir)
$ErrorActionPreference='Stop'
$root=(Resolve-Path -LiteralPath $PackageDir).ProviderPath.TrimEnd('\')
if(Test-Path -LiteralPath (Join-Path $root 'iped.jar')){
 if((Split-Path -Leaf $root) -ne 'iped-4.3.1'){throw 'Wskaz folder paczki zawierajacy iped-4.3.1.'}
 $root=Split-Path -Parent $root
}
if(!(Test-Path -LiteralPath (Join-Path $root 'iped-4.3.1\iped.jar'))){throw 'Nie znaleziono programu. Podaj folder paczki TEST2.'}
$manifest=Get-Content -LiteralPath (Join-Path $PSScriptRoot 'patch-manifest.json') -Raw -Encoding UTF8|ConvertFrom-Json
$info=Get-Content -LiteralPath (Join-Path $PSScriptRoot 'patch-info.json') -Raw -Encoding UTF8|ConvertFrom-Json
$pending=@()
foreach($entry in $manifest){
 $rel=$entry.path.Replace('/',[IO.Path]::DirectorySeparatorChar)
 $target=[IO.Path]::GetFullPath((Join-Path $root $rel))
 if(!$target.StartsWith($root+'\',[StringComparison]::OrdinalIgnoreCase)){throw 'Nieprawidlowa sciezka manifestu.'}
 $payload=Join-Path (Join-Path $PSScriptRoot 'files') $rel
 if((Get-FileHash -LiteralPath $payload -Algorithm SHA256).Hash.ToLower() -ne $entry.patched_sha256){throw "Uszkodzony plik poprawki: $rel"}
 $actual=$null
 if(Test-Path -LiteralPath $target){
  if(!(Test-Path -LiteralPath $target -PathType Leaf)){throw "To nie jest plik: $target"}
  $actual=(Get-FileHash -LiteralPath $target -Algorithm SHA256).Hash.ToLower()
 }
 if($actual -eq $entry.patched_sha256){continue}
 if($actual -ne $entry.original_sha256){throw "Inna wersja lub lokalne zmiany w $rel. Nic nie nadpisano."}
 if($actual){$stream=[IO.File]::Open($target,[IO.FileMode]::Open,[IO.FileAccess]::Read,[IO.FileShare]::None);$stream.Dispose()}
 $pending+=@{entry=$entry;target=$target;payload=$payload;rel=$rel;existed=($null -ne $actual)}
}
if($pending.Count -eq 0){Write-Host 'Poprawka jest juz zainstalowana.';exit 0}
$hashList=Join-Path $root 'SHA256SUMS.txt'
if((Get-FileHash -LiteralPath $hashList -Algorithm SHA256).Hash.ToLower() -ne $info.original_manifest_sha256){throw 'Inny manifest paczki. Instalacja przerwana przed zmianami.'}
$backup=Join-Path $root ('PATCH-BACKUPS\diagnostics-'+(Get-Date -Format 'yyyyMMdd-HHmmss')+'-'+[guid]::NewGuid().ToString('N').Substring(0,6))
New-Item -ItemType Directory -Path $backup -Force|Out-Null
Copy-Item -LiteralPath $hashList -Destination (Join-Path $backup 'SHA256SUMS.txt')
# All compatibility and payload checks above precede mutations.
foreach($item in $pending){if($item.existed){$b=Join-Path $backup $item.rel;New-Item -ItemType Directory -Path (Split-Path -Parent $b) -Force|Out-Null;Copy-Item -LiteralPath $item.target -Destination $b}}
$changed=@()
try{
 foreach($item in $pending){
  New-Item -ItemType Directory -Path (Split-Path -Parent $item.target) -Force|Out-Null
  $changed+=,$item
  Copy-Item -LiteralPath $item.payload -Destination $item.target -Force
  if((Get-FileHash -LiteralPath $item.target -Algorithm SHA256).Hash.ToLower() -ne $item.entry.patched_sha256){throw "Blad zapisu: $($item.rel)"}
 }
 $hashes=@{}
 foreach($line in [IO.File]::ReadAllLines($hashList)){if($line){$hashes[$line.Substring(66)]=$line.Substring(0,64)}}
 foreach($entry in $manifest){$hashes[$entry.path]=$entry.patched_sha256}
 $lines=@($hashes.Keys|Sort-Object|ForEach-Object {$hashes[$_]+'  '+$_})
 [IO.File]::WriteAllLines($hashList,$lines,(New-Object Text.UTF8Encoding($false)))
}catch{
 foreach($item in $changed){
  if($item.existed){Copy-Item -LiteralPath (Join-Path $backup $item.rel) -Destination $item.target -Force}
  elseif(Test-Path -LiteralPath $item.target -PathType Leaf){Remove-Item -LiteralPath $item.target -Force}
 }
 Copy-Item -LiteralPath (Join-Path $backup 'SHA256SUMS.txt') -Destination $hashList -Force
 throw
}
Write-Host "Zainstalowano poprawke. Kopia poprzednich plikow: $backup"
Write-Host 'W profilu forensic porownywanie z zewnetrzna baza skrotow jest wylaczone; obliczanie skrotow pozostaje aktywne.'
Write-Host 'OCR i tekst ponawiaj tylko dla problematycznych plikow, do NOWEJ sprawy. Szczegoly: PRZECZYTAJ.txt poprawki.'
