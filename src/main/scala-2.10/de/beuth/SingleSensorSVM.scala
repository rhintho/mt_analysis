package de.beuth

import org.apache.log4j.{Level, LogManager, Logger}
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Sebastian Urbanek on 27.01.17.
  */
object SingleSensorSVM {

  // LogManager initialisieren
  val log: Logger = LogManager.getLogger("SingleSensorSVM")
  log.setLevel(Level.DEBUG)

  def startSupportVectorMachine(trainingDataPath: String, testDataPath: String, sensorType: String): Unit = {
    log.debug("Start der Support Vector Machine ...")

    val conf = new SparkConf().setAppName("MT_Analysis")
    val sc   = new SparkContext(conf)

    // Daten einlesen
    val trainingData = MLUtils.loadLibSVMFile(sc, trainingDataPath)

    // Daten trainieren
    val numIterations = 100
    val model = SVMWithSGD.train


    log.debug("Support Vector Machine Programme wird beendet.")
  }

}
