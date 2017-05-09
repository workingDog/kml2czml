package com.kodekutters.czml

import java.util.UUID

import com.scalakml.io.KmlFileReader
import com.scalakml.kml._
import com.scalakml.{kml => KML}
import com.kodekutters.czml.{czmlProperties => CZMLTYPE}
import com.kodekutters.czml.CzmlImplicits._
import com.kodekutters.czml.czmlCore._
import com.kodekutters.czml.czmlProperties.{CZML, CZMLPacket, CzmlPositions, CzmlProperty}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * converts a KML file containing kml geometry Models into a CZML file with the equivalent czml Models
  * Note: it does not convert the kml collada.dae model to czml glTF model.
  */
object Kml2Czml {

  import com.kodekutters.czml.Utility._

  /**
    * convert KML Models into a CZML Models
    */
  def main(args: Array[String]) {
    val usage = """Usage: java -jar kml2czml_2.11-1.0.jar kml_file.kml czml_file.czml""".stripMargin
    if (args.isEmpty)
      println(usage)
    else {
      val outFile = if (args.length == 2) args(1).trim else ""
      // the input file
      args(0).trim.toLowerCase match {
        case inFile if inFile.endsWith("kml") => doKmlToCzml(inFile, outFile)
        case inFile => println("Error --> input file \"" + inFile + "\" must have extension .kml")
      }
    }
  }

  /**
    * convert the input kml file and write the CZML results to the output file
    *
    * @param inFile  the input kml file name must have extension .kml
    * @param outFile the CZML output file
    */
  private def doKmlToCzml(inFile: String, outFile: String) = {
    // read the KML file
    val kmlOpt = new KmlFileReader().getKmlFromFile(inFile)
    // create the CZML model packets
    val packetList = kmlOpt.flatMap(kml => for (f <- kml.feature) yield getModel(f))
    packetList.map(list => {
      // create an empty czml object with the list of packets
      val czml = new CZML[CZMLPacket](ArrayBuffer(list: _*))
      // write the czml as a json document to the output file
      Util.writeCzmlToFile(czml, Some(outFile))
    })
  }

  /**
    * recursively pick the kml models from the feature such as Document, Folder etc... and convert them
    */
  def getModel(feature: Feature): List[CZMLPacket] = {
    feature match {
      case f: Model => toCzml(f.asInstanceOf[KML.Model])
      case f: Placemark =>
        f.asInstanceOf[KML.Placemark].geometry match {
          case Some(geom) => toCzml(geom.asInstanceOf[KML.Model])
          case None => List.empty
        }
      case f: MultiGeometry => (for (geom <- f.asInstanceOf[KML.MultiGeometry].geometries)
        yield toCzml(geom.asInstanceOf[KML.Model])).flatten.toList
      case f: Document => (for (x <- f.features) yield getModel(x)).flatten.toList
      case f: Folder => (for (x <- f.features) yield getModel(x)).flatten.toList
      case _ => List.empty
    }
  }

  /**
    * do the kml to czml model conversion
    */
  def toCzml(model: KML.Model): List[CZMLPacket] = {
    val kmlScale = model.scale.flatMap(s => s.x).getOrElse(1d) // picking the x scale component
    val kmlLink = model.link.flatMap(s => s.href).getOrElse("").replace(".dae", ".gltf").replace(".obj", ".gltf")
    val kmlId = model.id.getOrElse(UUID.randomUUID().toString)

    // todo it is not correct to have 0.0 as default
    // create a position property
    //  ≥−180 kml.longitude ≤180
    //  ≥−90 kml.latitude ≤90
    val pos = model.location.map(p => CzmlPositions(new Cartographic[DEGREE](
      p.longitude.getOrElse(0.0),
      p.latitude.getOrElse(0.0),
      p.altitude.getOrElse(0.0)))).getOrElse(CzmlPositions(Array[CzmlPosition]()))

    // todo altitude mode, e.g. relativeToGround

    // convert a kml heading, tilt and roll to a czml quaternion orientation
    // ≥−360 kml heading ≤360
    // ≥0 kml tilt ≤180
    // ≥−180 kml roll ≤180
    val czmlQ = model.orientation match {
      case Some(z) => unitQuaternionDeg(z.heading.getOrElse(0.0), z.tilt.getOrElse(0.0), z.roll.getOrElse(0.0))
      case None => new UnitQuaternion(0.0, 0.0, 0.0, 1.0) // identity
    }
    // create an orientation property
    val orient = new CZMLTYPE.Orientation(unitQuaternion = Option(czmlQ))
    // create a czml model from the KML model info
    val czmlModel = new CZMLTYPE.Model(scale = kmlScale, gltf = CzmlUri(kmlLink))
    // create a czml packet
    val packet = new CZMLPacket(kmlId, mutable.HashSet[CzmlProperty](pos, orient, czmlModel))
    // the czml model packet as a list
    List(packet)
  }

}