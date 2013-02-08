package data

class Result(var confidence: Array[Double], var prediction: Int) {
	
  if(confidence == null)
    confidence = new Array[Double](0)
  
  def this() = this(null, 0)
}