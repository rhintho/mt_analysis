package de.beuth

import de.beuth.inspector.ArgumentInspector

/**
  * Created by Sebastian Urbanek on 27.01.17.
  */
object Main extends App {
  override def main(args: Array[String]): Unit = {
    try {
      // Parameter auslesen
      val trainingDataPath = args.apply(0)
      val testDataPath = args.apply(1)

      // Überprüfung der Gültigkeit aller Argumente
      if (ArgumentInspector.inspectArguments(trainingDataPath, testDataPath)) {
        println("Alle Parameter sind korrekt, Spark-Machine-Learning-Programm wird gestartet ...")

        // Sparkprogramm starten
        SingleSensorSVM.startSupportVectorMachine(trainingDataPath, testDataPath)
      }
    } catch {
      case e: ArrayIndexOutOfBoundsException =>
        System.err.println("No arguments found.")
    }
  }
}
