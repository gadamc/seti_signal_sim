package org.seti.simulator.signaldef

import java.util.Random;

abstract class SignalDef (val rand: Random, val dataClass: String)  {

  //default values
  var sigmaN: Double = 13.0
  var deltaPhiRad: Double = 0.0
  var SNR: Double = 0.0
  var drift: Double = 0.0
  var driftRateDerivate: Double = 0.0
  var sigmaSquiggle: Double = 0.0
  //var outputLength: Int = 786432 128 * 6144
  var outputLength: Int = 196608  //32 * 6144
  var ampModType: String = "none"
  var ampModPeriod: Double = 0.0
  var ampModDuty: Double = 0.0
  var signalClass: String = _

  next //call next in constructor!


  //each subclass needs to define this
  def next

  def nextDoubleFromRange(min: Double, max: Double) : Double = {
    return min + (max - min)*rand.nextDouble
  }

  override def toString() : String = {
      "sigmaN = " + sigmaN + "\n" +
      "deltaPhiRad = " + deltaPhiRad + "\n" +
      "SNR = " + SNR + "\n" +
      "drift = " + drift + "\n" +
      "driftRateDerivate = " + driftRateDerivate + "\n" +
      "sigmaSquiggle = " + sigmaSquiggle + "\n" +
      "outputLength = " + outputLength + "\n" +
      "ampModType = " + ampModType + "\n" +
      "ampModPeriod (only valid if type != none) = " + ampModPeriod + "\n" +
      "ampModDuty (only if type = 'square' or 'brightpixel') = " + ampModDuty + "\n" +
      "signalClass = " + signalClass + "\n"
      "dataClass = " + dataClass + "\n"
  }
}