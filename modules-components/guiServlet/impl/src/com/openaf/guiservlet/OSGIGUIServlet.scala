package com.openaf.guiservlet

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import java.io.File
import java.util.jar.JarFile
import com.openaf.guiservlet.ServletHelper._


object OSGIGUIServlet {
  val Address = "/osgigui"
}

import OSGIGUIServlet._

class OSGIGUIServlet extends HttpServlet {
  private val osgiJARDir = new File("gui-bundle-cache")

  override def doGet(req:HttpServletRequest, resp:HttpServletResponse) {
    val path = req.getRequestURI.stripPrefix(Address).replaceAll("/", "").trim
    path match {
      case "" => sendOSGIJARsPage(resp)
      case s => sendOSGIJAR(s, req, resp)
    }
  }

  private def symbolicNameAndVersion(jar:File) = {
    val mainAttributes = new JarFile(jar).getManifest.getMainAttributes
    (mainAttributes.getValue("Bundle-SymbolicName"), mainAttributes.getValue("Bundle-Version"))
  }

  private def sendOSGIJARsPage(resp:HttpServletResponse) {
    println("£££ SENDING sendOSGIJARsPage") // TODO - why is this called twice?
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

  private def sendOSGIJAR(name:String, req:HttpServletRequest, resp:HttpServletResponse) {
    val components = name.split('-')
    val symbolicName = components(0)
    val version = components(1)
    val timestamp = components(2).toLong
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
