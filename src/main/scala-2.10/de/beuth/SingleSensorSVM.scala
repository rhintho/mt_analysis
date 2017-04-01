package de.beuth

import java.text.DecimalFormat

import org.apache.log4j.{Level, LogManager, Logger}
import org.apache.spark.mllib.classification.{LogisticRegressionWithLBFGS, SVMWithSGD}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.optimization.L1Updater
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Das Objekt kapselt alle Funktionen für das maschinelle Lernen
  * mit der Support Vector Machine und der logistischen Regression.
  */
object SingleSensorSVM {

  // LogManager initialisieren für informative Konsolenausgaben
  val log: Logger = LogManager.getLogger("SingleSensorSVM")
  log.setLevel(Level.DEBUG)

  /**
    *
    * @param trainingDataPath Pfad zur LIBSVM mit den Trainingsdaten
    * @param testDataPath Pfad zur LIBSVM mit den Testdaten
    */
  def startSupportVectorMachine(trainingDataPath: String, testDataPath: String): Unit = {
    log.debug("Start der Support Vector Machine ...")

    val conf = new SparkConf().setAppName("MT_Analysis")
    val sc   = new SparkContext(conf)

    // Daten einlesen
    val trainingdata = MLUtils.loadLibSVMFile(sc, trainingDataPath).cache()
    val testdata = MLUtils.loadLibSVMFile(sc, testDataPath).cache()

    // Maschine trainieren und Modell bilden mit SVM
    val svmAlgorithm = new SVMWithSGD()
    svmAlgorithm.optimizer
      .setNumIterations(200)
      .setRegParam(0.1)
      .setUpdater(new L1Updater)
    val svmModel = svmAlgorithm.run(trainingdata)

    // Maschine mit LR trainieren und das Modell erstellen
    val logistigRegressionModel = new LogisticRegressionWithLBFGS()
      .setNumClasses(2)
      .run(trainingdata)

    // Den Standard-Threshold löschen
    svmModel.clearThreshold()
    logistigRegressionModel.clearThreshold()

    // Scores auf Basis der Test-LIBSVM berechnen (erst für SVM danach für LR)
    val scoreAndLabelsSVM = testdata.map(point => {
      val score = svmModel.predict(point.features)
      (score, point.label)}
    )

    val scoreAndLabelsLogReg = testdata.map(point => {
      val score = logistigRegressionModel.predict(point.features)
      (score, point.label)}
    )

    // Die Evaluationsmetriken erhalten (für SVM und danach LR)
    val metrics = new BinaryClassificationMetrics(scoreAndLabelsSVM)
    val auROC = metrics.areaUnderROC

    val metricsLogReg = new BinaryClassificationMetrics(scoreAndLabelsLogReg)
    val auROCLogReg = metricsLogReg.areaUnderROC

    // Ausgabe zur Kontrolle
    println("\u001B[31m LogReg: Area under ROC = " + new DecimalFormat("#.####").format(auROCLogReg) + "\u001B[0m")
    println("\u001B[31m SVM: Area under ROC = " + new DecimalFormat("#.####").format(auROC) + "\u001B[0m")

    // Modell abspeichern
    svmModel.save(sc, "/Volumes/BLACK_FIN/master_data/svm/model")
    scoreAndLabelsSVM.saveAsTextFile("/Volumes/BLACK_FIN/master_data/svm/score_and_label")

    log.debug("Support Vector Machine Programme wird beendet.")
  }
}
