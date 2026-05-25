$svgPath = Join-Path $PSScriptRoot "..\design\book_icon.svg"
$outPng = Join-Path $PSScriptRoot "..\app\src\main\res\drawable\ic_nav_tennis_ball.png"
if (-not (Test-Path $svgPath)) {
    Write-Error "Place book_icon.svg in design/ folder first"
    exit 1
}
$svg = Get-Content $svgPath -Raw
if ($svg -notmatch 'base64,([A-Za-z0-9+/=\r\n]+)') {
    Write-Error "base64 not found in SVG"
    exit 1
}
$b64 = $matches[1] -replace '\s', ''
[IO.File]::WriteAllBytes($outPng, [Convert]::FromBase64String($b64))
Write-Host "Saved $outPng ($((Get-Item $outPng).Length) bytes)"
