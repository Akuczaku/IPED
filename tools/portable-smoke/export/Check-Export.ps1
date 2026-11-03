#requires -Version 5.1
[CmdletBinding()]
param([Parameter(Mandatory=$true)][string]$ExportDir)
$ErrorActionPreference='Stop'
$dir=(Resolve-Path -LiteralPath $ExportDir).ProviderPath
$expected=@(
 @{name='OCR-PL-obraz.png';sha256='f743d855253bf56655053b4b68c495c649a492ef5589b2bfcecc35f88f5acb18'},
 @{name='OCR-PL-skan.pdf';sha256='7568157ad2f090b3d6e64f542edc46a4197494536563f016f73f902f785d216f'}
)
$files=@(Get-ChildItem -LiteralPath $dir -Recurse -File)
if(!$files.Count){throw 'Export folder is empty.'}
$hashed=@($files | ForEach-Object {[pscustomobject]@{path=$_.FullName;sha256=(Get-FileHash -LiteralPath $_.FullName -Algorithm SHA256).Hash.ToLower()}})
$results=@(foreach($item in $expected){
 $matches=@($hashed | Where-Object {$_.sha256 -eq $item.sha256})
 [pscustomobject]@{original=$item.name;expected_sha256=$item.sha256;found=($matches.Count -gt 0);matching_paths=@($matches | ForEach-Object {$_.path})}
})
$passed=@($results | Where-Object {!$_.found}).Count -eq 0
$status='REVIEW';if($passed){$status='EXPORT_BYTES_PASS'}
$report=Join-Path $PSScriptRoot ('export-check-'+(Get-Date -Format 'yyyyMMdd-HHmmss')+'.json')
[ordered]@{status=$status;export_dir=$dir;files_checked=$files.Count;results=$results;scope='SHA-256 comparison of two synthetic original files; not report rendering or bookmark persistence'} | ConvertTo-Json -Depth 7 | Set-Content -LiteralPath $report -Encoding UTF8
$results | ForEach-Object {Write-Output ($_.original+': '+$_.found)}
Write-Output "Status: $status"
Write-Output "Report: $report"
if(!$passed){throw 'An original file was not found byte-for-byte. Keep the export for review.'}
