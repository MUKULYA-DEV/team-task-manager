# Run Maven Wrapper with a JDK. Edit JAVA_HOME below if your JDK is elsewhere,
# or set JAVA_HOME permanently in Windows (Environment Variables) and remove this block.
if (-not $env:JAVA_HOME) {
    $env:JAVA_HOME = 'D:\JDK'
}

& "$PSScriptRoot\mvnw.cmd" @args
