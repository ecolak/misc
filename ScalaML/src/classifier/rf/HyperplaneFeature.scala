package classifier.rf
import util.Utilities
import data.Sample

class HyperplaneFeature(numProjFeatures: Int, features: Array[Int],
  weights: Array[Double], numClasses: Int, val numFeatures: Int,
  val minFeatRange: Array[Double], val maxFeatRange: Array[Double])
  extends RandomTest(numProjFeatures, features, weights, numClasses) {
  
  var randFeatures = Utilities.randPerm(numFeatures, numProjFeatures, features)
  var minRange = 0.0
  var maxRange = 0.0
  for (i <- 0 until numProjFeatures) {
    minRange += minFeatRange(randFeatures(i)) * weights(i)
    maxRange += maxFeatRange(randFeatures(i)) * weights(i)
  }
  threshold = Utilities.randFromRange(minRange, maxRange)

  def this(numProjFeatures: Int, numClasses: Int, numFeatures: Int,
    minFeatRange: Array[Double], maxFeatRange: Array[Double]) = {
    this(numProjFeatures, new Array[Int](numFeatures), 
        Utilities.getArrayWithRandomNumbers(numProjFeatures), numClasses, 
        numFeatures, minFeatRange, maxFeatRange)
  }

  def update(sample: Sample) = updateStats(sample, eval(sample))

  def eval(sample: Sample): Boolean = {
    var proj = 0.0
    for (i <- 0 until numProjFeatures) {
      proj += sample.vector(randFeatures(i)) * weights(i)
    }

    if (proj > threshold)
      return true
    else
      return false
  }

}