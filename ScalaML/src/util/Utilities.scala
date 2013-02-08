package util
import scala.util.Random

object Utilities {

  val random = new Random();

  def randFromRange(minRange: Double, maxRange: Double): Double = {
    minRange + (maxRange - minRange) * random.nextDouble()
  }

  def log2(value: Double): Double = math.log(value) / math.log(2)

  def argmax(arr: Array[Double]): Int = {
    var maxIdx = 0
    var max = arr(0)
    for(i <- 1 until arr.length) {
      if(arr(i) > max) {
        max = arr(i)
        maxIdx = i
      }
    }
    return maxIdx
  }

  def randPerm(inNum: Int, outVect: Array[Int]): Array[Int] = {
    var result: Array[Int] = new Array[Int](outVect.length)
    var randIndex = 0
    var tempIndex = 0
    for (nFeat <- 0 until inNum) {
      result(nFeat) = nFeat
    }
    for (nFeat <- 0 until inNum) {
      randIndex = (math.floor((inNum - nFeat) * random.nextDouble()) + nFeat).toInt
      if (randIndex == inNum) {
        randIndex -= 1
      }
      tempIndex = result(nFeat)
      result(nFeat) = result(randIndex)
      result(randIndex) = tempIndex
    }
    return result
  }

  def randPerm(inNum: Int, inPart: Int, outVect: Array[Int]): Array[Int] = {
    var result = randPerm(inNum, outVect)
    return result.slice(inPart, result.length)
  }

  def getArrayWithRandomNumbers(length: Int): Array[Double] = {
    var result = new Array[Double](length)
    for (i <- 0 until length) {
      result(i) = 2.0 * (random.nextDouble() - 0.5)
    }
    return result
  }

  def poisson(a: Double): Int = {
    var aa = a
    var k = -1
    var maxK = 10

    do {
      var uk = random.nextDouble()
      aa *= uk
      k += 1
    } while (k <= maxK && a >= math.exp(-1.0))

    return k
  }
}
