package classifier.rf
import classifier.Classifier
import data.Sample
import data.DataSet
import util.Utilities
import data.Result

class OnlineRandomTree(val hp: HyperParameters, val numClasses: Int,
  val numFeatures: Int, val minFeatRange: Array[Double],
  val maxFeatRange: Array[Double]) extends Classifier {

  val rootNode = new OnlineNode(hp, numClasses, numFeatures, minFeatRange, maxFeatRange, 0);

  override def update(sample: Sample) = rootNode.update(sample)

  override def train(dataSet: DataSet): Unit = {
    var randIndex = new Array[Int](dataSet.numSamples)
    val sampRatio: Int = dataSet.numSamples / 10
    for (n <- 0 until hp.numEpochs) {
      randIndex = Utilities.randPerm(dataSet.numSamples, randIndex)
      for (i <- 0 until dataSet.numSamples) {
        update(dataSet.samples(randIndex(i)))
      }
    }
  }

  override def eval(sample: Sample): Result = rootNode.eval(sample)

  override def test(dataSet: DataSet): Array[Result] = {
    var result = new Array[Result](0)
    for (i <- 0 until dataSet.numSamples) {
      result = result :+ eval(dataSet.samples(i))
    }

    val error = compError(result, dataSet)
    println("error: " + error.toString())

    return result
  }

}