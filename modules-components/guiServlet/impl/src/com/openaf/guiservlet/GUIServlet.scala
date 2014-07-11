package com.openaf.guiservlet

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import ServletHelper._
import java.io.File
import org.eclipse.jetty.util.IO

object GUIServlet {
  val Address = "/gui"
  val OutputPathPrefix = "out/production/"
}

import GUIServlet._

class GUIServlet(serverName:String, externalURL:String, guiUpdatePort:String, servicePort:String) extends HttpServlet {
  private val programArgsConfigLine = serverName + " com.openaf.start.GUI " + guiUpdatePort + " " + servicePort

  private val scalaLibraryJAR = new File("common-bundles/scala-library.jar")
  private val scalaLibraryName = scalaLibraryJAR.getName

  private val bootstrapperName = "bootstrapper.jar"
  private val configName = "config.txt"
  private val osgiStartModuleName = "osgi-module-start.jar"
  private val felixJAR = new File("start/lib/felix.jar")
  private val felixName = felixJAR.getName

  private val bootstrapperMainClass = "com.openaf.bootstrapper.Bootstrapper"

  override def doGet(req:HttpServletRequest, resp:HttpServletResponse) {
    val path = req.getRequestURI.stripPrefix(Address).replaceAll("/", "").trim

    path match {
      case "" => returnGuiPage(resp)
      case `bootstrapperName` => returnBootstrapperJAR(resp)
      case `configName` => returnConfigPage(resp)
      case `osgiStartModuleName` => returnStartModuleJAR(req, resp)
      case `felixName` => returnFelixJAR(req, resp)
      case `scalaLibraryName` => returnScalaLibraryJAR(req, resp)
      case _ => resp.sendError(404)
    }
  }

  private def returnGuiPage(resp:HttpServletResponse) {
    resp.setContentType("text/html")
    val writer = resp.getWriter
    writer.println("""<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">""")
    val page =
      s"""<html xmlns="http://www.w3.org/1999/xhtml">
         |  <head>
         |    <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
         |    <title>OpenAF - $serverName</title>
         |    <style type="text/css">
         |      body {{
         |      background-color: white
         |      }}
         |    </style>
         |  </head>
         |  <body>
         |    <h1>Launch the GUI - TODO GENERATE EXE HERE</h1>
         |  </body>
         |</html>""".stripMargin
    resp.getWriter.println(page)
  }

  private def returnBootstrapperJAR(resp:HttpServletResponse) {
    val classesDir = new File(OutputPathPrefix + "bootstrapper")
    val lastModified = findLastModified(classesDir)
    val bootstrapperName = "bootstrapper-" + lastModified + ".jar"

    def getOrGenerateBootstrapperJAR = {
      val bootstrapperJARFile = new File(FileCacheDir, bootstrapperName)
      if (bootstrapperJARFile.exists) {
        bootstrapperJARFile
      } else {
        generateAndWriteJARFile(bootstrapperJARFile, classesDir, Some(bootstrapperMainClass))
        signJARFile(bootstrapperJARFile)
        bootstrapperJARFile.setLastModified(lastModified)
        bootstrapperJARFile
      }
    }

    val bootstrapperJARFile = memoizeFile(bootstrapperName, getOrGenerateBootstrapperJAR)
    writeFileAsResponse(bootstrapperJARFile, resp)
  }

  private def returnImage(imagePath:String, resp:HttpServletResponse) {
    resp.setContentType("image/png")
    IO.copy(classOf[GUIServlet].getClassLoader.getResourceAsStream(imagePath), resp.getOutputStream)
  }

  private def returnConfigPage(resp:HttpServletResponse) {
    val scalaLibraryJARMD5 = md5String(scalaLibraryJAR)
    val startModuleMD5 = md5String(startModuleJAR)
    val felixJARMD5 = md5String(felixJAR)
    resp.setContentType("text/plain")
    val writer = resp.getWriter
    writer.println(programArgsConfigLine)
    writer.println(scalaLibraryName + " " + scalaLibraryJARMD5)
    writer.println(osgiStartModuleName + " " + startModuleMD5)
    writer.println(felixName + " " + felixJARMD5)
  }

  private def returnStartModuleJAR(req:HttpServletRequest, resp:HttpServletResponse) {
    val startModuleJARFile = startModuleJAR
    writeFileAsResponse(startModuleJARFile, resp, Some(req.getParameter("md5")))
  }

  private def returnFelixJAR(req:HttpServletRequest, resp:HttpServletResponse) {
    writeFileAsResponse(felixJAR, resp, Some(req.getParameter("md5")))
  }

  private def returnScalaLibraryJAR(req:HttpServletRequest, resp:HttpServletResponse) {
    writeFileAsResponse(scalaLibraryJAR, resp, Some(req.getParameter("md5")))
  }
}
