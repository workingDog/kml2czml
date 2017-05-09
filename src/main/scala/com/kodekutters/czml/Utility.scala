package com.kodekutters.czml

import com.kodekutters.czml.czmlCore.{Cartesian3D, UnitQuaternion}

/**
  * various functions to convert kml heading, pitch and roll to a czml quaternion orientation
  *
  * equations from Cesium-1.33
  */
object Utility {

  // In CZML, a rotation is expressed as a heading, pitch, and roll.
  // Heading is the rotation about the negative z axis.
  // Pitch is the rotation about the negative y axis.
  // Roll is the rotation about the positive x axis.

  /**
    * convert a heading, pitch and roll to a czml quaternion orientation
    *
    * @param heading in radians
    * @param pitch   in radians
    * @param roll    in radians
    * @return a czml quaternion orientation
    */
  def unitQuaternionRad(heading: Double, pitch: Double, roll: Double) = {
    val rollq = fromAxisAngle(new Cartesian3D(1.0, 0.0, 0.0), roll)
    val pitchq = fromAxisAngle(new Cartesian3D(0.0, 1.0, 0.0), -pitch)
    val result = multiply(pitchq, rollq)
    val headq = fromAxisAngle(new Cartesian3D(0.0, 0.0, 1.0), -heading)
    multiply(headq, result)
  }

  /**
    * convert a heading, pitch and roll to a czml quaternion orientation
    *
    * @param heading in degrees
    * @param pitch   in degrees
    * @param roll    in degrees
    * @return a czml quaternion orientation
    */
  def unitQuaternionDeg(heading: Double, pitch: Double, roll: Double) = unitQuaternionRad(toRadians(heading), toRadians(pitch), toRadians(roll))

  def toRadians(d: Double) = (d / 180) * Math.PI

  /**
    * multiply two Quaternion arrays
    */
  def multiply(left: UnitQuaternion, right: UnitQuaternion) = {
    var leftX = left.q.head.x
    var leftY = left.q.head.y
    var leftZ = left.q.head.z
    var leftW = left.q.head.w

    var rightX = right.q.head.x
    var rightY = right.q.head.y
    var rightZ = right.q.head.z
    var rightW = right.q.head.w

    var x = leftW * rightX + leftX * rightW + leftY * rightZ - leftZ * rightY
    var y = leftW * rightY - leftX * rightZ + leftY * rightW + leftZ * rightX
    var z = leftW * rightZ + leftX * rightY - leftY * rightX + leftZ * rightW
    var w = leftW * rightW - leftX * rightX - leftY * rightY - leftZ * rightZ

    new UnitQuaternion(x, y, z, w)
  }

  /**
    * Computes a quaternion representing a rotation around an axis.
    *
    * @param axis  The axis of rotation
    * @param angle he angle in radians to rotate around the axis.
    * @return UnitQuaternion
    */
  def fromAxisAngle(axis: Cartesian3D, angle: Double) = {
    val halfAngle = angle / 2.0
    val s = Math.sin(halfAngle)
    val c = normalize(axis).coordinates.head
    new UnitQuaternion(c.x * s, c.y * s, c.z * s, Math.cos(halfAngle))
  }

  /**
    * normalize a Cartesian3D
    *
    * @param cartesian to normalize
    * @return a cartesian divided by its magnitude.
    */
  def normalize(cartesian: Cartesian3D): Cartesian3D = {
    val c = cartesian.coordinates.head
    val mag = Math.sqrt(c.x * c.x + c.y * c.y + c.z * c.z)
    new Cartesian3D(c.x / mag, c.y / mag, c.z / mag)
  }

}
