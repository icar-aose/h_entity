package org.icar.h.sps_management.rpi_ina219;

import org.icar.h.core.Akka2Jade;
import org.icar.h.sps_management.worker.AmpData;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class FaultAmpGui {

    JLabel amp1,amp2,amp3, amp4, amp5, amp6;

    JLabel swm1, swm2, swmg1, swL1, swL2, swauxg1;

    JLabel swm1bus, swm2bus, swmg1bus, swL1bus, swL2bus, swauxg1bus;


    Border border_green = BorderFactory.createLineBorder(Color.GREEN, 50);

    Border border_red = BorderFactory.createLineBorder(Color.RED, 50);

    JFrame f = new JFrame();
    Dimension size = new Dimension(100,20);

    JButton faultF1,faultF2;
    double[] val =new double[6];

    public FaultAmpGui(final Akka2Jade bridge) throws IOException {

        f.setSize(1500,346);//400 width and 500 height
        //f.setLayout(null);//using no layout managers
        BufferedImage img = ImageIO.read(new File("/Users/giovannirenda/Documents/GitHub/h_entity/src/org/icar/h/sps_management/rpi_ina219/circuit.png"));
        ImageIcon imgI = new ImageIcon(img);
        JLabel x = new JLabel(imgI);
        f.setContentPane(x);
        f.pack();

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println(actionEvent.getActionCommand());
                if(actionEvent.getActionCommand().equals("F1"))
                    if(faultF1.getBackground()==Color.GREEN)
                    {
                        faultF1.setBackground(Color.RED);
                        //System.out.println("invio messaggio switchf1");
                        bridge.sendHead("selected_failure(switchf1)");
                        //INVIA COMANDO AL FAULTENACTOR
                    }
                    else {
                        faultF1.setBackground(Color.GREEN);
                        bridge.sendHead("selected_failure(switchf1)");
                        //INVIA COMANDO AL FAULTENACTOR
                    }
                if(actionEvent.getActionCommand().equals("F2"))
                    if(faultF2.getBackground()==Color.GREEN)
                    {
                        faultF2.setBackground(Color.RED);
                        bridge.sendHead("selected_failure(switchf2)");
                        //INVIA COMANDO AL FAULTENACTOR
                    }
                    else {
                        faultF2.setBackground(Color.GREEN);
                        bridge.sendHead("selected_failure(switchf2)");
                        //INVIA COMANDO AL FAULTENACTOR
                    }
            }
        };

        faultF1 = new JButton("F1");
        faultF1.setBackground(Color.GREEN);
        faultF1.setBounds(120, 50, 70, 30);
        faultF1.addActionListener(actionListener);
        faultF1.setOpaque(true);

        faultF2 = new JButton("F2");
        faultF2.setBackground(Color.green);
        faultF2.setBounds(540, 220, 70, 30);
        faultF2.addActionListener(actionListener);
        faultF2.setOpaque(true);

        swm1 = new JLabel();
        swm1.setBorder(border_green);
        swm1.setBounds(140,205,30,20);
        f.add(swm1);

        swm2 = new JLabel();
        swm2.setBorder(border_green);
        swm2.setBounds(370,205,30,20);
        f.add(swm2);

        swmg1 = new JLabel();
        swmg1.setBorder(border_green);
        swmg1.setBounds(630,205,30,20);
        f.add(swmg1);

        swL1 = new JLabel();
        swL1.setBorder(border_green);
        swL1.setBounds(850,205,30,20);
        f.add(swL1);

        swL2 = new JLabel();
        swL2.setBorder(border_green);
        swL2.setBounds(1090,205,30,20);
        f.add(swL2);

        swauxg1 = new JLabel();
        swauxg1.setBorder(border_green);
        swauxg1.setBounds(1310,205,30,20);
        f.add(swauxg1);

        swm1bus = new JLabel();
        swm1bus.setBorder(border_green);
        swm1bus.setBounds(10,185,30,20);
        f.add(swm1bus);

        swm2bus = new JLabel();
        swm2bus.setBorder(border_green);
        swm2bus.setBounds(240,185,30,20);
        f.add(swm2bus);

        swmg1bus = new JLabel();
        swmg1bus.setBorder(border_green);
        swmg1bus.setBounds(470,185,30,20);
        f.add(swmg1bus);

        swL1bus = new JLabel();
        swL1bus.setBorder(border_green);
        swL1bus.setBounds(710,185,30,20);
        f.add(swL1bus);

        swL2bus = new JLabel();
        swL2bus.setBorder(border_green);
        swL2bus.setBounds(940,185,30,20);
        f.add(swL2bus);

        swauxg1bus = new JLabel();
        swauxg1bus.setBorder(border_green);
        swauxg1bus.setBounds(1170,185,30,20);
        f.add(swauxg1bus);

        amp1 =new JLabel(val[0]+" mA");
        // size = amp1.getPreferredSize();
        amp1.setBounds(70, 160, size.width, size.height);

        amp2=new JLabel(val[1]+" mA");
        //size = l2.getPreferredSize();
        amp2.setBounds(300, 160, size.width, size.height);

        amp3 =new JLabel(val[2]+" mA");
        //size = amp3.getPreferredSize();
        amp3.setBounds(570, 160, size.width, size.height);

        amp4 =new JLabel(val[3]+" mA");
        //size = amp4.getPreferredSize();
        amp4.setBounds(785, 160, size.width, size.height);

        amp5 =new JLabel(val[4]+" mA");
        //size = amp5.getPreferredSize();
        amp5.setBounds(1020, 160, size.width, size.height);

        amp6 =new JLabel(val[5]+" mA");
        // size = amp6.getPreferredSize();
        amp6.setBounds(1250, 160, size.width, size.height);


        f.add(amp1);
        f.add(amp2);
        f.add(amp3);
        f.add(amp4);
        f.add(amp5);
        f.add(amp6);
        f.add(faultF1,BorderLayout.NORTH);
        f.add(faultF2,BorderLayout.NORTH);

        f.setLocation(10,10);
        //if(ResourceBundle.getBundle("org.icar.h.sps_management.Boot").getString("sensor.actor").equals("true"))
        f.setVisible(true);//making the frame visible
    }

    public JFrame getFrame(){
        return f;
    }

    public static void main (String[] args) throws IOException {

        // FaultAmpGui gui = new FaultAmpGui();
    }


    public void updateGui(AmpData data, String[] open_switchers) {

        for (int i = 0; i < 6; i++)
            if (data.getCurrent(i) < 0)
                val[i] = 0;
            else {

                val[i] = data.getCurrent(i);
                val[i] = round(val[i], 2);
                //System.out.println(val[i]);
            }

        amp1.setText(val[0] + " mA");
        amp2.setText(val[1] + " mA");
        amp3.setText(val[2] + " mA");
        amp4.setText(val[3] + " mA");
        amp5.setText(val[4] + " mA");
        amp6.setText(val[5] + " mA");

        for (int i = 0; i < open_switchers.length; i++) {
            switch (open_switchers[i]) {
                case "switchswp1":
                    swm1bus.setBorder(border_green);
                    break;
                case "switchswp2":
                    swm2bus.setBorder(border_green);
                    break;
                case "switchswp3":
                    swmg1bus.setBorder(border_green);
                    break;
                case "switchswp4":
                    swL1bus.setBorder(border_green);
                    break;
                case "switchswp5":
                    swL2bus.setBorder(border_green);
                    break;
                case "switchswp6":
                    swauxg1bus.setBorder(border_green);
                    break;
                case "switchsws1":
                    swm1bus.setBorder(border_red);
                    break;
                case "switchsws2":
                    swm2bus.setBorder(border_red);
                    break;
                case "switchsws3":
                    swmg1bus.setBorder(border_red);
                    break;
                case "switchsws4":
                    swL1bus.setBorder(border_red);
                    break;
                case "switchsws5":
                    swL2bus.setBorder(border_red);
                    break;
                case "switchsws6":
                    swauxg1bus.setBorder(border_red);
                    break;
                case "switchswauxg1":
                    swauxg1.setBorder(border_red);
                    break;
                case "switchswmg1":
                    swmg1.setBorder(border_red);
                    break;
                case "switchsw1":
                    swm1.setBorder(border_red);
                    break;
                case "switchsw2":
                    swm2.setBorder(border_red);
                    break;
                case "switchsw3":
                    swL1.setBorder(border_red);
                    break;
                case "switchsw4":
                    swL2.setBorder(border_red);
                    break;
                    //switchsw5
                case "switchsw6":
                    swauxg1.setBorder(border_red);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + open_switchers[i]);
            }

        }
    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

}
