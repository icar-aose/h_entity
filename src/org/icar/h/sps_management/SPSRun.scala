package org.icar.h.sps_management

import java.util

import org.icar.h.core.HLauncher
import org.icar.h.core.matlab.matEngine.MatValidator

object SPSRun extends App {
  //var x : Array[String] = _

  //MatValidator.main(x)          //test with matlab in the same pc of the h-entity

  Thread.sleep(10000)

  val launcher = new HLauncher("org.icar.h.sps_management.Boot")

  launcher.start()
}
