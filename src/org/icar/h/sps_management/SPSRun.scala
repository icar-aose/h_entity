package org.icar.h.sps_management

import org.icar.h.core.HLauncher

object SPSRun extends App {
  val launcher = new HLauncher("org.icar.h.sps_management.Boot")

  launcher.start()
}
