package data

class DataSet(var samples: Array[Sample], val numSamples: Int, 
    val numFeatures: Int, val numClasses: Int, var minFeatRange: Array[Double],
    var maxFeatRange: Array[Double]) {
  
  if(samples == null)
    samples = new Array[Sample](0)
  
  if(minFeatRange == null)
    minFeatRange = new Array[Double](numFeatures)
    
  if(maxFeatRange == null)
    maxFeatRange = new Array[Double](numFeatures)
    
  findFeatRange()
  
  def this(samples: Array[Sample], numFeatures: Int, numClasses: Int) = {
    this(samples, samples.length, numFeatures, numClasses, null, null)
  }
  
  def findFeatRange(): Unit = {
    var minVal: Double = 0
    var maxVal: Double = 0
    
    for(i <- 0 until numFeatures) {
      minVal = samples(0).vector(i)
      maxVal = samples(0).vector(i)
      for(n <- 1 until numSamples) {
        if(samples(n).vector(i) < minVal) {
          minVal = samples(n).vector(i)
        }
        if(samples(n).vector(i) > maxVal) {
          maxVal = samples(n).vector(i)
        }
      }
      
      minFeatRange = minFeatRange :+ minVal
      maxFeatRange = maxFeatRange :+ maxVal
    }
  }
  
}