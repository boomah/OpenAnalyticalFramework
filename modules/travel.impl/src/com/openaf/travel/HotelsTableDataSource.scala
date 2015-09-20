package com.openaf.travel

import com.openaf.table.lib.api.TableState
import com.openaf.table.server._
import com.openaf.travel.api.TravelPage._
import com.openaf.table.server.FieldDefinitionGroup
import com.openaf.table.server.datasources.{DataSourceTable, UnfilteredArrayTableDataSource}

class HotelsTableDataSource extends UnfilteredArrayTableDataSource {
  private val fieldDefinitions = List(
    HotelNameField, PeriodField, CostField, StarRatingField
  ).map(field => Right(AnyFieldDefinition(field)))
  private val fieldDefinitionGroups = FieldDefinitionGroups(List(FieldDefinitionGroup("Hotel", fieldDefinitions)))
  override def dataSourceTable(tableState:TableState) = DataSourceTable(Array.empty, Array.empty, fieldDefinitionGroups)
}
