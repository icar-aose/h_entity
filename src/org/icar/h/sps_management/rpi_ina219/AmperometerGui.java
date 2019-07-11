package org.icar.h.sps_management.rpi_ina219;

import org.icar.h.sps_management.worker.AmpData;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.*;
import java.util.ResourceBundle;

public class AmperometerGui {

    JLabel l1,l2,l3,l4,l5,l6;
    JFrame f = new JFrame();

    double[] val = new double[4];

    public AmperometerGui()
    {

        f.setSize(400,500);//400 width and 500 height
        f.setLayout(null);//using no layout managers
        if(ResourceBundle.getBundle("org.icar.h.sps_management.Boot").getString("sensor.actor").equals("true"))
            f.setVisible(true);//making the frame visible

        l1=new JLabel();
        l2=new JLabel();
        l3=new JLabel();
        l4=new JLabel();
        l5=new JLabel();
        l6=new JLabel();
        f.setLayout(new GridLayout(6, 1));
        f.add(l1);
        f.add(l2);
        f.add(l3);
        f.add(l4);
        f.add(l5);
        f.add(l6);
    }


    public void testGui(AmpData data)
    {

        for (int i=0;i<6;i++)
            if(data.getCurrent(i)<0)
                val[i]=0;
            else val[i]=data.getCurrent(i);


        l1.setText("Motor 1 Current: "+val[0]+" mA");
        l2.setText("Motor 2 Current: "+val[1]+" mA");
        l3.setText("Main Gen Current: "+val[2]+" mA");
        l4.setText("Load 1 Current: "+val[3]+" mA");
        l1.setText("Load 2 Current: "+val[4]+" mA");
        l2.setText("Aux Gen Current: "+val[5]+" mA");
    }

}
