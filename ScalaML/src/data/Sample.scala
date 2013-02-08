package data

case class Label(value: Int)
case class Weight(value: Double)

class Sample(val vector: Array[Double], val label: Label, val weight: Weight) {

  def this(vector: Array[Double], label: Label) = this(vector, label, new Weight(1.0))
}