package org.icar.h.sps_management.rpi_ina219
import scala.swing._


class UI extends MainFrame {
  title = "Current Value"
  preferredSize = new Dimension(320, 240)
  val Current0 = new Label()
  val Current1 = new Label()
  val Current2 = new Label()

  contents = new GridPanel(3,1) {
    contents.append(Current0, Current1, Current2)
    border = Swing.EmptyBorder(10, 10, 10, 10)
  }
}
