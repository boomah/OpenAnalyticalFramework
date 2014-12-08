package com.openaf.guiservlet

import javax.servlet.http.HttpServlet
import java.util.Hashtable
import com.openaf.properties.api.PropertiesService
import org.osgi.framework.BundleContext
import com.openaf.osgi.OpenAFBundleActivator

class GuiServletBundleActivator extends OpenAFBundleActivator {
  protected def startUp(context:BundleContext) {
    val propertiesService = waitForService[PropertiesService](context)
    val guiProps = new Hashtable[String,AnyRef]
    guiProps.put("alias", GUIServlet.Address)
    context.registerService(classOf[HttpServlet],
      new GUIServlet(propertiesService.name, propertiesService.webExternalURL,
        propertiesService.portForGUIUpdates.toString, propertiesService.servicesPort.toString), guiProps)

    val guiOSGIProps = new Hashtable[String,AnyRef]
    guiOSGIProps.put("alias", OSGIGUIServlet.Address)
    context.registerService(classOf[HttpServlet], new OSGIGUIServlet, guiOSGIProps)
  }

  def stop(context:BundleContext) {}
}