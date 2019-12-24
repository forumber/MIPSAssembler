@ECHO OFF

if exist build\libs\MIPSAssembler.jar (
	xcopy /y lookUpTable.txt build\libs
	cls
    java -jar build\libs\MIPSAssembler.jar
) else (
    echo Build it first!
)

pause
