package com.openaf.guiservlet

import org.osgi.framework.{BundleContext, BundleActivator}
import javax.servlet.http.HttpServlet
import java.util.Hashtable

class GuiServletBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    val props = new Hashtable[String,AnyRef]()
    props.put("alias", GuiServlet.Address)
    context.registerService(classOf[HttpServlet], new GuiServlet("Test", "http://nick-linux:7777"), props)
  }

  def stop(context:BundleContext) {}
}