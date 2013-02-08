package demo
import data.Sample
import scala.io.Source
import data.Label
import data.DataSet
import classifier.rf.HyperParameters
import classifier.rf.OnlineRandomForest
import scala.util.control.Breaks._

object OnlineRandomForestTest {

  val numFeatures = 10
  val numClasses = 2

  // I'll use the first 400/683 data points for training and the rest for testing
  val trainSize = 400

  def main(args: Array[String]) = {
    val s = Source.fromURL(getClass.getResource("/resources/breast-cancer-train.txt"))
    
    var trainSamples = new Array[Sample](0)
    var testSamples = new Array[Sample](0)
    var lineCount = 1
    for (line <- s.getLines) {
      var tokens = line.split(' ')
      var label = tokens(0).toInt
      tokens = tokens.slice(1, tokens.length)
      var vector = new Array[Double](tokens.length)
      for (t <- tokens) {
        var column = t.split(':')
        vector(column(0).toInt - 1) = column(1).toDouble
      }

      val sample = new Sample(vector, new Label(label))
      if (lineCount <= trainSize)
        trainSamples = trainSamples :+ sample
      else
        testSamples = testSamples :+ sample

      lineCount += 1
    }

    val trainData = new DataSet(trainSamples, numFeatures, numClasses)
    val testData = new DataSet(testSamples, numFeatures, numClasses)
    
    // Set up the online random forest with default options
    val orf = new OnlineRandomForest(numClasses, numFeatures,
      trainData.minFeatRange, trainData.maxFeatRange)
    println("Training started")
    
    orf.train(trainData)
    println("Training complete")

    var correct: Float = 0
    var incorrect: Float = 0
    for (tSample <- testData.samples) {
      val label = tSample.label.value
      val result = orf.eval(tSample)
      if (label == result.prediction)
        correct += 1
      else
        incorrect += 1
    }
    var pct: Float = correct / (correct + incorrect) * 100
    println("Correctly classified: " + pct + " %")
  }
}