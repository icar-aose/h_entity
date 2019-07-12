package org.icar.h.sps_management.rpi_ina219;

import org.icar.h.sps_management.worker.AmpData;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AmperometerGui {

    JLabel l1,l2,l3,l4,l5,l6;
    JFrame f = new JFrame();
    Dimension size = new Dimension(100,20);

    double[] val =new double[6];

    public AmperometerGui() throws IOException {

        f.setSize(1500,346);//400 width and 500 height
        //f.setLayout(null);//using no layout managers

        BufferedImage img = ImageIO.read(new File("/Users/giovannirenda/Documents/GitHub/h_entity/src/org/icar/h/sps_management/rpi_ina219/circuit.png"));
        ImageIcon imgI = new ImageIcon(img);
        JLabel x = new JLabel(imgI);
        f.setContentPane(x);
        f.pack();

        l1=new JLabel(val[0]+" mA");
       // size = l1.getPreferredSize();
        l1.setBounds(70, 160, size.width, size.height);

        l2=new JLabel(val[1]+" mA");
        //size = l2.getPreferredSize();
        l2.setBounds(300, 160, size.width, size.height);

        l3=new JLabel(val[2]+" mA");
        //size = l3.getPreferredSize();
        l3.setBounds(570, 160, size.width, size.height);

        l4=new JLabel(val[3]+" mA");
        //size = l4.getPreferredSize();
        l4.setBounds(785, 160, size.width, size.height);

        l5=new JLabel(val[4]+" mA");
        //size = l5.getPreferredSize();
        l5.setBounds(1020, 160, size.width, size.height);

        l6=new JLabel(val[5]+" mA");
       // size = l6.getPreferredSize();
        l6.setBounds(1250, 160, size.width, size.height);


        f.add(l1);
        f.add(l2);
        f.add(l3);
        f.add(l4);
        f.add(l5);
        f.add(l6);

        f.setLocation(10,10);
        //if(ResourceBundle.getBundle("org.icar.h.sps_management.Boot").getString("sensor.actor").equals("true"))
        f.setVisible(true);//making the frame visible
    }

    public JFrame getFrame(){
        return f;
    }

    public static void main (String[] args) throws IOException {

        AmperometerGui gui = new AmperometerGui();
    }


    public void testGui(AmpData data)
    {

        for (int i=0;i<6;i++)
            if(data.getCurrent(i)<0)
                val[i]=0;
            else {

                val[i]=data.getCurrent(i);
                val[i]=round(val[i],2);
                //System.out.println(val[i]);
            }

        l1.setText(val[0] + " mA");
        l2.setText(val[1] + " mA");
        l3.setText(val[2] + " mA");
        l4.setText(val[3] + " mA");
        l5.setText(val[4] + " mA");
        l6.setText(val[5] + " mA");

    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
