package com.openaf.table.gui

import java.lang.Boolean
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control._

import com.openaf.table.gui.binding.{PackageNameBinding, TableLocaleStringBinding}
import com.openaf.table.lib.api._

class FieldNodeContextMenu[T](field:Field[T], tableFields:OpenAFTableFields) extends ContextMenu {
  private def localeProperty = tableFields.localeProperty
  private def stringBinding(id:String) = new TableLocaleStringBinding(id, localeProperty)
  // TODO - the rendererBinding presumes all renderers are going to be in a package with a structure like
  // com\.openaf\.(.+)\.gui which might not be the case for user defined renderers
  private def rendererBinding(renderer:Renderer[_]) = new PackageNameBinding(renderer.name, renderer, localeProperty)
  // TODO - the transformerTypeBinding presumes all transformer types are listed in com.openaf.table.gui.resources.table.properties
  // which will not be the case for user defined transformer types
  private def transformerTypeBinding(transformerType:TransformerType[_]) = stringBinding(transformerType.name)
  private def requestTableStateProperty = tableFields.requestTableStateProperty
  private def requestTableState = requestTableStateProperty.getValue.tableState
  private def tableData = tableFields.tableDataProperty.getValue

  field.fieldType match {
    case MultipleFieldType(currentFieldType) =>
      val menuItem = if (currentFieldType.isDimension) {
        val measureField = new MenuItem
        measureField.textProperty.bind(stringBinding("switchToMeasure"))
        measureField.setOnAction(new EventHandler[ActionEvent] {
          def handle(event:ActionEvent) {
            val newTableState = requestTableState.replaceField(field, field.toMeasure)
            requestTableStateProperty.setValue(RequestTableState(newTableState))
          }
        })
        measureField
      } else {
        val dimensionField = new MenuItem
        dimensionField.textProperty.bind(stringBinding("switchToDimension"))
        dimensionField.setOnAction(new EventHandler[ActionEvent] {
          def handle(event:ActionEvent) {
            val newTableState = requestTableState.replaceField(field, field.toDimension)
            requestTableStateProperty.setValue(RequestTableState(newTableState))
          }
        })
        dimensionField
      }
      getItems.addAll(menuItem, new SeparatorMenuItem)
    case _ =>
  }

  {
    val removeMenuItem = new MenuItem
    removeMenuItem.textProperty.bind(stringBinding("remove"))
    removeMenuItem.setOnAction(new EventHandler[ActionEvent] {
      def handle(event:ActionEvent) {
        val newTableState = requestTableState.remove(List(field))
        requestTableStateProperty.setValue(RequestTableState(newTableState))
      }
    })
    getItems.add(removeMenuItem)
  }
  
  if (field.fieldType.isDimension) {
    val reverseSortOrderMenuItem = new MenuItem
    reverseSortOrderMenuItem.textProperty.bind(stringBinding("reverseSortOrder"))
    reverseSortOrderMenuItem.setOnAction(new EventHandler[ActionEvent] {
      def handle(event:ActionEvent) {
        val newTableState = requestTableState.replaceField(field, field.flipSortOrder)
        requestTableStateProperty.setValue(RequestTableState(newTableState))
      }
    })
    getItems.addAll(new SeparatorMenuItem, reverseSortOrderMenuItem)
  }

  {
    val (topTotalStringID, bottomTotalStringID) = if (tableData.tableState.isColumnHeaderField(field)) {
      ("leftTotal", "rightTotal")
    } else {
      ("topTotal", "bottomTotal")
    }

    val topTotalMenuItem = new CheckMenuItem
    topTotalMenuItem.textProperty.bind(stringBinding(topTotalStringID))
    topTotalMenuItem.selectedProperty.set(field.totals.top)
    topTotalMenuItem.selectedProperty.addListener(new ChangeListener[Boolean] {
      def changed(observable:ObservableValue[_ <: Boolean], oldValue:Boolean, newValue:Boolean) {
        val newTotals = field.totals.copy(top = newValue)
        val newTableState = requestTableState.replaceField(field, field.withTotals(newTotals))
        requestTableStateProperty.setValue(RequestTableState(newTableState))
      }
    })

    val bottomTotalMenuItem = new CheckMenuItem
    bottomTotalMenuItem.textProperty.bind(stringBinding(bottomTotalStringID))
    bottomTotalMenuItem.selectedProperty.set(field.totals.bottom)
    bottomTotalMenuItem.selectedProperty.addListener(new ChangeListener[Boolean] {
      def changed(observable:ObservableValue[_ <: Boolean], oldValue:Boolean, newValue:Boolean) {
        val newTotals = field.totals.copy(bottom = newValue)
        val newTableState = requestTableState.replaceField(field, field.withTotals(newTotals))
        requestTableStateProperty.setValue(RequestTableState(newTableState))
      }
    })
    getItems.addAll(new SeparatorMenuItem, topTotalMenuItem, bottomTotalMenuItem)

    val expandAndCollapse = new ExpandAndCollapse(field, tableFields)

    getItems.addAll(new SeparatorMenuItem, expandAndCollapse.expandAllMenuItem, expandAndCollapse.collapseAllMenuItem)

    val renderers = tableFields.renderers.renderers(field.id, field.transformerType)
    if (renderers.nonEmpty) {
      val toggleGroup = new ToggleGroup
      val menuItems = new SeparatorMenuItem :: renderers.zipWithIndex.map{case (renderer,index) => {
        val rendererMenuItem = new RadioMenuItem
        rendererMenuItem.textProperty.bind(rendererBinding(renderer))
        toggleGroup.getToggles.add(rendererMenuItem)
        if (field.rendererId == renderer.id || (field.rendererId == RendererId.DefaultRendererId && index == 0)) {
          toggleGroup.selectToggle(rendererMenuItem)
        }
        rendererMenuItem.selectedProperty.addListener(new ChangeListener[Boolean] {
          def changed(observable:ObservableValue[_<:Boolean], oldValue:Boolean, newValue:Boolean):Unit = {
            if (newValue) {
              val newField = field.withRendererId(renderer.id)
              val newTableState = requestTableState.replaceField(field, newField)
              val newTableData = Some(tableData.replaceField(field, newField))
              requestTableStateProperty.setValue(RequestTableState(newTableState, newTableData))
            }
          }
        })

        rendererMenuItem
      }}
      getItems.addAll(menuItems.toArray:_*)
    }

    if (field.fieldType.isMeasure) {
      val toggleGroup = new ToggleGroup
      val menuItems = new SeparatorMenuItem :: CombinerType.Types.map(combinerType => {
        val combinerTypeMenuItem = new RadioMenuItem
        combinerTypeMenuItem.textProperty.bind(stringBinding(combinerType.nameId))
        toggleGroup.getToggles.add(combinerTypeMenuItem)
        if (field.combinerType == combinerType) {
          toggleGroup.selectToggle(combinerTypeMenuItem)
        }
        combinerTypeMenuItem.selectedProperty.addListener(new ChangeListener[Boolean] {
          def changed(observable:ObservableValue[_ <: Boolean], oldValue:Boolean, newValue:Boolean):Unit = {
            if (newValue) {
              val newField = field.withCombinerType(combinerType)
              val newTableState = requestTableState.replaceField(field, newField)
              requestTableStateProperty.setValue(RequestTableState(newTableState))
            }
          }
        })
        combinerTypeMenuItem
      })
      getItems.addAll(menuItems.toArray:_*)
    }

    {
      val transformerTypes = tableData.transformers.transformers(field.id)
      if (transformerTypes.nonEmpty) {
        val toggleGroup = new ToggleGroup
        val menuItems = new SeparatorMenuItem :: (IdentityTransformerType :: transformerTypes).map(transformerType => {
          val transformerTypeMenuItem = new RadioMenuItem
          transformerTypeMenuItem.textProperty.bind(transformerTypeBinding(transformerType))
          toggleGroup.getToggles.add(transformerTypeMenuItem)
          if (field.transformerType == transformerType) {
            toggleGroup.selectToggle(transformerTypeMenuItem)
          }
          transformerTypeMenuItem.selectedProperty.addListener(new ChangeListener[Boolean] {
            def changed(observable:ObservableValue[_ <: Boolean], oldValue:Boolean, newValue:Boolean):Unit = {
              if (newValue) {
                val newField = field.withTransformerType(transformerType)
                val newTableState = requestTableState.replaceField(field, newField)
                requestTableStateProperty.setValue(RequestTableState(newTableState))
              }
            }
          })
          transformerTypeMenuItem
        })

        getItems.addAll(menuItems.toArray:_*)
      }
    }
  }
}
