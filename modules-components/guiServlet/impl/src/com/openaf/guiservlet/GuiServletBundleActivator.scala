package com.openaf.guiservlet

import javax.servlet.http.HttpServlet
import java.util.Hashtable
import org.osgi.util.tracker.{ServiceTrackerCustomizer, ServiceTracker}
import com.openaf.properties.api.PropertiesService
import org.osgi.framework.{ServiceReference, BundleContext, BundleActivator}

class GuiServletBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    new ServiceTracker(context, classOf[PropertiesService], new ServiceTrackerCustomizer[PropertiesService, String] {
      def addingService(serviceReference:ServiceReference[PropertiesService]) = {
        val propertiesService = context.getService(serviceReference)

        val guiStarterProps = new Hashtable[String,AnyRef]()
        guiStarterProps.put("alias", GUIStarterServlet.Address)
        context.registerService(classOf[HttpServlet], new GUIStarterServlet(propertiesService.name, propertiesService.externalURL), guiStarterProps)

        val guiOSGIProps = new Hashtable[String,AnyRef]()
        guiOSGIProps.put("alias", OSGIGUIServlet.Address)
        context.registerService(classOf[HttpServlet], new OSGIGUIServlet(), guiOSGIProps)

        ""
      }
      def modifiedService(serviceReference:ServiceReference[PropertiesService], string:String) {}
      def removedService(serviceReference:ServiceReference[PropertiesService], string:String) {}
    }).open()
  }

  def stop(context:BundleContext) {}
}