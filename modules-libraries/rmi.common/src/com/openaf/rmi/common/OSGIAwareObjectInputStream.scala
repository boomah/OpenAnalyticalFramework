package com.openaf.rmi.common

import java.io.{ObjectStreamClass, InputStream, ObjectInputStream}
import collection.mutable

class OSGIAwareObjectInputStream(inputStream:InputStream, topLevelClassLoader:ClassLoader) extends ObjectInputStream(inputStream) {
  private val classLoaders = new mutable.LinkedHashSet[ClassLoader]
  classLoaders += topLevelClassLoader

  override def resolveClass(desc:ObjectStreamClass):Class[_] = {
    var classAndClassLoader:Option[ClassAndClassLoader] = None
    for (classLoader <- classLoaders if classAndClassLoader == None) {
      try {
        val klass = classLoader.loadClass(desc.getName)
        classAndClassLoader = Some(ClassAndClassLoader(klass, klass.getClassLoader))
      } catch {
        case e:Exception => //println("Can't find " + (desc.getName, classLoader))
      }
    }
    classAndClassLoader match {
      case Some(ClassAndClassLoader(klass, classLoader)) => {
        classLoaders += classLoader
        klass
      }
      case _ => super.resolveClass(desc)
    }
  }
}

case class ClassAndClassLoader(klass:Class[_], classLoader:ClassLoader)