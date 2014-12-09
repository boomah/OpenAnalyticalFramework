package com.openaf.travel

import com.openaf.table.server._
import com.openaf.travel.api.TravelPage._
import com.openaf.table.server.FieldDefinitionGroup
import com.openaf.table.lib.api.{TableData, TableState}
import com.openaf.table.server.datasources.TableDataSource

class HotelsTableDataSource extends TableDataSource {
  private val fieldDefinitions = List(
    HotelNameField, PeriodField, CostField, StarRatingField
  ).map(field => Right(AnyFieldDefinition(field)))

  def fieldDefinitionGroups = FieldDefinitionGroups(List(FieldDefinitionGroup("Hotel", fieldDefinitions)))
  def tableData(tableState:TableState) = TableData.Empty
}
