package mytask

import java.io.{File, FileNotFoundException, IOException}
import scala.collection.mutable.ArrayBuffer
import util.control.Breaks._

import scala.collection.immutable.Map
object Main extends App{

    System.out.println("Enter file path : ")
    val path = scala.io.StdIn.readLine()
    val directory = new File(path)
    val overall_readings_output : FileProcessingParams = FileProcessingParams(0,0,Map.empty[String, ArrayBuffer[String]])
    val formatted_output: ArrayBuffer[String] = ArrayBuffer("") // TODO
    if (directory.exists && directory.isDirectory) {
        val files_list = directory.listFiles.filter(_.isFile).toList
        for(x <- files_list){
            val processed_file_output: FileProcessingParams = processFile(x)
            overall_readings_output.sensor_map = merge_sensor_maps(overall_readings_output.sensor_map,
                processed_file_output.sensor_map)
            overall_readings_output.no_of_records += processed_file_output.no_of_records
            overall_readings_output.no_of_failed_records += processed_file_output.no_of_failed_records
        }
        //process the datastructure and get the min max and avg
        println("overall_readings_output: ", overall_readings_output)
        println("*******************Result**********************")
        println("Num of processed files: ",files_list.size)
        println("Num of processed measurements: ", overall_readings_output.no_of_records)
        println("Num of failed measurements: ", overall_readings_output.no_of_failed_records)
        println("Sensors with highest avg humidity: ")
        for(y <- overall_readings_output.sensor_map.keys){
            overall_readings_output.sensor_map.get(y) match {
                case Some(value) => println(y,findMinMaxAvg(value))
            }
        }
    } else {
        println("Invalid directory name!!")
    }

    def processFile(file_path: File): FileProcessingParams = {
        try {
            val source_file = scala.io.Source.fromFile(file_path)
            var sensor_map: Map[String, ArrayBuffer[String]] = Map.empty[String, ArrayBuffer[String]]
            var no_of_records: Int = 0
            var no_of_failed_records: Int = 0
            //assign everything into a data structure ~ map(string, array of readings)
            for (line <- source_file.getLines().drop(1)) {
                no_of_records += 1
                val cols = line.split(",").map(_.trim)
                if (cols(1) == "NaN") {
                    no_of_failed_records += 1
                }
                if (!sensor_map.contains(cols(0))) {
                    sensor_map = sensor_map ++ Map(cols(0) -> new ArrayBuffer[String]().addOne(cols(1)))
                }
                else {
                    sensor_map.get(cols(0)) match {
                        case Some(previous_value) =>
                            previous_value += cols(1)
                            val temp = sensor_map - cols(0)
                            sensor_map = temp ++ Map(cols(0) -> previous_value)
                    }
                }
            }
            source_file.close()
            FileProcessingParams(no_of_records, no_of_failed_records, sensor_map)
        } catch {
            case ex: FileNotFoundException => {
                println("File Missing")
                FileProcessingParams(0,0,Map.empty[String, ArrayBuffer[String]])
            }

            case ex: IOException => {
                println("Exception during IO")
                FileProcessingParams(0,0,Map.empty[String, ArrayBuffer[String]])
            }

        }
    }

    def merge_sensor_maps(overall_map: Map[String, ArrayBuffer[String]], current_map: Map[String, ArrayBuffer[String]]):
        Map[String, ArrayBuffer[String]]={
        var final_map = overall_map
        for(element <- current_map.keys) {
            if (!overall_map.contains(element)) {
                current_map.get(element) match {
                    case Some(value) => {
                        final_map = final_map ++ Map(element -> value)
                    }
                }
            }
            else {
                current_map.get(element) match {
                    case Some(current_file_readings) =>
                        var summarized_readings = ArrayBuffer[String]()
                        overall_map.get(element) match {
                            case Some(temp_readings) =>  for (i <- current_file_readings) {
                                                            if (!temp_readings.contains(i)) {
                                                                temp_readings += i
                                                            }
                                                          }
                                                          summarized_readings = temp_readings
                        }
                        val temp_sensor_map = overall_map - element
                        final_map = temp_sensor_map ++ Map(element -> summarized_readings)
                }
            }
        }
        final_map
    }

    def findMinMaxAvg(arrayBuffer: ArrayBuffer[String]): ArrayBuffer[String] = {
        if (arrayBuffer.size == 1) {
            var element = ""
            for (x <- arrayBuffer) {
                x match {
                    case "NaN" =>
                        element = "NaN"
                    case _ => element = x
                }
            }
            ArrayBuffer("NaN", "NaN", "NaN")
        }
        else {
            var min_val:String = arrayBuffer(0).toString
            var valid_entries:Int = 0
            var min: Int = 0
            min_val match {
                case "NaN" => min = -1
                case value => min = value.toInt
            }
            var max_val:String = arrayBuffer(0).toString
            var max:Int = 0
            max_val match {
                case "NaN" => max = 999999
                case value => max = value.toInt
            }
            var sum_val:String = arrayBuffer(0).toString
            var sum:Int = 0
            sum_val match {
                case "NaN" => sum = 0
                case value => sum = 0
            }
            for (x <- arrayBuffer) {
                breakable {
                    if (x == "NaN")
                        break
                    if (x.toInt > max)
                        max = x.toInt
                    if (x.toInt < min)
                        min = x.toInt
                    sum = sum + x.toInt
                    valid_entries += 1
                }
            }
            ArrayBuffer(min.toString, (sum/valid_entries).toString, max.toString)
        }
    }
}

