package de.beuth

/**
  * Created by Sebastian Urbanek on 27.01.17.
  */
object Main extends App {
  override def main(args: Array[String]): Unit = {
    try {
      // Parameter auslesen


    } catch {
      case e: ArrayIndexOutOfBoundsException =>
        System.err.println("No arguments found.")
    }
  }
}
