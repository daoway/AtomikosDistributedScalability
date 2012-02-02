set MAVEN_OPTS=-Xmx1024m
call mvn -e clean compile exec:java -Dexec.mainClass=com.blogspot.ostas.lora.nodes.replication.report.Main
