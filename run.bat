@ECHO OFF

if exist build\libs\MIPSAssembler.jar (
	xcopy /y lookUpTable.txt build\libs
	xcopy /y testCode.src build\libs
	cd build\libs
	cls
	java -jar MIPSAssembler.jar
) else (
	echo Build it first!
)

pause
