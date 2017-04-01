package de.beuth

import de.beuth.inspector.ArgumentInspector

/**
  * Einstiegspunkt des Programms.
  */
object Main extends App {

  /**
    * Erste Funktion, die nach Programmstart aufgerufen wird.
    * @param args Argumente, die beim Programmaufruf übergeben werden
    */
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
      // Falls es Probleme beim Auslesen der Programmargumente gibt,
      // wird das Programm beendet und ein Fehler ausgegeben.
      case e: ArrayIndexOutOfBoundsException =>
        System.err.println("No arguments found.")
    }
  }
}
