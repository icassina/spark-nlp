package com.johnsnowlabs.util

import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

import scala.collection.mutable.ArrayBuffer
import scala.util.Try

object CoNLLGenerator {

  def exportConllFiles(spark: SparkSession, filesPath: String, pipelineModel: PipelineModel, outputPath: String): Unit = {
    import spark.implicits._ //for toDS and toDF
    val data = spark.sparkContext.wholeTextFiles(filesPath).toDS.toDF("filename", "text")
    exportConllFiles(data, pipelineModel, outputPath)
  }

  def exportConllFiles(spark: SparkSession, filesPath: String, pipelinePath: String, outputPath: String): Unit = {
    val model = PipelineModel.load(pipelinePath)
    exportConllFiles(spark, filesPath, model, outputPath)
  }

  def exportConllFiles(data: DataFrame, pipelineModel: PipelineModel, outputPath: String): Unit = {
    val POSdataset = pipelineModel.transform(data)
    exportConllFiles(POSdataset, outputPath)
  }

  def exportConllFiles(data: DataFrame, pipelinePath: String, outputPath: String): Unit = {
    val model = PipelineModel.load(pipelinePath)
    exportConllFiles(data, model, outputPath)
  }

  def exportConllFiles(data: DataFrame, outputPath: String): Unit = {
    import data.sparkSession.implicits._ //for udf
    var dfWithNER = data
    //if data does not contain ner column, add "O" as default
    if (Try(data("finished_ner")).isFailure){
      def OArray = (len : Int) => { //create array of $len "O"s
        Array.fill(len)("0")
      }
      val makeOArray = data.sparkSession.udf.register("finished_pos", OArray)
      dfWithNER=data.withColumn("finished_ner", makeOArray(size(col("finished_pos"))))
    }

    val newPOSDataset = dfWithNER.select("finished_token", "finished_pos", "finished_token_metadata", "finished_ner").
      as[(Array[String], Array[String], Array[(String, String)], Array[String])]
    val CoNLLDataset = makeConLLFormat(newPOSDataset)
    CoNLLDataset.coalesce(1).write.format("com.databricks.spark.csv").
      options(scala.collection.Map("delimiter" -> " ", "emptyValue" -> "")).
      save(outputPath)
  }


  def makeConLLFormat(newPOSDataset : Dataset[(Array[String], Array[String], Array[(String, String)], Array[String])]) ={
    import newPOSDataset.sparkSession.implicits._ //for row casting
    newPOSDataset.flatMap(row => {
      val newColumns: ArrayBuffer[(String, String, String, String)] = ArrayBuffer()
      val columns = ((row._1 zip row._2), row._3.map(_._2.toInt), row._4).zipped.map{case (a,b, c) => (a._1, a._2, b, c)}
      var sentenceId = 1
      newColumns.append(("", "", "", ""))
      newColumns.append(("-DOCSTART-", "-X-", "-X-", "O"))
      newColumns.append(("", "", "", ""))
      columns.foreach(a => {
        if (a._3 != sentenceId){
          newColumns.append(("", "", "", ""))
          sentenceId = a._3
        }
        newColumns.append((a._1, a._2, a._2, a._4))
      })
      newColumns
    })
  }

}
