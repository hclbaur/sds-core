mkdir tmp
javac -d tmp -cp sda-core.jar^
 ..\..\main\java\be\baur\sds\*.java^
 ..\..\main\java\be\baur\sds\common\*.java^
 ..\..\main\java\be\baur\sds\content\*.java^
 ..\..\main\java\be\baur\sds\model\*.java^
 ..\..\main\java\be\baur\sds\serialization\*.java^
 ..\..\main\java\be\baur\sds\validation\*.java
 
jar cvf sds-core.jar -C tmp .

javac -d . -cp sda-core.jar;sds-core.jar ..\java\demo.java

rmdir tmp /s /q