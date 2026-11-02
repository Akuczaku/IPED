#requires -Version 5.1
[CmdletBinding()]
param([Parameter(Mandatory=$true)][ValidateSet('On','Off')][string]$State,[string]$PackageDir)
$ErrorActionPreference='Stop'
if([string]::IsNullOrWhiteSpace($PackageDir)){$PackageDir=$PSScriptRoot}
$root=(Resolve-Path -LiteralPath $PackageDir).ProviderPath
$app=Join-Path $root 'iped-4.3.1'
$model=Join-Path $app 'tools\tesseract\tessdata\pol.traineddata'
$config=Join-Path $app 'IPEDConfig.txt'
if($State -eq 'On'){
 if((Get-FileHash -LiteralPath $model -Algorithm SHA256).Hash.ToLower() -ne 'c4476cdbc0e33d898d32345122b7be1cbf85ace15f920f06c7714756e1ef79b2'){throw 'Polish dictionary checksum mismatch.'}
 if(!(Select-String -LiteralPath (Join-Path $app 'conf\OCRConfig.txt') -Pattern '^\s*OCRLanguage\s*=\s*pol\s*$' -Quiet)){throw 'OCRLanguage must be pol; no settings changed.'}
}
$utf8=New-Object Text.UTF8Encoding($false,$true)
$bytes=[IO.File]::ReadAllBytes($config)
$text=$utf8.GetString($bytes)
$pattern='(?m)^(\s*enableOCR\s*=\s*)(true|false)(\s*)$'
if([regex]::Matches($text,$pattern).Count -ne 1){throw 'Expected exactly one enableOCR setting.'}
$value='false';if($State -eq 'On'){$value='true'}
$newText=[regex]::Replace($text,$pattern,('${1}'+$value+'${3}'))
if($newText -eq $text){Write-Output "OCR already $State";return}
$backup=$config+'.before-ocr-'+[guid]::NewGuid().ToString('N')+'.bak'
$temp=$config+'.ocr-'+[guid]::NewGuid().ToString('N')+'.tmp'
try{
 [IO.File]::WriteAllText($temp,$newText,$utf8)
 [IO.File]::Replace($temp,$config,$backup)
}finally{if(Test-Path -LiteralPath $temp){Remove-Item -LiteralPath $temp}}
Write-Output "OCR $State. Original configuration: $backup"
Write-Output 'Restart IPED. This affects future processing; existing indexes are unchanged.'
