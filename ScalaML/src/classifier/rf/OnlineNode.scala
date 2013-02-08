package classifier.rf
import data.Sample
import data.Result
import util.Utilities

class OnlineNode(val hp: HyperParameters, val numClasses: Int,
  val numFeatures: Int, val depth: Int,
  var isLeaf: Boolean, var counter: Double, var label: Int,
  var parentCounter: Double,
  val minFeatRange: Array[Double], val maxFeatRange: Array[Double],
  var labelStats: Array[Double], var leftChild: OnlineNode,
  var rightChild: OnlineNode, var onlineTests: Array[HyperplaneFeature],
  var bestTest: HyperplaneFeature) {

  isLeaf = true

  if (labelStats == null) {
    labelStats = new Array[Double](numClasses)
  }
  
  label = Utilities.argmax(labelStats)
  parentCounter = labelStats.sum

  if (onlineTests == null) {
    onlineTests = new Array[HyperplaneFeature](hp.numRandomTests)
  }

  // Creating random tests
  for (i <- 0 until hp.numRandomTests) {
    val test = new HyperplaneFeature(hp.numProjFeat, numClasses,
      numFeatures, minFeatRange, maxFeatRange)
    onlineTests(i) = test
  }

  def this(hp: HyperParameters, numClasses: Int, numFeatures: Int,
    minFeatRange: Array[Double], maxFeatRange: Array[Double], depth: Int) = {
    this(hp, numClasses, numFeatures, depth, true, 0.0, -1, 0.0, minFeatRange,
      maxFeatRange, null, null, null, null, null)
  }

  def this(hp: HyperParameters, numClasses: Int, numFeatures: Int,
    minFeatRange: Array[Double], maxFeatRange: Array[Double], depth: Int,
    parentStats: Array[Double]) = {
    this(hp, numClasses, numFeatures, depth, true, 0.0, -1, 0.0, minFeatRange,
      maxFeatRange, parentStats, null, null, null, null)
  }

  def eval(sample: Sample): Result = {
    if (isLeaf) {
      val result = new Result(null, 0)
      if (counter + parentCounter > 0) {
        result.confidence = labelStats
        for (i <- 0 until result.confidence.length)
          result.confidence(i) = result.confidence(i) * (1.0 / (counter + parentCounter))
        //scale(result.confidence, 1.0 / (m_counter + m_parentCounter));
        result.prediction = label
      } else {
        for (i <- 0 until numClasses) {
          result.confidence = result.confidence :+ (1.0 / numClasses)
        }
        result.prediction = 0
      }
      return result
    } else {
      if (bestTest.eval(sample)) {
        return rightChild.eval(sample)
      } else {
        return leftChild.eval(sample)
      }
    }
  }

  def update(sample: Sample): Unit = {
    counter += sample.weight.value
    labelStats(sample.label.value) += sample.weight.value

    if (isLeaf) {
      for (i <- 0 until hp.numRandomTests) {
        onlineTests(i).update(sample)
      }

      label = Utilities.argmax(labelStats)

      if (shouldISplit()) {
        isLeaf = false

        var maxIndex = 0
        var maxScore = -1e10
        var score = maxScore
        for (i <- 0 until hp.numRandomTests) {
          score = onlineTests(i).score()
          if (score > maxScore) {
            maxScore = score
            maxIndex = i
          }
        }

        bestTest = onlineTests(maxIndex)
        onlineTests = new Array[HyperplaneFeature](onlineTests.length)

        // split
        var parentStats = bestTest.getStats()
        rightChild = new OnlineNode(hp, numClasses, numFeatures, minFeatRange, maxFeatRange,
          depth + 1, parentStats._1)
        leftChild = new OnlineNode(hp, numClasses, numFeatures, minFeatRange, maxFeatRange,
          depth + 1, parentStats._2)
      } 
    } else {
        if (bestTest.eval(sample))
          rightChild.update(sample)
        else
          leftChild.update(sample)
      }
  }

  private def shouldISplit(): Boolean = {
    for (i <- 0 until numClasses) {
      if (labelStats(i) == (counter + parentCounter)) {
        return false
      }
    }

    if (depth >= hp.maxDepth)
      return false

    if (counter < hp.counterThreshold)
      return false

    return true
  }

}