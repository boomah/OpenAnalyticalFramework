package com.openaf.rmi.common

import java.io.{ObjectStreamClass, InputStream, ObjectInputStream}
import collection.mutable

class OSGIAwareObjectInputStream(inputStream:InputStream, topLevelClassLoader:ClassLoader) extends ObjectInputStream(inputStream) {
  private val classLoaders = new mutable.LinkedHashSet[ClassLoader]
  classLoaders += topLevelClassLoader

  override def resolveClass(desc:ObjectStreamClass):Class[_] = {
    var classAndClassLoaderOption:Option[ClassAndClassLoader] = None
    for (classLoader <- classLoaders if classAndClassLoaderOption == None) {
      try {
        val klass = classLoader.loadClass(desc.getName)
        classAndClassLoaderOption = Some(new ClassAndClassLoader(klass, klass.getClassLoader))
      } catch {
        case e:Exception => //println("Can't find " + (desc.getName, classLoader))
      }
    }
    classAndClassLoaderOption match {
      case Some(classAndClassLoader) => {
        classLoaders += classAndClassLoader.classLoader
        classAndClassLoader.klass
      }
      case _ => super.resolveClass(desc)
    }
  }
}

class ClassAndClassLoader(val klass:Class[_], val classLoader:ClassLoader)