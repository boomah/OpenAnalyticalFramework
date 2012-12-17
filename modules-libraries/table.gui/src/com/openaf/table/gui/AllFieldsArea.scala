package com.openaf.table.gui

import javafx.scene.layout.StackPane
import javafx.scene.control.{TreeItem, TreeView}
import javafx.beans.property.Property
import com.openaf.table.api.{FieldGroup, TableData}
import javafx.beans.value.{ObservableValue, ChangeListener}

class AllFieldsArea(tableDataProperty:Property[TableData]) extends StackPane {
  tableDataProperty.addListener(new ChangeListener[TableData] {
    def changed(observableValue:ObservableValue[_<:TableData], oldTableData:TableData, newTableData:TableData) {
      updateTreeView(newTableData)
    }
  })

  private val root = new TreeItem[String]("Fields")
  private val treeView = new TreeView[String](root)

  private def updateTreeItem(treeItem:TreeItem[String], fieldGroup:FieldGroup) {
    treeItem.setValue(fieldGroup.groupName)
    treeItem.setExpanded(true)
    fieldGroup.children.foreach(fieldGroupOrField => {
      fieldGroupOrField match {
        case Left(fieldGroup0) => {
          val newTreeItem = new TreeItem[String]
          treeItem.getChildren.add(newTreeItem)
          updateTreeItem(newTreeItem, fieldGroup0)
        }
        case Right(field) => treeItem.getChildren.add(new TreeItem[String](field.displayName))
      }
    })
  }

  private def updateTreeView(tableData:TableData) {
    root.getChildren.clear()
    updateTreeItem(root, tableData.fieldGroup)
  }

  getChildren.addAll(treeView)
}
