rem set MAVEN_OPTS=-agentpath:C:\PROGRA~1\YOURKI~1.0\bin\win32\yjpagent.dll=disablestacktelemetry,disableexceptiontelemetry,builtinprobes=none,delay=10000 -Xmx1024m
set MAVEN_OPTS=-Xmx1024m
call mvn -e clean compile exec:java -Dexec.mainClass=com.blogspot.ostas.lora.nodes.replication.report.Main
