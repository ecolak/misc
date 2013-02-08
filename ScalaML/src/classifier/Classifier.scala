package classifier
import data.DataSet
import data.Sample
import data.Result

trait Classifier {

  def update(sample: Sample): Unit
  def eval(samle: Sample): Result
  def train(dataSet: DataSet): Unit
  def test(dataSet: DataSet): Array[Result]

  def compError(results: Array[Result], dataSet: DataSet): Double = {
    var error: Double = 0
    for(i <- 0 until dataSet.numSamples) {
      if(results(i).prediction != dataSet.samples(i).label.value) {
        error += 1
      }
    }
    return error / dataSet.numSamples
  }
}