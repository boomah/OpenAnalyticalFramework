package com.openaf.guiservlet

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import xml.XML
import GuiServletHelper._
import java.io.{FileInputStream, BufferedInputStream, File}
import org.eclipse.jetty.util.IO

object GuiServlet {
  val Address = "/gui"
}

class GuiServlet(serverName:String, externalURL:String) extends HttpServlet {
  private val standardMemory = "512m"
  private val specifiedMemory1024 = "1024m"

  private val webStartNormalMemory = "openaf.jnlp"
  private val webStartExtraMemory = "openAFExtra.jnlp"

  private val bootstrapperName = "bootstrapper.jar"
  private val webStartIcon = "webstart-icon.png"

  private val webStartIconName = "com/openaf/guiservlet/resources/openaf.png"

  private val bootstrapperMainClass = "com.openaf.bootstrapper.Bootstrapper"

  override def doGet(req:HttpServletRequest, resp:HttpServletResponse) {
    val path = req.getRequestURI.replaceFirst(GuiServlet.Address, "").replaceAll("/", "")

    if (path.isEmpty) {
      returnGuiPage(resp)
    } else if (path == webStartNormalMemory) {
      returnJNLPFile(resp, webStartNormalMemory, standardMemory)
    } else if (path == webStartExtraMemory) {
      returnJNLPFile(resp, webStartExtraMemory, specifiedMemory1024)
    } else if (path == bootstrapperName) {
      returnBootstrapperJAR(resp)
    } else if (path == webStartIcon) {
      returnImage(webStartIconName, resp)
    } else  {
      resp.sendError(404)
    }
  }

  private def returnGuiPage(resp:HttpServletResponse) {
    def link(target:String) = GuiServlet.Address + "/" + target
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
      <jnlp spec='1.0+' codebase={externalURL + GuiServlet.Address + "/"} href={jnlpName}>
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
    val classesDir = new File("out/production/bootstrapper")
    val lastModified = findLastModified(classesDir)
    val bootstrapperName = "bootstrapper-" + lastModified + ".jar"

    def getOrGenerateBootstrapperJAR(name:String) = {
      val bootstrapperJARFile = new File(FileCacheDir, name)
      if (bootstrapperJARFile.exists()) {
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

  private def writeFileAsResponse(file:File, resp:HttpServletResponse) {
    resp.setContentType("application/octet-stream")
    resp.setDateHeader("Last-Modified", file.lastModified)
    val bufferedInputStream = new BufferedInputStream(new FileInputStream(file))
    IO.copy(bufferedInputStream, resp.getOutputStream)
    bufferedInputStream.close()
    resp.getOutputStream.flush()
    resp.getOutputStream.close()
  }

  private def returnImage(imagePath:String, resp:HttpServletResponse) {
    resp.setContentType("image/png")
    IO.copy(classOf[GuiServlet].getClassLoader.getResourceAsStream(imagePath), resp.getOutputStream)
  }
}
