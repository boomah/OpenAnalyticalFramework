package com.openaf.guiservlet

import java.util.concurrent.{Callable, FutureTask, ConcurrentHashMap}
import java.util.jar.{JarEntry, JarOutputStream}
import java.io._
import org.eclipse.jetty.util.IO
import java.security.MessageDigest
import GUIServlet._
import javax.servlet.http.HttpServletResponse
import java.util.Calendar

object ServletHelper {
  private val expiryTimeForProxies = {
    val cal = Calendar.getInstance()
    cal.set(2099, 0, 1)
    cal.getTimeInMillis
  }

  private val fileCache = new ConcurrentHashMap[String,FutureTask[File]]
  val FileCacheDir = new File("file-cache")
  if (!FileCacheDir.exists) FileCacheDir.mkdir

  def memoizeFile(fileName:String, fileGenerator: =>File):File = {
    val task = new FutureTask(new Callable[File] {def call = fileGenerator})
    var taskToUse = fileCache.putIfAbsent(fileName, task)
    if (taskToUse == null) {
      taskToUse = task
      taskToUse.run()
    }
    taskToUse.get()
  }

  def findLastModified(dir:File, initialValue:Long = 0):Long = {
    if (!dir.exists) throw new FileNotFoundException("Can't find the last modified file as " + dir.getPath + " doesn't exist")
    (initialValue /: dir.listFiles)((lastModified:Long, f:File) => {
      if (f.isDirectory) {
        findLastModified(f, lastModified)
      } else {
        math.max(lastModified, f.lastModified)
      }
    })
  }

  private def allFiles(rootDir:File):List[File] = {
    (List[File]() /: rootDir.listFiles)((fileList:List[File], file:File) => {
      if (file.isDirectory) {
        fileList ++ allFiles(file)
      } else {
        file :: fileList
      }
    })
  }

  def generateAndWriteJARFile(targetFile:File, fromClassesDir:File, mainClass:Option[String]=None) {
    if (!fromClassesDir.exists()) throw new FileNotFoundException("There are no classes to generate a JAR file from as " + fromClassesDir.getPath + " doesn't exist")
    val jarOutputStream = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(targetFile)))
    val fromClassesPathLength = fromClassesDir.getPath.length + 1
    allFiles(fromClassesDir).foreach(file => {
      val path = file.getPath
      val jarEntry = new JarEntry(path.substring(fromClassesPathLength))
      jarEntry.setTime(0)
      jarOutputStream.putNextEntry(jarEntry)
      val inputStream = new BufferedInputStream(new FileInputStream(file))
      IO.copy(inputStream, jarOutputStream)
      inputStream.close()
    })
    mainClass match {
      case Some(classToUse) => {
        jarOutputStream.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"))
        val manifest =
"""Manifest-Version: 1.0
Created-By: OpenAF.com
Main-Class: """ + classToUse + "\n"
        IO.copy(new ByteArrayInputStream(manifest.getBytes), jarOutputStream)
      }
      case _ =>
    }
    jarOutputStream.flush()
    jarOutputStream.close()
  }

  def md5String(file:File) = {
    generateMD5(file)
  }

  private def generateMD5(file:File) = {
    val inputStream = new BufferedInputStream(new FileInputStream(file))
    val buffer = new Array[Byte](1024)
    val md5 = MessageDigest.getInstance("MD5")
    var bytesRead = 0
    while (bytesRead != -1) {
      bytesRead = inputStream.read(buffer)
      if (bytesRead > 0) {
        md5.update(buffer, 0, bytesRead)
      }
    }
    inputStream.close()
    val md5Bytes = md5.digest
    val sb = new StringBuilder
    for (b <- md5Bytes) {
      sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1))
    }
    sb.toString()
  }

  def startModuleJAR = {
    val moduleName = "osgi.start"
    val classesDir = new File(OutputPathPrefix + moduleName)
    val lastModified = findLastModified(classesDir)
    val moduleJARName = moduleName + "_" + lastModified + ".jar"

    def getOrGenerateStartModuleJAR = {
      val moduleJARFile = new File(FileCacheDir, moduleJARName)
      if (moduleJARFile.exists) {
        moduleJARFile
      } else {
        generateAndWriteJARFile(moduleJARFile, classesDir)
        moduleJARFile.setLastModified(lastModified)
        moduleJARFile
      }
    }

    memoizeFile(moduleJARName, getOrGenerateStartModuleJAR)
  }

  def writeFileAsResponse(file:File, resp:HttpServletResponse, requestedMD5:Option[String]=None) {
    def writeFile() {
      resp.setContentType("application/octet-stream")
      val bufferedInputStream = new BufferedInputStream(new FileInputStream(file))
      IO.copy(bufferedInputStream, resp.getOutputStream)
      bufferedInputStream.close()
    }

    requestedMD5 match {
      case None => {
        resp.setDateHeader("Last-Modified", file.lastModified)
        writeFile()
      }
      case Some(md5) => {
        val currentMD5 = md5String(file)
        if (currentMD5 != md5) {
          resp.setContentType("text/plain")
          resp.setStatus(404)
          resp.getWriter.write("The requested MD5 (" + md5 + ") doesn't match the current MD5 (" + currentMD5 + ")")
        } else {
          resp.setDateHeader("Expires", expiryTimeForProxies)
          writeFile()
        }
      }
    }
  }
}