package com.openaf.guiservlet

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import xml.XML
import ServletHelper._
import java.io.File
import org.eclipse.jetty.util.IO

object GUIStarterServlet {
  val Address = "/gui"
  val OutputPathPrefix = "out/production/"
}

import GUIStarterServlet._

class GUIStarterServlet(serverName:String, externalURL:String) extends HttpServlet {
  private val mainClassConfigLine = "com.openaf.start.GUIStarter 7778"

  private val standardMemory = "512m"
  private val specifiedMemory1024 = "1024m"

  private val scalaLibraryJAR = new File("lib/scala-library.jar")
  private val scalaLibraryName = scalaLibraryJAR.getName

  private val webStartNormalMemory = "openaf.jnlp"
  private val webStartExtraMemory = "openAFExtra.jnlp"

  private val bootstrapperName = "bootstrapper.jar"
  private val webStartIcon = "webstart-icon.png"
  private val configName = "config.txt"
  private val osgiStartModuleName = "osgi-module-start.jar"
  private val felixJAR = new File("start/lib/felix.jar")
  private val felixName = felixJAR.getName

  private val webStartIconName = "com/openaf/guiservlet/resources/openaf.png"

  private val bootstrapperMainClass = "com.openaf.bootstrapper.Bootstrapper"

  override def doGet(req:HttpServletRequest, resp:HttpServletResponse) {
    val path = req.getRequestURI.stripPrefix(Address).replaceAll("/", "").trim

    path match {
      case "" => returnGuiPage(resp)
      case `webStartNormalMemory` => returnJNLPFile(resp, webStartNormalMemory, standardMemory)
      case `webStartExtraMemory` => returnJNLPFile(resp, webStartExtraMemory, specifiedMemory1024)
      case `bootstrapperName` => returnBootstrapperJAR(resp)
      case `webStartIcon` => returnImage(webStartIconName, resp)
      case `configName` => returnConfigPage(resp)
      case `osgiStartModuleName` => returnStartModuleJAR(req, resp)
      case `felixName` => returnFelixJAR(req, resp)
      case `scalaLibraryName` => returnScalaLibraryJAR(req, resp)
      case _ => resp.sendError(404)
    }
  }

  private def returnGuiPage(resp:HttpServletResponse) {
    def link(target:String) = Address + "/" + target
    resp.setContentType("text/html")
    val writer = resp.getWriter
    writer.println("""<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">""")
    val page =
      <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
            <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
          <title>OpenAF - {serverName}</title>
          <style type="text/css">
            body {{
            background-color: white
            }}
          </style>
        </head>
        <body>
          <h1>Launch the GUI</h1>
          <a href={link(webStartNormalMemory)}>Launch via web start</a><br/>
          <a href={link(webStartExtraMemory)}>Launch via web start with extra memory</a>
        </body>
      </html>

    XML.write(resp.getWriter, page, "UTF-8", true, null)
  }

  private def returnJNLPFile(resp:HttpServletResponse, jnlpName:String, memory:String) {
    resp.setContentType("application/x-java-jnlp-file")
    val jnlp =
      <jnlp spec='1.0+' codebase={externalURL + Address + "/"} href={jnlpName}>
        <information>
          <title>OpenAF - {serverName}</title>
          <vendor>OpenAF.com</vendor>
            <homepage href='http://www.openaf.com'/>
          <description>OpenAF - {serverName + (if (standardMemory != memory) memory else "")}</description>
            <icon href={webStartIcon}/>
            <offline-allowed/>
          <shortcut><menu submenu="OpenAF"/></shortcut>
        </information>
        <security>
            <all-permissions/>
        </security>
        <resources>
            <j2se version='1.6+' max-heap-size={memory}/>
            <jar href={bootstrapperName}/>
        </resources>
        <application-desc main-class="com.openaf.bootstrapper.Bootstrapper">
          <argument>{externalURL}</argument>
          <argument>{serverName.replaceAll(" ", "_")}</argument>
        </application-desc>
          <update check='always' policy='always'/>
      </jnlp>

    XML.write(resp.getWriter, jnlp, "UTF-8", true, null)
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
    IO.copy(classOf[GUIStarterServlet].getClassLoader.getResourceAsStream(imagePath), resp.getOutputStream)
  }

  private def returnConfigPage(resp:HttpServletResponse) {
    val scalaLibraryJARMD5 = md5String(scalaLibraryJAR)
    val startModuleMD5 = md5String(startModuleJAR)
    val felixJARMD5 = md5String(felixJAR)
    resp.setContentType("text/plain")
    val writer = resp.getWriter
    writer.println(mainClassConfigLine)
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
