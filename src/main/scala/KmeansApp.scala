import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.log4j._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{MinMaxScaler, VectorAssembler}
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.sql.functions.{column, expr, from_json}
import org.apache.spark.sql.types.{DataTypes, StructType}

@SerialVersionUID(101L)
class KmeansApp extends Serializable {
  Logger.getLogger("org").setLevel(Level.ERROR)
  def start(): Unit = {
    val spark = SparkSession.builder()
      .master("local")
      .appName("LR").getOrCreate()


    val struct_merge = new StructType()
      .add("cpu_pct", DataTypes.DoubleType)
      .add("mem_pct", DataTypes.DoubleType)

      .add("container_name", DataTypes.StringType)
      .add("message", DataTypes.StringType)
      .add("timestamp", DataTypes.DateType)

    val dataset = spark.read
      .option("header", "true")
      .schema(struct_merge)
      .csv("/tmp/merge_csv1/part-00000-a0387551-2d74-4b9b-879e-d4457f3aa5df-c000.csv")
      .cache()

    val vectorAssembler = new VectorAssembler().setInputCols(Array("cpu_pct")).setOutputCol("features")
    val new_ds = vectorAssembler.transform(dataset)
    val newnewdfs = new_ds.select(column("features"), column("mem_pct"));
    newnewdfs.show(3)

    val splits = newnewdfs.randomSplit(Array(0.7, 0.3))
    val train_df = splits(0)
    val test_df = splits(1)

    val lr = new LinearRegression()
      .setLabelCol("mem_pct")
      .setMaxIter(10)
      .setRegParam(0.3)
      .setElasticNetParam(0.8)

    val lrModel = lr.fit(train_df)

    // Print the coefficients and intercept for linear regression
    println(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")

    // Summarize the model over the training set and print out some metrics
    val trainingSummary = lrModel.summary
    println(s"numIterations: ${trainingSummary.totalIterations}")
    println(s"objectiveHistory: [${trainingSummary.objectiveHistory.mkString(",")}]")
    trainingSummary.residuals.show()
    println(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
    println(s"r2: ${trainingSummary.r2}")

    train_df.describe().show()
  }

}
