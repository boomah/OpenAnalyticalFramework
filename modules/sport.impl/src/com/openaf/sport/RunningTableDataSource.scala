package com.openaf.sport

import java.nio.file.{Files, Paths}

import com.openaf.sport.api.SportPage._
import com.openaf.table.lib.api.{NoValue, FieldID, TableState}
import com.openaf.table.server._
import com.openaf.table.server.datasources.{DataSourceTable, UnfilteredArrayTableDataSource}

import scala.collection.JavaConversions

class RunningTableDataSource extends UnfilteredArrayTableDataSource {
  private val fieldDefinitions:List[FieldDefinition] = List(
    StringFieldDefinition(LocationField),
    IntegerFieldDefinition(NumberField),
    LocalDateFieldDefinition(DateField),
    IntegerFieldDefinition(PositionField),
    StringFieldDefinition(NameField),
    DurationFieldDefinition(TimeField),
    StringFieldDefinition(AgeCatField),
    StringFieldDefinition(AgeGradeField),
    StringFieldDefinition(GenderField),
    IntegerFieldDefinition(GenderPosField),
    StringFieldDefinition(ClubField),
    StringFieldDefinition(NoteField)
  )

  private val fieldIDs:Array[FieldID] = fieldDefinitions.map(_.fieldID).toArray

  private val fieldDefinitionGroups = FieldDefinitionGroups(List(FieldDefinitionGroup.Standard,
    FieldDefinitionGroup("Fields", fieldDefinitions.map(Right(_)))
  ))

  private lazy val data:Array[Array[Any]] = {
    import JavaConversions._
    val userHome = System.getProperty("user.home")
    val dir = Paths.get(userHome + "/runningData")
    val files = Files.newDirectoryStream(dir)
    val lines = files.flatMap(file => Files.lines(file).toArray.tail.map(_.toString)).toArray
    val numberOfCols = fieldIDs.length
    lines.map(row => {
      val columns = row.split(",")
      columns.zip(fieldDefinitions).map{case (string,fieldDefinition) => fieldDefinition.parser.parse(string).asInstanceOf[Any]}.padTo(numberOfCols, NoValue)
    })
  }
  override def dataSourceTable(tableState:TableState) = DataSourceTable(fieldIDs, data, fieldDefinitionGroups)
}
