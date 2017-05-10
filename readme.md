# Kml Models to Czml Models conversion

Experiment in converting Kml geometry Models to Czml Models.

Note this App does not convert kml COLLADA to czml glTF models, only the kml model documents.

To convert the actual COLLADA (.dae) models or OBJ models to glTF use the [Cesium Converter Tool](https://cesiumjs.org/convertmodel.html)
  
## Building

Require Java 8.

The easiest way to compile and package the application from source is to use [SBT](http://www.scala-sbt.org/).
To assemble the application and all its dependencies into a single jar file type:

    sbt assembly

This will produce "kml2czml_2.11-1.0.jar" in the "./target/scala-2.11" directory.

For convenience a **kml2czml_2.11-1.0.jar** is included in the "distrib" directory.

## Usage

Use as follows:

    java -jar kml2czml_2.11-1.0.jar kml_file.kml czml_file.czml
  
Where "kml_file.kml" is the kml file containing the kml models descriptions, and  
"czml_file.czml" is the czml output file that will contain the equivalent czml models in a czml packet.
  
For example, converting the kml file "Sydney-oz.kml" produces the file "Sydney-oz.czml".

The following rules are used in the conversion:


    The kml id value is transferred to the czml id value, 
    if there is no id a random (UUID) string is generated.
    
    The kml Scale.x component is used to set the czml scale value, 
    if there is no scale a value of 1 is used.
    
    The kml model Orientation is converted to czml unitQuaternion orientation, 
    if there is no orientation a identity unitQuaternion is used.
    
    The kml Location is used for the czml position, 
    if there is no position a position of zero is used.
    
    The kml reference to the 3D model, 
    for example "geometry/theModel.dae" becomes "geometry/theModel.gltf" for the czml gltf,
    if there is no reference an empty string is used.





  
  