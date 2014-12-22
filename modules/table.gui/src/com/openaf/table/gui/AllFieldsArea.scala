package com.openaf.table.gui

import javafx.scene.layout.StackPane
import javafx.scene.control.{TreeCell, TreeItem, TreeView}
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.util.Callback
import com.openaf.table.lib.api._
import javafx.scene.SnapshotParameters

object AllFieldsArea {
  type TreeItemType = Either[TreeGroup,Field[_]]
}

case class TreeGroup(fieldGroup:String, allChildFields:List[Field[_]])

import AllFieldsArea._

class AllFieldsArea(val tableFields:OpenAFTableFields) extends StackPane with DropTarget with DragAndDropContainer with ConfigAreaNode {
  getStyleClass.add("all-fields-area")

  tableFields.tableDataProperty.addListener(new ChangeListener[TableData] {
    def changed(observableValue:ObservableValue[_<:TableData], oldTableData:TableData, newTableData:TableData) {
      if (Option(oldTableData).map(_.fieldGroup) != Option(newTableData).map(_.fieldGroup)) {
        updateTreeView(newTableData.fieldGroup)
      }
    }
  })

  private val root = new TreeItem[TreeItemType]
  private val treeView = new TreeView[TreeItemType](root)
  treeView.setShowRoot(false)
  treeView.setCellFactory(new Callback[TreeView[TreeItemType],TreeCell[TreeItemType]] {
    def call(treeView:TreeView[TreeItemType]) = new TreeItemTypeTreeCell(AllFieldsArea.this, tableFields)
  })

  private def updateTreeItem(treeItem:TreeItem[TreeItemType], fieldGroup:FieldGroup) {
    treeItem.setValue(Left(TreeGroup(fieldGroup.groupName, fieldGroup.fields)))
    treeItem.setExpanded(true)
    fieldGroup.children.foreach{
      case Left(childFieldGroup) => {
        val newTreeItem = new TreeItem[TreeItemType]
        treeItem.getChildren.add(newTreeItem)
        updateTreeItem(newTreeItem, childFieldGroup)
      }
      case Right(field) => treeItem.getChildren.add(new TreeItem[TreeItemType](Right(field)))
    }
  }

  private def updateTreeView(fieldGroup:FieldGroup) {
    root.getChildren.clear()
    updateTreeItem(root, fieldGroup)
  }

  getChildren.addAll(treeView)

  def addDropTargets(draggableFieldsInfo:DraggableFieldsInfo) {}
  def removeDropTargets() {}
  def dropTargets(draggableFieldsInfo:DraggableFieldsInfo) = if (getParent != null) List(this) else Nil
  def fieldsDropped(draggableFieldsInfo:DraggableFieldsInfo, tableState:TableState) = tableState
  def childFieldsDropped(dropTarget:DropTarget, draggableFieldsInfo:DraggableFieldsInfo, tableState:TableState) = tableState
  def setDefaultFocus() = {treeView.requestFocus()}
  def isConfigAreaNodeFocused = treeView.isFocused

  dragAndDrop.setRemoveDropTarget(this)
}

class TreeItemTypeTreeCell(allFieldsArea:AllFieldsArea, val tableFields:OpenAFTableFields) extends TreeCell[TreeItemType] with Draggable {
  private var fields0:List[Field[_]] = Nil
  override def updateItem(treeItemType:TreeItemType, isEmpty:Boolean) {
    super.updateItem(treeItemType, isEmpty)
    textProperty.unbind()
    if (isEmpty) {
      setText(null)
      fields0 = Nil
    } else {
      treeItemType match {
        case Left(treeGroup) => {
          setText(treeGroup.fieldGroup)
          fields0 = treeGroup.allChildFields
        }
        case Right(field) => {
          Option(tableFields.fieldBindings.get(field.id)) match {
            case Some(binding) => textProperty.bind(binding)
            case None => setText(field.id.id)
          }
          fields0 = List(field)
        }
      }
    }
  }
  override def noOpSceneBounds = allFieldsArea.localToScene(allFieldsArea.getBoundsInLocal)
  def dragAndDropContainer = allFieldsArea
  def fields = fields0
  def dragImage = snapshot(new SnapshotParameters, null)
  override def isParent = getTreeItem.getValue.isLeft
}