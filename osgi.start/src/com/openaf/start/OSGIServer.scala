package com.openaf.start

import ServerOSGIInstanceStarter._

object OSGIServer {
  def main(args:Array[String]) {
    System.setProperty("org.osgi.service.http.port", "7777")
    startOrTrigger(TopLevel, guiConfig _, serverConfig _)
  }

  private def serverModulesBundleDefinitions = modules.flatMap(module => {
    if (module.endsWith(".api")) {
      Some(ModuleBundleDefinition(module, ModuleType.API))
    } else if (module.endsWith(".impl")) {
      Some(ModuleBundleDefinition(module, ModuleType.IMPL))
    } else {
      None
    }
  })

  private def serverBundleDefinitions = {
    serverModulesBundleDefinitions ::: serverOSGIJARBundleDefinitions ::: commonOSGIJARBundleDefinitions
  }

  private def serverConfig = {
    val bundleDefinitions = new SimpleBundleDefinitions(systemPackages _, serverBundleDefinitions _)
    OSGIInstanceConfig(TopLevel + "server", () => Map(), bundleDefinitions)
  }

  private def guiModulesBundleDefinitions = modules.flatMap(module => {
    if (module.endsWith(".api")) {
      Some(ModuleBundleDefinition(module, ModuleType.API))
    } else if (module.endsWith(".gui")) {
      Some(ModuleBundleDefinition(module, ModuleType.GUI))
    } else if (module.endsWith(".guiapi")) {
      Some(ModuleBundleDefinition(module, ModuleType.GUI_API))
    } else {
      None
    }
  })

  private def guiBundleDefinitions = {
    guiModulesBundleDefinitions ::: commonOSGIJARBundleDefinitions
  }

  private def guiConfig = {
    def fakeSystemPackages:List[String] = Nil // This guiConfig just needs the bundles
    val bundleDefinitions = new SimpleBundleDefinitions(fakeSystemPackages _, guiBundleDefinitions _)
    OSGIInstanceConfig(TopLevel + "gui", () => Map(), bundleDefinitions)
  }
}