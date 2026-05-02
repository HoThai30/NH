cd /d C:\Users\Admin\Downloads\mssfull\mssdemo\demo
if exist run.log del /f run.log
mvnw.cmd -DskipTests -Dmaven.test.skip=true spring-boot:run > run.log 2>&1
