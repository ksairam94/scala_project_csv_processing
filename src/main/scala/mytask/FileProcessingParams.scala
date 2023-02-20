package mytask

import scala.collection.mutable.ArrayBuffer

case class FileProcessingParams(var no_of_records: Int,var no_of_failed_records: Int,
                               var sensor_map: Map[String, ArrayBuffer[String]])

object FileProcessingParams {
 var no_of_records: Int = 0
 var no_of_failed_records: Int = 0
 var sensor_map: Map[String, ArrayBuffer[String]] = Map.empty[String, ArrayBuffer[String]]
}
