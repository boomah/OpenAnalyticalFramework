package com.openaf.start

import java.io.File
import collection.mutable.ListBuffer

object FileUtils {
  def findLastModified(dir:File, initialValue:Long = 0):Long = {
    if (!dir.exists) {
      0
    } else {
      (initialValue /: dir.listFiles)((lastModified:Long, f:File) => {
        if (f.isDirectory) {
          findLastModified(f, lastModified)
        } else if (f.isFile) {
          math.max(lastModified, f.lastModified)
        } else {
          lastModified
        }
      })
    }
  }

  def allModules(baseDirectory:File) = {
    val allFiles = baseDirectory.listFiles().toList
    allFiles.flatMap(file => {
      if (file.isDirectory) {
        if (new File(file, file.getName + ".iml").exists()) {
          Some(file.getName)
        } else {
          None
        }
      } else {
        None
      }
    })
  }

  def exportedLibraries(moduleName:String) = {
    val module = new File(moduleName)
    val exportedLibsDir = new File(module, "lib/exportedLibs")
    if (exportedLibsDir.exists()) {
      val allFiles = exportedLibsDir.listFiles().toList
      allFiles.flatMap(file => {
        if (file.isDirectory) {
          None
        } else {
          Some(file)
        }
      })
    } else {
      Nil
    }
  }

  def subModules(moduleName:String) = {
    val componentsModulesDir = new File("modules-components")
    val componentsModulesNames = componentsModulesDir.listFiles().map(_.getName)
    if (componentsModulesNames.contains(moduleName)) {
      val moduleDir = new File(componentsModulesDir, moduleName)
      val subModulesNames = moduleDir.listFiles().map(_.getName)
      val lb = new ListBuffer[ModuleType.ModuleType]()
      if (subModulesNames.contains("api")) {
        lb += ModuleType.API
      }
      if (subModulesNames.contains("impl")) {
        lb += ModuleType.IMPL
      }
      if (subModulesNames.contains("gui")) {
        lb += ModuleType.GUI
      }
      lb.toList
    } else {
      val librariesModulesDir = new File("modules-libraries")
      val librariesModulesNames = librariesModulesDir.listFiles().map(_.getName)
      if (librariesModulesNames.contains(moduleName)) {
        List(ModuleType.Library)
      } else {
        Nil
      }
    }
  }
}
