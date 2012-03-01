package com.openaf.guiservlet

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import xml.XML

object GuiServlet {
  val Address = "/gui"
}

class GuiServlet(serverName:String, externalURL:String) extends HttpServlet {
  private val standardMemory = "512m"
  private val specifiedMemory1024 = "1024m"

  private val webStartNormalMemory = "openAF.jnlp"
  private val webStartExtraMemory = "openAFExtra.jnlp"

  private val bootstrapperName = "bootstrapper.jar"

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
    } else  {
      resp.sendError(404)
    }
  }

  private def returnGuiPage(resp:HttpServletResponse) {
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
          <a href={webStartNormalMemory}>Launch via web start</a><br/>
          <a href={webStartExtraMemory}>Launch via web start with extra memory</a>
        </body>
      </html>

    XML.write(resp.getWriter, page, "UTF-8", true, null)
  }

  private def returnJNLPFile(resp:HttpServletResponse, jnlpName:String, memory:String) {
    resp.setContentType("application/x-java-jnlp-file")
    val jnlp =
      <jnlp spec='1.0+' codebase={externalURL + GuiServlet.Address + "/"} href={jnlpName}>
        <information>
          <vendor>OpenAF.com</vendor>
          <title>OpenAF - {serverName}</title>
            <homepage href='http://www.openaf.com'/>
          <description>OpenAF - {serverName + (if (standardMemory != memory) memory else "")}</description>
            <icon href='icon.png'/>
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
        </application-desc>ª
          <update check='always' policy='always'/>
      </jnlp>

    XML.write(resp.getWriter, jnlp, "UTF-8", true, null)
  }

  private def returnBootstrapperJAR(resp:HttpServletResponse) {
    println("")
    println("!! WRITE BOOTSTRAPPER")
    println("")
  }
}
