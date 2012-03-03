package com.openaf.guiservlet

import java.util.concurrent.{Callable, FutureTask, ConcurrentHashMap}
import java.util.jar.{JarEntry, JarOutputStream}
import java.io._
import org.eclipse.jetty.util.IO

object GuiServletHelper {
  private val fileCache = new ConcurrentHashMap[String,FutureTask[File]]
  val FileCacheDir = new File("file-cache")
  if (!FileCacheDir.exists) FileCacheDir.mkdir

  def memoizeFile(fileName:String, fileGenerator:(String)=>File):File = {
    val task = new FutureTask(new Callable[File] {def call = fileGenerator(fileName)})
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
      jarOutputStream.putNextEntry(new JarEntry(path.substring(fromClassesPathLength)))
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

  def signJARFile(jarFile:File) {
    if (!jarFile.exists()) throw new FileNotFoundException("Can't sign the JAR file as " + jarFile.getPath + " doesn't exist")
//    val jarSignerPath = new File(System.getProperty("java.home")).getParent + "/bin/jarsigner"
    val jarSignerPath = "jarsigner"
    // Generate .jks file like this: keytool -genkey -alias openaf -keyalg RSA -dname "CN=openaf" -keypass password -storepass password -validity 18250 -keystore openaf.jks
    execute(jarSignerPath, "-keystore", "modules-components/guiServlet/impl/jks/openaf.jks", "-storepass", "password", "-keypass", "password", jarFile.getPath, "openaf")
  }

  private def execute(args:String*) {
    val processBuilder = new ProcessBuilder(args :_*)
    processBuilder.redirectErrorStream(true)
    val process = processBuilder.start()
    val bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream))
    var output = ""
    while (output != null) {
      output = bufferedReader.readLine()
      if (output != null) println("^^^ " + output)
    }
    bufferedReader.close()
  }
}