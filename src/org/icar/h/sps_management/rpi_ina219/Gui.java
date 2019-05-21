package org.icar.h.sps_management.rpi_ina219;

import org.icar.h.sps_management.worker.AmpData;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.*;

public class Gui   {

    JLabel l1,l2,l3,l4;
    JFrame f = new JFrame();


    public Gui()
    {

        f.setSize(400,500);//400 width and 500 height
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible

        l1=new JLabel();
        l2=new JLabel();
        l3=new JLabel();
        l4=new JLabel();
        f.setLayout(new GridLayout(4, 1));
        f.add(l1);
        f.add(l2);
        f.add(l3);
        f.add(l4);
    }


    public void testGui(AmpData data)
    {

        l1.setText("Motor Current: "+data.getCurrent(0)+" mA");
        l2.setText("Main Current: "+data.getCurrent(1)+" mA");
        l3.setText("Led Current: "+data.getCurrent(2)+" mA");
        l4.setText("Aux Current: "+data.getCurrent(3)+" mA");
    }

}
