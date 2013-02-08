package classifier.rf
import util.Utilities
import data.Sample

class RandomTest(val numProjFeatures: Int, var features: Array[Int],
  var weights: Array[Double], val numClasses: Int, var threshold: Double,
  var trueCount: Double, var falseCount: Double,
  var trueStats: Array[Double], var falseStats: Array[Double],
  featMin: Double, featMax: Double) {

  weights = Utilities.getArrayWithRandomNumbers(numProjFeatures)
  threshold = Utilities.randFromRange(featMin, featMax)

  def this(numProjFeatures: Int, features: Array[Int],
    weights: Array[Double], numClasses: Int) = {
    this(numProjFeatures, features, weights, numClasses, 0.0, 0.0, 0.0,
      new Array[Double](10), new Array[Double](10), -1, 1)
  }

  def updateStats(sample: Sample, decision: Boolean): Unit = {
    if (decision) {
      trueCount += sample.weight.value
      trueStats(sample.label.value) = trueStats(sample.label.value) + sample.weight.value
    } else {
      falseCount += sample.weight.value
      falseStats(sample.label.value) = falseStats(sample.label.value) + sample.weight.value
    }
  }

  def score(): Double = {
    val totalCount = trueCount + falseCount
    var p = 0.0
    var splitEntropy: Double = 0.0

    if (trueCount > 0) {
      p = trueCount / totalCount
      splitEntropy -= p * Utilities.log2(p)
    }

    // Prior entropy
    var priorEntropy = 0.0
    for (i <- 0 until numClasses) {
      p = (trueStats(i) + trueStats(i)) / totalCount
      if (p > 0) {
        priorEntropy -= p * Utilities.log2(p)
      }
    }

    // Posterior entropy
    var trueScore = 0.0
    var falseScore = 0.0
    if (trueCount > 0) {
      for (i <- 0 until numClasses) {
        p = trueStats(i) / trueCount
        if (p > 0) {
          trueScore -= p * Utilities.log2(p)
        }
      }
    }

    if (falseCount > 0) {
      for (i <- 0 until numClasses) {
        p = falseStats(i) / falseCount
        if (p > 0) {
          falseScore -= p * Utilities.log2(p)
        }
      }
    }

    val posteriorEntropy = (trueCount * trueScore + falseCount * falseScore) / totalCount

    // Information Gain
    (2.0 * (priorEntropy - posteriorEntropy)) / (priorEntropy * splitEntropy + 1e-10)
  }

  def getStats(): (Array[Double], Array[Double]) = (trueStats, falseStats)

}