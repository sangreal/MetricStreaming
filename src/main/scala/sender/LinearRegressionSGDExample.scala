package sender

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionModel, LinearRegressionWithSGD}
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.util.MLUtils

object LinearRegressionSGDExample {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setMaster("local")
      .setAppName("LinearRegressionWithSGDExample")
    val sc = new SparkContext(conf)

    // $example on$
    // Load and parse the data
    val data = sc.textFile("/tmp/merge_csv1/part-00000-a0387551-2d74-4b9b-879e-d4457f3aa5df-c000.csv")
    val parsedData = data.map { line =>
      val parts = line.split(',')
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).toDouble))
    }.cache()

    val splits = parsedData.randomSplit(Array(0.7, 0.3))
    val train_data = splits(0)
    val test_data = splits(1)

    // Building the model
    val numIterations = 100
    val stepSize = 0.00000001
    val model = LinearRegressionWithSGD.train(train_data, numIterations, stepSize)

    // Evaluate model on training examples and compute training error
    val valuesAndPreds = parsedData.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }
    val MSE = valuesAndPreds.map { case (v, p) => math.pow((v - p), 2) }.mean()
    println(s"training Mean Squared Error $MSE")

    // Save and load model
    model.save(sc, "target/tmp/scalaLinearRegressionWithSGDModel")
    val sameModel = LinearRegressionModel.load(sc, "target/tmp/scalaLinearRegressionWithSGDModel")
    // $example off$

    val preds = train_data.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }
    preds.foreach{ case (label, prediction) => println(s"label : $label  |  prediction : $prediction")}
    sc.stop()
  }
}
