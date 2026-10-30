$ErrorActionPreference = "Stop"

$release = Join-Path $PSScriptRoot "target\release\iped-4.3.1"
$exe = Join-Path $release "iped.exe"

if (-not (Test-Path -LiteralPath $exe)) {
    throw "Brak iped.exe: $exe"
}

$ipedArgs = @($args)

if ($ipedArgs.Count -eq 0) {
    $ipedArgs = @("-h")
}

$oldOptions = $env:JAVA_TOOL_OPTIONS

try {
    if ($oldOptions -match '(?:^|\s)-Dfile\.encoding=') {
        $env:JAVA_TOOL_OPTIONS = $oldOptions
    }
    else {
        $env:JAVA_TOOL_OPTIONS = (
            "$oldOptions -Dfile.encoding=UTF-8"
        ).Trim()
    }

    Push-Location $release

    try {
        & $exe @ipedArgs
        $result = $LASTEXITCODE
    }
    finally {
        Pop-Location
    }
}
finally {
    if ($null -eq $oldOptions) {
        Remove-Item Env:JAVA_TOOL_OPTIONS -ErrorAction SilentlyContinue
    }
    else {
        $env:JAVA_TOOL_OPTIONS = $oldOptions
    }
}

if ($result -ne 0) {
    throw "IPED zakonczyl dzialanie z kodem $result"
}

