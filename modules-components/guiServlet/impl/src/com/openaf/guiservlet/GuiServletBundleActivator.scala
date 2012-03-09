package com.openaf.guiservlet

import org.osgi.framework.{BundleContext, BundleActivator}
import javax.servlet.http.HttpServlet
import java.util.Hashtable

class GuiServletBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    val guiStarterProps = new Hashtable[String,AnyRef]()
    guiStarterProps.put("alias", GUIStarterServlet.Address)
    context.registerService(classOf[HttpServlet], new GUIStarterServlet("Test", "http://localhost:7777"), guiStarterProps)

    val guiOSGIProps = new Hashtable[String,AnyRef]()
    guiOSGIProps.put("alias", OSGIGUIServlet.Address)
    context.registerService(classOf[HttpServlet], new OSGIGUIServlet(), guiOSGIProps)
  }

  def stop(context:BundleContext) {}
}