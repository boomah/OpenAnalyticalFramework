package com.openaf.table.gui

import javafx.scene.control.{Cell, TableCell, TableColumn, TableView}
import javafx.beans.property.{ReadOnlyObjectWrapper, Property}
import javafx.beans.value.{ObservableValue, ChangeListener}
import com.openaf.table.lib.api.{RendererID, TableData}
import javafx.collections.{ObservableMap, ObservableList, FXCollections}
import javafx.util.Callback
import javafx.scene.control.TableColumn.CellDataFeatures

class OpenAFTableView(tableDataProperty:Property[TableData]/*, cellFactories:ObservableMap[RendererID,CellFactory[_]]*/)
  extends TableView[ObservableList[Int]] {

  tableDataProperty.addListener(new ChangeListener[TableData] {
    def changed(observableValue:ObservableValue[_<:TableData], oldTableData:TableData, newTableData:TableData) {
      setUpTableView(newTableData)
    }
  })

  private def setUpTableView(tableData:TableData) {
    val rowHeaders = tableData.tableState.tableLayout.rowHeaderFields
    val rowHeaderTableColumns = rowHeaders.map(field => new TableColumn[ObservableList[Int],Int](field.displayName))
    getColumns.clear()
    getColumns.addAll(rowHeaderTableColumns :_*)

    // TODO - do this properly with renderers etc

    val rowHeaderData = FXCollections.observableArrayList[ObservableList[Int]]()
    tableData.rowHeaders.foreach(rowArray => {
      val rowHeaderRow = FXCollections.observableArrayList(rowArray :_*)
      rowHeaderData.add(rowHeaderRow)
    })
    setItems(rowHeaderData)

    rowHeaderTableColumns.zipWithIndex.foreach{case (column, index) => {
      column.setCellValueFactory(new DefaultCellValueFactory(index))
      column.setCellFactory(new DefaultCellFactory(tableData.valueLookUp(rowHeaders(index).id)))
    }}
  }
}

class DefaultCellValueFactory(index:Int)
  extends Callback[CellDataFeatures[ObservableList[Int],Int],ObservableValue[Int]] {

  def call(cellDataFeatures:CellDataFeatures[ObservableList[Int],Int]) = {
    val row = cellDataFeatures.getValue
    new ReadOnlyObjectWrapper[Int](row.get(index))
  }
}

class DefaultCellFactory(values:Array[Any])
  extends Callback[TableColumn[ObservableList[Int],Int],TableCell[ObservableList[Int],Int]] {

  def call(tableColumn:TableColumn[ObservableList[Int],Int]) = new TableCell[ObservableList[Int],Int] {
    override def updateItem(intValue:Int, isEmpty:Boolean) {
      if (isEmpty) {
        setText(null)
      } else {
        val string = values(intValue).toString
        setText(string)
      }
    }
  }
}

trait CellFactory[V] {
  def cell(values:Array[V]):Cell[Int]
}