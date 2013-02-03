package com.openaf.guiservlet

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import java.io.File
import java.util.jar.JarFile
import com.openaf.guiservlet.ServletHelper._
import collection.JavaConversions._

object OSGIGUIServlet {
  val Address = "/osgigui"
}

class OSGIGUIServlet extends HttpServlet {
  private val osgiJARDir = new File("gui-bundle-cache")

  override def doGet(req:HttpServletRequest, resp:HttpServletResponse) {
    val parameterMap = req.getParameterMap.toMap
    if (parameterMap.isEmpty) {
      sendOSGIJARsPage(resp)
    } else {
      sendOSGIJAR(parameterMap, req, resp)
    }
  }

  private def symbolicNameAndVersion(jar:File) = {
    val mainAttributes = new JarFile(jar).getManifest.getMainAttributes
    (mainAttributes.getValue("Bundle-SymbolicName"), mainAttributes.getValue("Bundle-Version"))
  }

  private def sendOSGIJARsPage(resp:HttpServletResponse) {
    val jars = osgiJARDir.listFiles.filter(_.getName.toLowerCase.endsWith(".jar"))
    val infoToWrite = jars.map(jar => {
      val (symbolicName, version) = symbolicNameAndVersion(jar)
      val lastModified = jar.lastModified
      List(symbolicName, version, lastModified)
    })
    resp.setContentType("text/plain")
    val writer = resp.getWriter
    infoToWrite.foreach(line => {
      writer.println(line.mkString(" "))
    })
  }

  private def sendOSGIJAR(parameterMap:Map[String,Array[String]], req:HttpServletRequest, resp:HttpServletResponse) {
    val symbolicName = parameterMap("symbolicName").head
    val version = parameterMap("version").head
    val timestamp = parameterMap("timestamp").head.toLong
    val jars = osgiJARDir.listFiles.filter(_.getName.toLowerCase.endsWith(".jar"))
    jars.find(jar => {
      val (symbolicName0, version0) = symbolicNameAndVersion(jar)
      ((symbolicName == symbolicName0) && (version == version0) && (timestamp == jar.lastModified))
    }) match {
      case Some(jar) => {
        writeFileAsResponse(jar, resp)
      }
      case _ => resp.sendError(404)
    }
  }
}
