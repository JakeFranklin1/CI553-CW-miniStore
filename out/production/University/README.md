###### CI553-miniStore, release for the 2024-CI553 cohort.
###### This is a deliberately convoluted legacy codebase for the CI553 coursework.
###### This version was simplified from catshop system of the previous year CW.
###### This version is all-in-one server configurion, with customer, cashier, packing, and backDoor clients.
## Notes

The file DataBase.txt must contain Derby to work with the Derby database

### BlueJ
To use the Derby database, you must add in 
Tools -> Preferences -> Libaries
%DERBY_HOME%\lib\derby.jar
%DERBY_HOME%\lib\derbytools.jar

REPLACE %DERBY_HOME% with the path to the base of the Derby database

### Eclipse
To use the Derby database, after importing the project,
Move the file DataBase.txt to be in the toplevel directory of your project.
Copy the directory Images to the same top level directory as DataBase.txt

Then in Properties -> Resource -> Java Build Path -> Libraries
Select Add external JARs
%DERBY_HOME%\lib\derby.jar

REPLACE %DERBY_HOME% with the path to the base of the Derby database

### IntelliJ 2022.1.4+

1. File -> New project from existing sources -> select CI553-CW. (Click next until complete)
2. Select JDK 17 ([download](https://jdk.java.net/archive/) if not installed).
3. Run (both of which will fail) `clients.Setup.main()` and `clients.Main.main()`.
4. Run -> Edit configurations (alt+shift+f10) and:
- select Setup, modify options -> modify classpath -> add `\lib\derby.jar` (located in lib/db-derby-10.14.2.0-lib/)
- select Main, modify options -> modify classpath -> add `\lib\derby.jar` (located in lib/db-derby-10.14.2.0-lib/)

## Run

1. To create the Derby database, after compiling the system, run `clients.Setup.main()`.

2. To start the system, run `clients.Main.main()`.
