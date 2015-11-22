package com.openaf.table.lib.api

import org.scalatest.FunSuite

class FieldTest extends FunSuite {
  test("setting transformer type enables should transform and sets default renderer") {
    val field = Field[Int]("position")
    val position = 1
    val filteredField = field.withSingleFilter(position)
    assert(!filteredField.filter.shouldTransform, "shouldTransform should be false")
    val rendererId = "DifferentRenderer"
    val rendererUpdatedField = filteredField.withRendererId(rendererId)
    assert(rendererUpdatedField.rendererId === rendererId)
    val transformedField = rendererUpdatedField.withTransformerType(IntToDoubleTransformerType)
    assert(transformedField.filter.shouldTransform, "shouldTransform should be true")
    assert(transformedField.rendererId === RendererId.DefaultRendererId)
  }

  test("resetting transformer clears filter and sets default renderer") {
    val field = Field[Int]("position")
    val position = 1
    val filteredField = field.withSingleFilter(position)
    val rendererId = "DifferentRenderer"
    val rendererUpdatedField = filteredField.withRendererId(rendererId)
    val transformedField = rendererUpdatedField.withTransformerType(IdentityTransformerType)
    assert(transformedField.filter === RetainAllFilter())
    assert(transformedField.rendererId === RendererId.DefaultRendererId)
  }
}
