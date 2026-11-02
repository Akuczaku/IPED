#requires -Version 5.1
[CmdletBinding()]
param([Parameter(Mandatory=$true)][string]$ModelDir)
$ErrorActionPreference='Stop'
$sha='c4476cdbc0e33d898d32345122b7be1cbf85ace15f920f06c7714756e1ef79b2'
$revision='87416418657359cb625c412a48b6e1d6d41c29bd'
New-Item -ItemType Directory -Path $ModelDir -Force | Out-Null
$dir=(Resolve-Path -LiteralPath $ModelDir).ProviderPath
$target=Join-Path $dir 'pol.traineddata'
if(Test-Path -LiteralPath $target){
 if((Get-FileHash -LiteralPath $target).Hash.ToLower() -eq $sha){Write-Output 'Verified model already present.';return}
 throw 'Different model exists; no overwrite.'
}
$temp=Join-Path $dir ('pol-'+[guid]::NewGuid().ToString('N')+'.tmp')
$oldProtocol=[Net.ServicePointManager]::SecurityProtocol
try {
 [Net.ServicePointManager]::SecurityProtocol=$oldProtocol -bor [Net.SecurityProtocolType]::Tls12
 Invoke-WebRequest -UseBasicParsing -Uri "https://raw.githubusercontent.com/tesseract-ocr/tessdata_fast/$revision/pol.traineddata" -OutFile $temp
 if((Get-FileHash -LiteralPath $temp).Hash.ToLower() -ne $sha){throw 'Model checksum mismatch.'}
 Move-Item -LiteralPath $temp -Destination $target
} finally {
 [Net.ServicePointManager]::SecurityProtocol=$oldProtocol
 if(Test-Path -LiteralPath $temp){Remove-Item -LiteralPath $temp}
}
Write-Output "Verified Polish OCR model: $target"
