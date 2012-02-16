package com.openaf.start

import aQute.lib.osgi.Builder
import aQute.lib.osgi.Constants._
import org.osgi.framework.Version
import java.io._

case class BundleName(name:String, version:Version)

object BundleName {
  def apply(name:String, version:String):BundleName = {
    try {
      BundleName(name, Version.parseVersion(version))
    } catch {
      case e:Exception => {
        throw new Exception("Can't parse " + name + " bundle version " + version, e)
      }
    }
  }
  def fromJar(jarName:String) = {
    val noJar = jarName.substring(0, jarName.size-4)
    val hyphen = noJar.lastIndexOf("-")
    BundleName(noJar.substring(0, hyphen), Version.parseVersion(noJar.substring(hyphen+1)))
  }
}

trait BundleDefinition {
  def name:BundleName
  def lastModified:Long
  def inputStream:InputStream
}

case class SimpleLibraryBundleDefinition(libraryName:String, file:File) extends BundleDefinition {
  def name:BundleName = BundleName(libraryName, new Version(0, 0, 0))
  def lastModified:Long = file.lastModified()
  def inputStream:InputStream = {
    val builder = new Builder()
    builder.addClasspath(file)
    builder.setProperty(BUNDLE_SYMBOLICNAME, name.name)
    builder.setProperty(EXPORT_PACKAGE, "*")
    val jar = builder.build()
    val out = new ByteArrayOutputStream()
    jar.write(out)
    new ByteArrayInputStream(out.toByteArray)
  }
}

case class LibraryBundleDefinition(jarFile:File, excludedPackages:List[String]) extends BundleDefinition {
  def name:BundleName = BundleName.fromJar(jarFile.getName)
  def lastModified:Long = jarFile.lastModified()
  def inputStream:InputStream = {
    val builder = new Builder()
    builder.addClasspath(jarFile)
    builder.setProperty(BUNDLE_SYMBOLICNAME, name.name)
    builder.setProperty(EXPORT_PACKAGE, "*")
    val allIncludes = List[String]()
    val allExcludes = excludedPackages
    builder.setProperty(IMPORT_PACKAGE, (allExcludes.map(p => "!"+p) ::: allIncludes.toList ::: List("*")).mkString(","))
    val jar = builder.build()
    val out = new ByteArrayOutputStream()
    jar.write(out)
    new ByteArrayInputStream(out.toByteArray)
  }
}

case class OSGIJarBundleDefinition(jar:File) extends BundleDefinition {
  def name:BundleName = BundleName.fromJar(jar.getName)
  def lastModified:Long = jar.lastModified()
  def inputStream:InputStream = new BufferedInputStream(new FileInputStream(jar))
}

case class ModuleBundleDefinition(moduleName:String) extends BundleDefinition {
  private lazy val outputDirectory = new File("out/production/" + moduleName)
  def name:BundleName = BundleName(moduleName, new Version(0, 0, 0))
  def lastModified:Long = FileUtils.findLastModified(outputDirectory, outputDirectory.lastModified())
  def inputStream:InputStream = {
    val builder = new Builder()
    builder.addClasspath(outputDirectory)
    builder.setProperty(BUNDLE_SYMBOLICNAME, moduleName)
    builder.setProperty(BUNDLE_ACTIVATOR, "com.openaf." + moduleName + "." + moduleName.capitalize + "BundleActivator")
    builder.setProperty(EXPORT_PACKAGE, "*")
    val allIncludes = List[String]()
    val allExcludes = List[String]()
    builder.setProperty(IMPORT_PACKAGE, (allExcludes.map(p => "!"+p) ::: allIncludes.toList ::: List("*")).mkString(","))
    val jar = builder.build()
    val out = new ByteArrayOutputStream()
    jar.write(out)
    new ByteArrayInputStream(out.toByteArray)
  }
}

trait BundleDefinitions {
  def bundles:List[BundleDefinition]
  def systemPackages:List[String]
}

case class SimpleBundleDefinitions(systemPackages:List[String], bundles:List[BundleDefinition]) extends BundleDefinitions
