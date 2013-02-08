package classifier.rf
import classifier.Classifier
import data.Sample
import data.Result
import util.Utilities
import data.DataSet

class OnlineRandomForest(val hp: HyperParameters, val numClasses: Int,
  val numFeatures: Int, val minFeatRange: Array[Double],
  val maxFeatRange: Array[Double]) extends Classifier {

  var counter = 0.0
  var oobe = 0.0
  var trees = new Array[OnlineRandomTree](hp.numTrees)

  for (i <- 0 until hp.numTrees) {
    val tree = new OnlineRandomTree(hp, numClasses, numFeatures,
      minFeatRange, maxFeatRange)
    trees(i) = tree
  }

  def this(hp: HyperParameters, numClasses: Int, numFeatures: Int) = {
    this(hp, numClasses, numFeatures, null, null)
  }
  
  def this(numClasses: Int, numFeatures: Int, minFeatRange: Array[Double], maxFeatRange: Array[Double]) = {
    this(new HyperParameters(), numClasses, numFeatures, minFeatRange, maxFeatRange)
  }

  override def update(sample: Sample): Unit = {
    counter += sample.weight.value
    var result = new Result(new Array[Double](numClasses), 0)
    var treeResult = new Result(new Array[Double](numClasses), 0)

    var numTries = 0
    for (i <- 0 until hp.numTrees) {
      numTries = Utilities.poisson(1.0)
      if (numTries > 0) {
        trees.foreach(tree => tree.update(sample))
      } else {
        treeResult = trees(i).eval(sample)
        if (hp.useSoftVoting) {
          // add(treeResult.confidence, result.confidence);
        } else {
          result.confidence(treeResult.prediction) += 1
        }
      }
    }

    if (Utilities.argmax(result.confidence) != sample.label.value) {
      oobe += sample.weight.value
    }
  }

  override def train(dataSet: DataSet): Unit = {
    var randIndex = new Array[Int](dataSet.numSamples)
    val sampRatio: Int = dataSet.numSamples / 10
    for (n <- 0 until hp.numEpochs) {
      println("Epoch: " + n)
      randIndex = Utilities.randPerm(dataSet.numSamples, randIndex)
      for (i <- 0 until dataSet.numSamples) {
        //println("Training sample " + i)
        update(dataSet.samples(randIndex(i)))
      }
    }
  }

  override def eval(sample: Sample): Result = {
    var result = new Result(new Array[Double](numClasses), 0)
    var treeResult = new Result(new Array[Double](numClasses), 0)

    for (i <- 0 until hp.numTrees) {
      treeResult = trees(i).eval(sample)
      if (hp.useSoftVoting) {
        // add(treeResult.confidence, result.confidence);
        for (i <- 0 until result.confidence.length)
          result.confidence(i) += treeResult.confidence(i)

        // More functional
        // treeResult.confidence = (treeResult.confidence, result.confidence).zipped map(_ + _)      
      } else {
        result.confidence(treeResult.prediction) += 1
      }
    }

    for (i <- 0 until result.confidence.length)
      result.confidence(i) = result.confidence(i) * (1.0 / hp.numTrees)

    result.prediction = Utilities.argmax(result.confidence)
    return result
  }

  override def test(dataSet: DataSet): Array[Result] = {
    var result = new Array[Result](0)
    for (i <- 0 until dataSet.numSamples) {
      result = result :+ eval(dataSet.samples(i))
    }

    val error = compError(result, dataSet)
    //println("error: " + error.toString())

    return result
  }

}