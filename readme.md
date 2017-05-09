# Kml Models to Czml Models conversion

Experiment in converting Kml geometry Models to Czml Models.

Note this App does not convert kml COLLADA to czml glTF models, only the kml model documents.

To convert the actual COLLADA (.dae) models or OBJ models to glTF use the [Cesium Converter Tool](https://cesiumjs.org/convertmodel.html)
  
## Usage

Require Java 8.

The easiest way to compile and package the application from source is to use [SBT](http://www.scala-sbt.org/).
To assemble the application and all its dependencies into a single jar file type:

    sbt assembly

This will produce "kml2czml_2.11-1.0.jar" in the "./target/scala-2.11" directory.

For convenience a **kml2czml_2.11-1.0.jar** is included in the "distrib" directory.
Then use as:

    java -jar kml2czml_2.11-1.0.jar kml_file.kml czml_file.czml
  
The czml output file should contain a czml packet with the czml models.  
    