package com.openaf.testdata.gui

import com.openaf.table.gui.api.{TableApplication, TableBundleActivator}
import com.openaf.testdata.api.TestData

class TestDataBundleActivator extends TableBundleActivator {
  override def tableApplication = TestDataTableApplication
}

object TestDataTableApplication extends TableApplication {
  override def nameId = TestData.NameId
}
