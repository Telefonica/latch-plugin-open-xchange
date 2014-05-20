### Latch Open-Xchange Pluing ###

### Building ###

To build the Latch Open-Xchange plugin you will need the following software
installed on your machine:

    * JDK 1.6 or greater.
    * Apache Ant 1.8.2 or greater.
    * Open-Xchange 7.4.2.
    * Open-Xchange AppSuite Web UI Git Repository Clone.
    * IzPack 5.0.0 RC2.
    * LogBack 1.1.2.
    * PowerMock 1.5.4 for TestNG and Mockito.
    
The paths to the last five items must be configured in the build.properties file.

Once this has been donde, tne installer can be generated executing the following
command:

```
ant dist
```

This will generate a jar file in the dist directory, which must be run in each
Open-Xchange backend server executing the following command as root:

```
java -jar latch-open-xchange-installer-version.jar
``` 
