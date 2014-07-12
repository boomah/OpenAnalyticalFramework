package com.openaf.table.lib.api

case class TableData(fieldGroup:FieldGroup, tableState:TableState, tableValues:TableValues,
                     defaultRenderers:Map[Field[_],Renderer[_]]) {
  def withTableState(newTableState:TableState) = copy(tableState = newTableState)
  def rowHeaderFields = tableState.rowHeaderFields
  def withRowHeaderFields(newRowHeaderFields:List[Field[_]]) = {
    withTableState(tableState.withRowHeaderFields(newRowHeaderFields))
  }
  def withFilterFields(newFilterFields:List[Field[_]]) = withTableState(tableState.withFilterFields(newFilterFields))
  def columnHeaderLayout = tableState.columnHeaderLayout
  def withColumnHeaderLayout(newColumnHeaderLayout:ColumnHeaderLayout) = {
    withTableState(tableState.withColumnHeaderLayout(newColumnHeaderLayout))
  }

  def rowHeadersAsString = {
    val rowHeaderFields = tableState.tableLayout.rowHeaderFields.toArray
    val numRows = tableValues.rowHeaders.length
    val numCols = rowHeaderFields.length
    val arraysOfStrings = tableValues.rowHeaders.map(row => {
      row.zipWithIndex.map{case (rowValue, column) => {
        val field = rowHeaderFields(column)
        val renderer = defaultRenderers(field).asInstanceOf[Renderer[Any]]
        val value = tableValues.valueLookUp(field.id)(rowValue)
        renderer.render(value)
      }}
    })
    val columnWidths = arraysOfStrings.transpose.map(_.map(_.length).max)
    val sb = new StringBuilder
    def drawSeparatingLine(row:Int) {
      val (start, middle, end) = if (row == 0) ('┌','┬','┐') else if (row == numRows) ('└','┴','┘') else ('├','┼','┤')
      sb += start
      (0 until numCols).foreach(col => {
        sb ++= ("─" * columnWidths(col))
        if (col != numCols - 1) {
          sb += middle
        }
      })
      sb += end
      if (row != numRows) {
        sb ++= System.lineSeparator
      }
    }
    (0 until numRows).foreach(row => {
      drawSeparatingLine(row)
      val rowArray = arraysOfStrings(row)
      (0 until numCols).foreach(col => {
        sb += '│'
        sb ++= rowArray(col).padTo(columnWidths(col), " ").mkString("")
        if (col == numCols - 1) {
          sb += '│'
        }
      })
      sb ++= System.lineSeparator
    })
    drawSeparatingLine(numRows)
    sb.mkString
  }
}
object TableData {
  val Empty = TableData(FieldGroup.Empty, TableState.Blank, TableValues.Empty, Map.empty)
}

case object NoValue

case class TableValues(rowHeaders:Array[Array[Int]], columnHeaders:Array[Array[Array[Int]]],
                       data:Array[Array[Array[Any]]], valueLookUp:Map[FieldID,Array[Any]])

object TableValues {
  val Empty = TableValues(Array.empty, Array.empty, Array.empty, Map.empty)
}