package com.openaf.start

import java.io.{OutputStream, InputStream, File}


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

  def copyStreams(inputStream:InputStream, outputStream:OutputStream) {
    val buffer = new Array[Byte](1024)
    Iterator continually (inputStream read buffer) takeWhile (_ != -1) filter (_ > 0) foreach { read =>
      outputStream.write(buffer, 0, read)
    }
    inputStream.close()
    outputStream.flush()
    outputStream.close()
  }
}
