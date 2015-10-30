package com.openaf.table.lib.api

sealed trait CombinerType {
  def nameId:String
}

object CombinerType {
  val Types = List(Sum, Max, Min, Mean, Median)
}

case object Sum extends CombinerType {
  override val nameId = "combiner.sum"
}

case object Max extends CombinerType {
  override val nameId = "combiner.max"
}

case object Min extends CombinerType {
  override val nameId = "combiner.min"
}

case object Mean extends CombinerType {
  override val nameId = "combiner.mean"
}

case object Median extends CombinerType {
  override val nameId = "combiner.median"
}
