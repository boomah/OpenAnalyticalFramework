package com.openaf.testdata.gui

import com.openaf.table.gui.IntRenderer
import com.openaf.table.gui.api.{TableApplication, TableBundleActivator}
import com.openaf.testdata.api.TestData._

class TestDataBundleActivator extends TableBundleActivator {
  override def tableApplication = TestDataTableApplication
}

object TestDataTableApplication extends TableApplication {
  override def nameId = NameId
  override def defaultRenderers = Map(
    IdField.id -> IntRenderer
  )
}
