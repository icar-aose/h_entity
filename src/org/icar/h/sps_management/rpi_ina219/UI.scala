package org.icar.h.sps_management.rpi_ina219
import sun.font.TextLabel

import scala.swing._


class UI extends MainFrame {
  title = "Current Value"
  preferredSize = new Dimension(320, 240)


  def testGui (data : AmpData): Unit ={

    var Current = new TextArea(5,25)

    for (i <- 0 to 2 ) {
      Current.append("Current "+i+": "+data.getCurrent(i)+ "\n") //append the contents of the array list to the text area
    }

  }

}
