package com.openaf.start

import ServerOSGIInstanceStarter._
import collection.mutable.ListBuffer

object Server {
  def main(args:Array[String]) {
    System.setProperty("org.osgi.service.http.port", "7777")
    startOrTrigger(TopLevel, guiConfig _, serverConfig _)
  }

  // TODO - These libraries should be specified by the build system or project file.
  private val CommonLibraries = List("osgi", "rmi.common", "cache")
  private val ServerLibraries = List("utils", "rmi.server") ::: CommonLibraries
  private val GUILibraries = List("rmi.client") ::: CommonLibraries
  private val GUIAPIDependencies = List("test", "viewer")

  private def serverModules = modules.filter(module => {
    val moduleDirs = formattedSubNames(moduleDir(module))
    moduleDirs.contains("api") || moduleDirs.contains("impl")
  })
  private def serverModulesBundleDefinitions = serverModules.flatMap(module => {
    val moduleDirs = formattedSubNames(moduleDir(module))
    val lb = new ListBuffer[ModuleBundleDefinition]()
    if (moduleDirs.contains("api")) {
      lb += ModuleBundleDefinition(module, ModuleType.API)
    }
    if (moduleDirs.contains("impl")) {
      lb += ModuleBundleDefinition(module, ModuleType.IMPL)
    }
    lb.toList
  })

  private def serverBundleDefinitions = {
    val serverLibraryBundleDefinitions = ServerLibraries.map(library => ModuleBundleDefinition(library, ModuleType.Library))
    globalLibraryBundleDefinitions ::: serverModulesBundleDefinitions ::: serverLibraryBundleDefinitions :::
      serverOSGIJARBundleDefinitions ::: commonOSGIJARBundleDefinitions
  }

  private def serverConfig = {
    val bundleDefinitions = new SimpleBundleDefinitions(systemPackages _, serverBundleDefinitions _)
    OSGIInstanceConfig(TopLevel + "server", () => Map(), bundleDefinitions)
  }

  private def guiModules = modules.filter(module => {
    moduleDir(module).listFiles().map(_.getName.trim().toLowerCase).contains("gui")
  })
  private def guiModulesBundleDefinitions = guiModules.map(module => ModuleBundleDefinition(module, ModuleType.GUI))

  private def guiLibraryBundleDefinitions = {
    GUILibraries.flatMap(library => {
      val libraryJARBundles = libraryModuleJARs(library).map(jar => SimpleLibraryBundleDefinition(formattedFileName(jar), jar))
      ModuleBundleDefinition(library, ModuleType.Library) :: libraryJARBundles
    })
  }

  private def guiAPIDependencies = {
    GUIAPIDependencies.map(apiDependency => {
      ModuleBundleDefinition(apiDependency, ModuleType.API)
    })
  }

  private def guiBundleDefinitions = {
    globalLibraryBundleDefinitions ::: guiModulesBundleDefinitions ::: guiLibraryBundleDefinitions ::: guiAPIDependencies ::: commonOSGIJARBundleDefinitions
  }

  private def guiConfig = {
    def fakeSystemPackages:List[String] = Nil // This guiConfig just needs the bundles
    val bundleDefinitions = new SimpleBundleDefinitions(fakeSystemPackages _, guiBundleDefinitions _)
    OSGIInstanceConfig(TopLevel + "gui", () => Map(), bundleDefinitions)
  }
}