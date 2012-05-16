package com.openaf.start

import ServerOSGIInstanceStarter._
import collection.mutable.ListBuffer

object Server {
  def main(args:Array[String]) {
    System.setProperty("org.osgi.service.http.port", "7777")
    startOrTrigger(TopLevel, guiConfig _, serverConfig _)
  }

  // TODO - These libraries should be specified by the build system or project file.
  private val ServerLibraries = List("utils")
  private val GUILibraries = List[String]()

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
    globalLibraryBundleDefinitions ::: serverModulesBundleDefinitions ::: serverLibraryBundleDefinitions ::: serverOSGIJarBundleDefinitions
  }

  private def serverConfig = {
    val bundleDefinitions = new SimpleBundleDefinitions(systemPackages _, serverBundleDefinitions _)
    OSGIInstanceConfig(TopLevel + "server", () => Map(), bundleDefinitions)
  }

  private def guiModules = modules.filter(module => {
    moduleDir(module).listFiles().map(_.getName.trim().toLowerCase).contains("gui")
  })
  private def guiModulesBundleDefinitions = guiModules.map(module => ModuleBundleDefinition(module, ModuleType.GUI))

  private def guiBundleDefinitions = {
    val guiLibraryBundleDefinitions = GUILibraries.map(library => ModuleBundleDefinition(library, ModuleType.Library))
    globalLibraryBundleDefinitions ::: guiModulesBundleDefinitions ::: guiLibraryBundleDefinitions
  }

  private def guiConfig = {
    def fakeSystemPackages:List[String] = Nil // This guiConfig just needs the bundles
    val bundleDefinitions = new SimpleBundleDefinitions(fakeSystemPackages _, guiBundleDefinitions _)
    OSGIInstanceConfig(TopLevel + "gui", () => Map(), bundleDefinitions)
  }
}