package com.openaf.table.gui

import javafx.scene.layout._
import com.openaf.table.api.{MeasureAreaTree, MeasureAreaLayout, TableData}
import javafx.beans.property.{SimpleStringProperty, SimpleObjectProperty}

class MeasureFieldsArea(val tableDataProperty:SimpleObjectProperty[TableData], val dragAndDrop:DragAndDrop) extends DragAndDropNode {
  getStyleClass.add("measure-fields-area")

  def description = new SimpleStringProperty("Drop Measure and Column Fields Here")
  def fieldsDropped(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = tableData
  def addDropTargets(draggableFieldsInfo:DraggableFieldsInfo) {}
  def childFieldsDropped(dropTarget:DropTarget, draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = {
    tableData
  }
  def removeFields(draggableFieldsInfo:DraggableFieldsInfo, tableData:TableData) = tableData
  def fields(tableDataOption:Option[TableData]) = measureAreaLayout(tableDataOption).allFields.toList
  def nodes = List(new MeasureAreaLayoutNode(measureAreaLayout, tableDataProperty, dragAndDrop, this))
  private def measureAreaLayout(tableDataOption:Option[TableData]) = {
    tableDataOption.getOrElse(tableDataProperty.get).tableState.tableLayout.measureAreaLayout
  }
  private def measureAreaLayout:MeasureAreaLayout = measureAreaLayout(None)
}

class MeasureAreaLayoutNode(measureAreaLayout:MeasureAreaLayout, tableDataProperty:SimpleObjectProperty[TableData],
                            dragAndDrop:DragAndDrop, draggableParent:DraggableParent) extends HBox {
  val measureAreaTreeNodes = measureAreaLayout.measureAreaTrees.map(measureAreaTree => {
    new MeasureAreaTreeNode(measureAreaTree, tableDataProperty, dragAndDrop, draggableParent)
  })
  getChildren.addAll(measureAreaTreeNodes.toArray :_*)
}

class MeasureAreaTreeNode(measureAreaTree:MeasureAreaTree, tableDataProperty:SimpleObjectProperty[TableData],
                          dragAndDrop:DragAndDrop, draggableParent:DraggableParent) extends VBox {
  private val topNode = measureAreaTree.measureAreaTreeType match {
    case Left(measureAreaField) => new FieldNode(measureAreaField.field, dragAndDrop, draggableParent, tableDataProperty)
    case Right(measureAreaLayout) => new MeasureAreaLayoutNode(measureAreaLayout, tableDataProperty, dragAndDrop, draggableParent)
  }
  getChildren.add(topNode)
  if (measureAreaTree.hasChildren) {
    val childMeasureLayoutNode = new MeasureAreaLayoutNode(measureAreaTree.childMeasureAreaLayout, tableDataProperty, dragAndDrop, draggableParent)
    getChildren.add(childMeasureLayoutNode)
  }
}