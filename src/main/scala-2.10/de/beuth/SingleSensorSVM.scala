package de.beuth

import org.apache.log4j.{Level, LogManager, Logger}
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Sebastian Urbanek on 27.01.17.
  */
object SingleSensorSVM {

  // LogManager initialisieren
  val log: Logger = LogManager.getLogger("SingleSensorSVM")
  log.setLevel(Level.DEBUG)

  def startSupportVectorMachine(trainingDataPath: String, testDataPath: String): Unit = {
    log.debug("Start der Support Vector Machine ...")

    val conf = new SparkConf().setAppName("MT_Analysis")
    val sc   = new SparkContext(conf)

    // Daten einlesen
    val data = MLUtils.loadLibSVMFile(sc, trainingDataPath).cache()
    val test = MLUtils.loadLibSVMFile(sc, testDataPath).cache()

    // Maschine trainieren und Modell bilden
    val numIterations = 100
    val model = SVMWithSGD.train(data, numIterations)

    println("Model-Weight = " + model.weights)
    println("Model-Intercept = " + model.intercept)

    // Den Standard-Threshold löschen
    model.clearThreshold()

    // Scores auf Basis der Test.LIBSVM berechnen
    val scoreAndLabels = test.map(point => {
      val score = model.predict(point.features)
      (score, point.label)}
    )

    // Die Evaluationsmetriken erhalten
    val metrics = new BinaryClassificationMetrics(scoreAndLabels)
    val auROC = metrics.areaUnderROC()

    println("Area under ROC = " + auROC)


    // Modell abspeichern
    model.save(sc, "/Volumes/BLACK_FIN/master_data/svm/model")
    scoreAndLabels.saveAsTextFile("/Volumes/BLACK_FIN/master_data/svm/score_and_label")


    log.debug("Support Vector Machine Programme wird beendet.")
  }

}
