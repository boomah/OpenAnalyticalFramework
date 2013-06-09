package com.openaf.travel

import com.openaf.table.server._
import com.openaf.travel.api.TravelPage._
import com.openaf.table.server.FieldDefinitionGroup
import com.openaf.table.lib.api.TableState

class HotelsTableDataSource extends TableDataSource {
  private val fieldDefinitions = List(HotelNameField, PeriodField, CostField, StarRatingField).map(field => Right(new FieldDefinition(field)))

  def fieldDefinitionGroup = FieldDefinitionGroup("Hotel", fieldDefinitions)
  def result(tableState:TableState) = Result.Empty
}
