package com.openaf.table.gui

import javafx.scene.layout.StackPane
import javafx.scene.control.{TreeCell, TreeItem, TreeView}
import javafx.beans.property.Property
import com.openaf.table.api.{Field, FieldGroup, TableData}
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.util.Callback
import javafx.event.EventHandler
import javafx.scene.input.{MouseDragEvent, MouseEvent}

object AllFieldsArea {
  type TreeItemType = Either[String,Field]
}

import AllFieldsArea._

class AllFieldsArea(tableDataProperty:Property[TableData], dragAndDrop:DragAndDrop) extends StackPane {
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
    treeItem.setValue(Left(fieldGroup.groupName))
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
}

class TreeItemTypeTreeCell(dragAndDrop:DragAndDrop) extends TreeCell[TreeItemType] {
  override def updateItem(treeItemType:TreeItemType, empty:Boolean) {
    super.updateItem(treeItemType, empty)
    if (empty) {
      setText(null)
    } else {
      treeItemType match {
        case Left(fieldGroup) => setText(fieldGroup)
        case Right(field) => {
          setText(field.displayName)
          setOnDragDetected(new EventHandler[MouseEvent] {
            def handle(event:MouseEvent) {
              dragAndDrop.fieldBeingDragged.set(Some(field))
              dragAndDrop.dragging.set(true)
              event.consume()
            }
          })
          setOnMouseReleased(new EventHandler[MouseEvent] {
            def handle(event:MouseEvent) {
              dragAndDrop.fieldBeingDragged.set(None)
              dragAndDrop.dragging.set(false)
              event.consume()
            }
          })
          setOnMouseDragged(new EventHandler[MouseEvent] {
            def handle(event:MouseEvent) {
              println("Mouse dragged " + (event.getSceneX, event.getSceneY))
              event.consume()
            }
          })
        }
      }
    }
  }
}