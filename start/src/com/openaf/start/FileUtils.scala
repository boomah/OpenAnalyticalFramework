package com.openaf.start

import java.io.File

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
}
