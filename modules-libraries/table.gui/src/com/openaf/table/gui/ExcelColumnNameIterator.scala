package com.openaf.table.gui

/**
 * Iterates from 'A' to 'Z' continuing 'AA' to 'AZ' then 'BA' etc. ala Excel.
 */
class ExcelColumnNameIterator extends Iterator[String] {
  private var currentColumnName = "_"
  private def nextColumn(columnName:String):String = {
    columnName.last match {
      case '_' => "A"
      case 'Z' if columnName.length == 1 => "AA"
      case 'Z' => nextColumn(columnName.substring(0, columnName.length - 1)) + 'A'
      case c if columnName.length == 1 => (c + 1).toChar.toString
      case c => columnName.substring(0, columnName.length - 1) + (c + 1).toChar
    }
  }
  override def hasNext = true
  override def next = {
    currentColumnName = nextColumn(currentColumnName)
    currentColumnName
  }
}
