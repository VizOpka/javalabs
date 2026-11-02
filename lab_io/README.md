Запуск:

    javac --release 17 -encoding UTF-8 -d out src/*.java
    java -Dfile.encoding=UTF-8 -cp out Task1        # ... Task9
    java -Xmx16m -Dfile.encoding=UTF-8 -cp out Task8