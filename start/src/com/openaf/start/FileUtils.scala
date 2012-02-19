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
}
