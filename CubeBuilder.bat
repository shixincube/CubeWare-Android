@echo off
if exist cube-api (
	cd cube-api
	call gradle cubeapi
	cd ..
	echo cube-api finish
	) else (
	echo cube-api missing
	)
if exist cube-call (
	cd cube-call
	call gradle cubecall
	cd ..
	echo cube-call finish
	) else (
	echo cube-call missing
	)
if exist cube-conference (
	cd cube-conference
	call gradle cubeconference
	cd ..
	echo cube-conference finish
	) else (
	echo cube-conference missing
	)
if exist cube-core (
	cd cube-core
	call gradle cubecore
	cd ..
	echo cube-core finish
	) else (
	echo cube-core missing
	)
if exist cube-file (
	cd cube-file
	call gradle cubefile
	cd ..
	echo cube-file finish
	) else (
	echo cube-file missing
	)
if exist cube-instruction (
	cd cube-instruction
	call gradle cubeinstruction
	cd ..
	echo cube-instruction finish
	) else (
	echo cube-instruction missing
	)
if exist cube-live (
	cd cube-live
	call gradle cubelive
	cd ..
	echo cube-live finish
	) else (
	echo cube-live missing
	)
if exist cube-media (
	cd cube-media
	call gradle cubemedia
	cd ..
	echo cube-media finish
	) else (
	echo cube-media missing
	)
if exist cube-message (
	cd cube-message
	call gradle cubemessage
	cd ..
	echo cube-message finish
	) else (
	echo cube-message missing
	)
if exist cube-sharedesktop (
	cd cube-sharedesktop
	call gradle cubesharedesktop
	cd ..
	echo cube-sharedesktop finish
	) else (
	echo cube-sharedesktop missing
	)
if exist cube-whiteboard (
	cd cube-whiteboard
	call gradle cubewhiteboard
	cd ..
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
