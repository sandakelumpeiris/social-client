# FX Twitter Client
This is a client application that is developed using Java 8 with the JavaFX framework. Users can log in to their accounts using this app and they can view most recent tweets in their home page, search for any tweet view followers and followings, post new tweets through a single interface.
## installation
1. Clone git repository to your computer
2. in **pom.xml** file, change the maven compiler plugins execution configuration according to your java 8 installation location:
    ```xml
        <executable>C:\Program Files\Java\jdk1.8.0_231\bin\javac.exe</executable>
    ```
3. Open terminal on root folder and execute following command:
    ```shell script
    mvn clean install package
    ```
4. After compilation finished, navigate to **target/** directory, and execute following command to run the application:
    ```shell script
    java -jar twitter-client-1.0-jar-with-dependancies.jar
    ```

5. Use system to access your twitter account.

## Requirement
* Java (version 8, 9 or 10. version 8 would be more appropriate)
* Apache maven