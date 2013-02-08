package classifier.rf

class HyperParameters(val numRandomTests: Int, val numProjFeat: Int, 
    val counterThreshold: Int, val maxDepth: Int, val numTrees: Int, 
    val useSoftVoting: Boolean, val numEpochs: Int, val trainData: String,
    val testData: String) {

  def this() = this(10, 2, 140, 10, 100, false, 10, null, null)
}