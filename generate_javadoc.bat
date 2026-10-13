:: Optional argument for mode (-v for output)
set MODE=%1

:: Compile javadoc with quiet flag unless "-v" arg passed
if %MODE%=="-v" (
    javadoc -d .\docs\javadoc .\src\card_playing_sim\*
) else (
    javadoc -quiet -d .\docs\javadoc\ .\src\card_playing_sim\*
)
