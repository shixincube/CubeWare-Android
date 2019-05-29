@echo off
if exist cube-api (
	cd cube-api
	start cmd /c gradle cubeapi
	cd ..
	pause
	echo cube-api finish
	) else (
	echo cube-api missing
	)
if exist cube-call (
	cd cube-call
	start cmd /c gradle cubecall
	cd ..
	pause
	echo cube-call finish
	) else (
	echo cube-call missing
	)
if exist cube-conference (
	cd cube-conference
	start cmd /c gradle cubeconference
	cd ..
	pause
	echo cube-conference finish
	) else (
	echo cube-conference missing
	)
if exist cube-core (
	cd cube-core
	start cmd /c gradle cubecore
	cd ..
	pause
	echo cube-core finish
	) else (
	echo cube-core missing
	)
if exist cube-file (
	cd cube-file
	start cmd /c gradle cubefile
	cd ..
	pause
	echo cube-file finish
	) else (
	echo cube-file missing
	)
if exist cube-instruction (
	cd cube-instruction
	start cmd /c gradle cubeinstruction
	cd ..
	pause
	echo cube-instruction finish
	) else (
	echo cube-instruction missing
	)
if exist cube-live (
	cd cube-live
	start cmd /c gradle cubelive
	cd ..
	pause
	echo cube-live finish
	) else (
	echo cube-live missing
	)
if exist cube-media (
	cd cube-media
	start cmd /c gradle cubemedia
	cd ..
	pause
	echo cube-media finish
	) else (
	echo cube-media missing
	)
if exist cube-message (
	cd cube-message
	start cmd /c gradle cubemessage
	cd ..
	pause
	echo cube-message finish
	) else (
	echo cube-message missing
	)
if exist cube-sharedesktop (
	cd cube-sharedesktop
	start cmd /c gradle cubesharedesktop
	cd ..
	pause
	echo cube-sharedesktop finish
	) else (
	echo cube-sharedesktop missing
	)
if exist cube-whiteboard (
	cd cube-whiteboard
	start cmd /c gradle cubewhiteboard
	cd ..
	pause
	echo cube-whiteboard finish
	) else (
	echo cube-whiteboard missing
	)
set /p str=Whether to copy files to app/libs(y/n)?:
if %str% == y (
    copy .\cube-api\build\libs\cube-api*.jar .\app\libs
    copy .\cube-call\build\libs\cube-call*.jar .\app\libs
    copy .\cube-conference\build\libs\cube-conference*.jar .\app\libs
    copy .\cube-core\build\libs\cube-core*.jar .\app\libs
    copy .\cube-file\build\libs\cube-file*.jar .\app\libs
    copy .\cube-instruction\build\libs\cube-instruction*.jar .\app\libs
    copy .\cube-live\build\libs\cube-live*.jar .\app\libs
    copy .\cube-media\build\libs\cube-media*.jar .\app\libs
    copy .\cube-message\build\libs\cube-message*.jar .\app\libs
    copy .\cube-sharedesktop\build\libs\cube-sharedesktop*.jar .\app\libs
    copy .\cube-whiteboard\build\libs\cube-whiteboard*.jar .\app\libs
    echo copy success
) else (
    echo Don't copy
)
