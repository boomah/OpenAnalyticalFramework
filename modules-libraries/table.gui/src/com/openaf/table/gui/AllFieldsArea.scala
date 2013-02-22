package com.openaf.table.gui

import javafx.scene.layout.StackPane
import javafx.scene.control.{TreeCell, TreeItem, TreeView}
import javafx.beans.property.Property
import com.openaf.table.api.{Field, FieldGroup, TableData}
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.util.Callback

object AllFieldsArea {
  type TreeItemType = Either[TreeGroup,Field]
}

case class TreeGroup(fieldGroup:String, allChildFields:List[Field])

import AllFieldsArea._

class AllFieldsArea(tableDataProperty:Property[TableData], dragAndDrop:DragAndDrop) extends StackPane with DropTargetContainer {
  tableDataProperty.addListener(new ChangeListener[TableData] {
    def changed(observableValue:ObservableValue[_<:TableData], oldTableData:TableData, newTableData:TableData) {
      updateTreeView(newTableData)
    }
  })

  private val root = new TreeItem[TreeItemType]
  private val treeView = new TreeView[TreeItemType](root)
  treeView.setCellFactory(new Callback[TreeView[TreeItemType],TreeCell[TreeItemType]] {
    def call(treeView:TreeView[TreeItemType]) = new TreeItemTypeTreeCell(dragAndDrop)
  })

  private def updateTreeItem(treeItem:TreeItem[TreeItemType], fieldGroup:FieldGroup) {
    treeItem.setValue(Left(TreeGroup(fieldGroup.groupName, fieldGroup.fields)))
    treeItem.setExpanded(true)
    fieldGroup.children.foreach(fieldGroupOrField => {
      fieldGroupOrField match {
        case Left(fieldGroup0) => {
          val newTreeItem = new TreeItem[TreeItemType]
          treeItem.getChildren.add(newTreeItem)
          updateTreeItem(newTreeItem, fieldGroup0)
        }
        case Right(field) => treeItem.getChildren.add(new TreeItem[TreeItemType](Right(field)))
      }
    })
  }

  private def updateTreeView(tableData:TableData) {
    root.getChildren.clear()
    updateTreeItem(root, tableData.fieldGroup)
  }

  getChildren.addAll(treeView)

  def dropTargets(draggedFields:List[Field]) = Nil
  dragAndDrop.register(this)
}

class TreeItemTypeTreeCell(val dragAndDrop:DragAndDrop) extends TreeCell[TreeItemType] with Draggable {
  private var fields0:List[Field] = Nil
  def fields:List[Field] = fields0
  override def updateItem(treeItemType:TreeItemType, empty:Boolean) {
    super.updateItem(treeItemType, empty)
    if (empty) {
      setText(null)
      fields0 = Nil
    } else {
      treeItemType match {
        case Left(treeGroup) => {
          setText(treeGroup.fieldGroup)
          fields0 = treeGroup.allChildFields
        }
        case Right(field) => {
          setText(field.displayName)
          fields0 = List(field)
        }
      }
    }
  }
}