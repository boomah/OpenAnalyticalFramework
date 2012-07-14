package com.openaf.guiservlet

import javax.servlet.http.HttpServlet
import java.util.Hashtable
import org.osgi.util.tracker.{ServiceTrackerCustomizer, ServiceTracker}
import com.openaf.properties.api.PropertiesService
import org.osgi.framework.{ServiceReference, BundleContext, BundleActivator}

class GuiServletBundleActivator extends BundleActivator {
  def start(context:BundleContext) {
    new ServiceTracker(context, classOf[PropertiesService], new ServiceTrackerCustomizer[PropertiesService,String] {
      def addingService(serviceReference:ServiceReference[PropertiesService]) = {
        val propertiesService = context.getService(serviceReference)

        val guiProps = new Hashtable[String,AnyRef]()
        guiProps.put("alias", GUIServlet.Address)
        context.registerService(classOf[HttpServlet],
          new GUIServlet(propertiesService.name, propertiesService.webExternalURL,
            propertiesService.portForGUIUpdates.toString, propertiesService.servicesPort.toString), guiProps)

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