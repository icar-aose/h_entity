package org.icar.h.sps_management.rpi_ina219;

import org.icar.h.core.Akka2Jade;
import org.icar.h.sps_management.worker.AmpData;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;


public class FaultAmpGui extends javax.swing.JFrame {

    private JLabel amp1,amp2,amp3, amp4, amp5, amp6;

    private JLabel swm1, swm2, swmg1, swL1, swL2, swauxg1;

    private JLabel boltLabelF1,boltLabelF2;

    private JLabel swm1bus, swm2bus, swmg1bus, swL1bus, swL2bus, swauxg1bus;

    private JButton swm1busBut, swm2busBut, swmg1busBut, swL1busBut, swL2busBut, swauxg1busBut;

    private JButton swm1But, swm2But, swmg1But, swL1But, swL2But, swauxg1But;

    private JFrame f = new JFrame();

    private JButton faultF1,faultF2;
    private double[] val =new double[6];

    private URL url_lineDown = getClass().getResource("circuit_file/line_down.png");
    private File lineDown = new File(url_lineDown.getPath());
    private BufferedImage imgLineDown = ImageIO.read(lineDown);
    private ImageIcon iconLineDown = new ImageIcon(imgLineDown);


    private URL url_lineUp = getClass().getResource("circuit_file/line_up.png");
    private File lineUp = new File(url_lineUp.getPath());
    private BufferedImage imgLineUp = ImageIO.read(lineUp);
    private ImageIcon iconLineUp = new ImageIcon(imgLineUp);

    private URL url_lineRect = getClass().getResource("circuit_file/line_rect.png");
    private File lineRect = new File(url_lineRect.getPath());
    private BufferedImage imgLineRect = ImageIO.read(lineRect);
    private ImageIcon iconLineRect = new ImageIcon(imgLineRect);


    private URL url_lineOpen = getClass().getResource("circuit_file/line_open_sw.png");
    private File lineOpen = new File(url_lineOpen.getPath());
    private BufferedImage imgLineOpen = ImageIO.read(lineOpen);
    private ImageIcon iconLineOpen = new ImageIcon(imgLineOpen);

    private URL url_bolt = getClass().getResource("circuit_file/bolt.png");
    private File bolt = new File(url_bolt.getPath());
    private BufferedImage imgBolt = ImageIO.read(bolt);
    private ImageIcon iconBolt = new ImageIcon(imgBolt);



    public FaultAmpGui(final Akka2Jade bridge) throws IOException {


        Dimension size = new Dimension(100,20);
        f.setSize(1500,346);//400 width and 500 height
        //f.setLayout(null);//using no layout managers
        URL urlCircuit = getClass().getResource("circuit_file/circuit.png");
        File circuit = new File(urlCircuit.getPath());
        BufferedImage img = ImageIO.read(circuit);
        ImageIcon iconCircuit = new ImageIcon(img);
        JLabel background = new JLabel(iconCircuit);
        f.setContentPane(background);

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if(actionEvent.getActionCommand().equals("F1"))
                    if(faultF1.getBackground()==Color.GREEN)
                    {
                        faultF1.setBackground(Color.RED);
                        boltLabelF1.setIcon(iconBolt);
                        bridge.sendHead("selected_failure(switchf1)");
                        //INVIA COMANDO AL FAULTENACTOR
                    }
                    else {
                        System.out.println("close");
                        faultF1.setBackground(Color.GREEN);
                        boltLabelF1.setIcon(null);
                        bridge.sendHead("selected_failure(switchf1)");
                        //INVIA COMANDO AL FAULTENACTOR
                    }
                if(actionEvent.getActionCommand().equals("F2"))
                    if(faultF2.getBackground()==Color.GREEN)
                    {
                        faultF2.setBackground(Color.RED);
                        boltLabelF2.setIcon(iconBolt);
                        bridge.sendHead("selected_failure(switchf2)");
                        //INVIA COMANDO AL FAULTENACTOR
                    }
                    else {
                        faultF2.setBackground(Color.GREEN);
                        boltLabelF2.setIcon(null);
                        bridge.sendHead("selected_failure(switchf2)");
                        //INVIA COMANDO AL FAULTENACTOR
                    }

                    switch(actionEvent.getActionCommand())
                    {
                        case "swm1busBut":
                            bridge.sendHead("selected_switch(swm1busPin)");
                            break;
                        case "swm1But":
                            bridge.sendHead("selected_switch(swm1Pin)");
                            break;
                        case "swm2busBut":
                            bridge.sendHead("selected_switch(swm2busPin)");
                            break;
                        case "swm2But":
                            bridge.sendHead("selected_switch(swm2Pin)");
                            break;
                        case "swmg1busBut":
                            System.out.println("swmg1busBut");
                            break;
                        case "swmg1But":
                            System.out.println("swmg1But");
                            break;
                        case "swL1busBut":
                            System.out.println("swL1busBut");
                            break;
                        case "swL2busBut":
                            System.out.println("swL2busBut");
                            break;
                        case "swL1But":
                            System.out.println("swL1But");
                            break;
                        case "swL2But":
                            System.out.println("swL2But");
                            break;
                        case "swauxg1busBut":
                            System.out.println("swauxg1busBut");
                            break;
                        case "swauxg1But":
                            System.out.println("swauxg1But");
                            break;
                    }
            }
        };

        faultF1 = new JButton("F1");
        faultF1.setBackground(Color.GREEN);
        faultF1.setBounds(120, 50, 70, 30);
        faultF1.addActionListener(actionListener);
        faultF1.setOpaque(true);

        boltLabelF1 = new JLabel();
        //swm1.setBorder(border_green);
        boltLabelF1.setBounds(120,85,30,45);
        f.add(boltLabelF1);

        faultF2 = new JButton("F2");
        faultF2.setBackground(Color.green);
        faultF2.setBounds(540, 220, 70, 30);
        faultF2.addActionListener(actionListener);
        faultF2.setOpaque(true);

        boltLabelF2 = new JLabel();
        //swm1.setBorder(border_green);
        boltLabelF2.setBounds(540,175,30,45);
        f.add(boltLabelF2);

        swm1 = new JLabel(iconLineRect);
        //swm1.setBorder(border_green);
        swm1.setBounds(140,188,30,20);
        f.add(swm1);

        swm2 = new JLabel(iconLineRect);
        //swm2.setBorder(border_green);
        swm2.setBounds(370,188,30,20);
        f.add(swm2);

        swmg1 = new JLabel(iconLineRect);
        //swmg1.setBorder(border_green);
        swmg1.setBounds(630,188,30,20);
        f.add(swmg1);

        swL1 = new JLabel(iconLineRect);
        //swL1.setBorder(border_green);
        swL1.setBounds(850,188,30,20);
        f.add(swL1);

        swL2 = new JLabel(iconLineRect);
        //swL2.setBorder(border_green);
        swL2.setBounds(1090,188,30,20);
        f.add(swL2);

        swauxg1 = new JLabel(iconLineRect);
        //swauxg1.setBorder(border_green);
        swauxg1.setBounds(1310,188,30,20);
        f.add(swauxg1);

        swm1bus = new JLabel();

        //swm1bus.setOpaque(false);
        f.add(swm1bus);

        swm2bus = new JLabel();
        //swm2bus.setBorder(border_green);
        //swm2bus.setBounds(276,182,30,20);
        f.add(swm2bus);

        swmg1bus = new JLabel();
        //swmg1bus.setBorder(border_green);
        //swmg1bus.setBounds(510,180,30,20);
        f.add(swmg1bus);

        swL1bus = new JLabel();
        //swL1bus.setBorder(border_green);
        //swL1bus.setBounds(745,180,30,20);
        f.add(swL1bus);

        swL2bus = new JLabel();
        //swL2bus.setBorder(border_green);
        //swL2bus.setBounds(980,182,30,20);
        f.add(swL2bus);

        swauxg1bus = new JLabel();
        //swauxg1bus.setBorder(border_green);
        //swauxg1bus.setBounds(1210,182,30,20);
        f.add(swauxg1bus);

        swm1busBut = new JButton("swm1busBut");
        swm1busBut.setBounds(26,176,50,50);
        swm1busBut.setOpaque(false);
        swm1busBut.setBorderPainted(false);
        swm1busBut.addActionListener(actionListener);
        f.add(swm1busBut);

        swm1But = new JButton("swm1But");
        swm1But.setBounds(131,176,50,50);
        swm1But.setOpaque(false);
        swm1But.setBorderPainted(false);
        swm1But.addActionListener(actionListener);
        f.add(swm1But);

        swm2busBut = new JButton("swm2busBut");
        swm2busBut.setBounds(261,176,50,50);
        swm2busBut.setOpaque(false);
        swm2busBut.setBorderPainted(false);
        swm2busBut.addActionListener(actionListener);
        f.add(swm2busBut);

        swm2But = new JButton("swm2But");
        swm2But.setBounds(356,176,50,50);
        swm2But.setOpaque(false);
        swm2But.setBorderPainted(false);
        swm2But.addActionListener(actionListener);
        f.add(swm2But);

        swmg1busBut = new JButton("swmg1busBut");
        swmg1busBut.setBounds(491,176,50,50);
        swmg1busBut.setOpaque(false);
        swmg1busBut.setBorderPainted(false);
        swmg1busBut.addActionListener(actionListener);
        f.add(swmg1busBut);

        swmg1But = new JButton("swmg1But");
        swmg1But.setBounds(611,176,50,50);
        swmg1But.setOpaque(false);
        swmg1But.setBorderPainted(false);
        swmg1But.addActionListener(actionListener);
        f.add(swmg1But);

        swL1busBut = new JButton("swL1busBut");
        swL1busBut.setBounds(731,176,50,50);
        swL1busBut.setOpaque(false);
        swL1busBut.setBorderPainted(false);
        swL1busBut.addActionListener(actionListener);
        f.add(swL1busBut);

        swL1But = new JButton("swL1But");
        swL1But.setBounds(841,176,50,50);
        swL1But.setOpaque(false);
        swL1But.setBorderPainted(false);
        swL1But.addActionListener(actionListener);
        f.add(swL1But);

        swL2busBut = new JButton("swL2busBut");
        swL2busBut.setBounds(961,176,50,50);
        swL2busBut.setOpaque(false);
        swL2busBut.setBorderPainted(false);
        swL2busBut.addActionListener(actionListener);
        f.add(swL2busBut);

        swL2But = new JButton("swL2But");
        swL2But.setBounds(1081,176,50,50);
        swL2But.setOpaque(false);
        swL2But.setBorderPainted(false);
        swL2But.addActionListener(actionListener);
        f.add(swL2But);

        swauxg1busBut = new JButton("swauxg1busBut");
        swauxg1busBut.setBounds(1191,176,50,50);
        swauxg1busBut.setOpaque(false);
        swauxg1busBut.setBorderPainted(false);
        swauxg1busBut.addActionListener(actionListener);
        f.add(swauxg1busBut);

        swauxg1But = new JButton("swauxg1But");
        swauxg1But.setBounds(1301,176,50,50);
        swauxg1But.setOpaque(false);
        swauxg1But.setBorderPainted(false);
        swauxg1But.addActionListener(actionListener);
        f.add(swauxg1But);

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
        String[] open_switchers = new String[]{"switchsw1","switchsw2","switchsw3","switchsw4","switchswp1","switchswp2","switchswp3","switchswp4","switchswp5","switchswp6","switchswmg1"};

        double[] current = {1.13,1.13,1.13,1.13,1.13,1.13};
        AmpData data = new AmpData(current);
        FaultAmpGui gui = new FaultAmpGui(null);
        gui.updateGui(data,open_switchers);
    }


    public void updateGui(AmpData data, String[] open_switchers) {

        for (int i = 0; i < 6; i++)
            if (data.getCurrent(i) < 0)
                val[i] = 0;
            else {

                val[i] = data.getCurrent(i);
                val[i] = round(val[i], 2);
            }

        amp1.setText(val[0] + " mA");
        amp2.setText(val[1] + " mA");
        amp3.setText(val[2] + " mA");
        amp4.setText(val[3] + " mA");
        amp5.setText(val[4] + " mA");
        amp6.setText(val[5] + " mA");

        boolean sw1,sw2,sw3,sw4,swm,swaux;
        sw1 = false;
        sw2 = false;
        sw3 = false;
        sw4 = false;
        swm = false;
        swaux = false;

        for (String open_switcher : open_switchers) {
            switch (open_switcher) {
                case "switchswp1":
                    swm1bus.setIcon(iconLineUp);
                    swm1bus.setBounds(41, 182, 30, 20);
                    break;
                case "switchswp2":
                    swm2bus.setIcon(iconLineUp);
                    swm2bus.setBounds(276, 182, 30, 20);
                    break;
                case "switchswp3":
                    swmg1bus.setIcon(iconLineUp);
                    swmg1bus.setBounds(510, 180, 30, 20);
                    break;
                case "switchswp4":
                    swL1bus.setIcon(iconLineUp);
                    swL1bus.setBounds(745, 180, 30, 20);
                    break;
                case "switchswp5":
                    swL2bus.setIcon(iconLineUp);
                    swL2bus.setBounds(980, 182, 30, 20);
                    break;
                case "switchswp6":
                    swauxg1bus.setIcon(iconLineUp);
                    swauxg1bus.setBounds(1210, 182, 30, 20);
                    break;
                case "switchsws1":
                    swm1bus.setIcon(iconLineDown);
                    swm1bus.setBounds(41, 191, 30, 20);
                    break;
                case "switchsws2":
                    swm2bus.setIcon(iconLineDown);
                    swm2bus.setBounds(276, 191, 30, 20);
                    break;
                case "switchsws3":
                    swmg1bus.setIcon(iconLineDown);
                    swmg1bus.setBounds(510, 192, 30, 20);
                    break;
                case "switchsws4":
                    swL1bus.setIcon(iconLineDown);
                    swL1bus.setBounds(745, 190, 30, 20);
                    break;
                case "switchsws5":
                    swL2bus.setIcon(iconLineDown);
                    swL2bus.setBounds(980, 190, 30, 20);
                    break;
                case "switchsws6":
                    swauxg1bus.setIcon(iconLineDown);
                    swauxg1bus.setBounds(1210, 191, 30, 20);
                    break;
                case "switchswauxg1":
                    swauxg1.setIcon(iconLineOpen);
                    swauxg1.setBounds(1310, 182, 30, 20);
                    swaux = true;
                    break;
                case "switchswmg1":
                    swmg1.setIcon(iconLineOpen);
                    swmg1.setBounds(625, 181, 30, 20);
                    swm = true;
                    break;
                case "switchsw1":
                    swm1.setIcon(iconLineOpen);
                    swm1.setBounds(140, 182, 30, 20);
                    sw1 = true;
                    break;
                case "switchsw2":
                    swm2.setIcon(iconLineOpen);
                    swm2.setBounds(370, 182, 30, 20);
                    sw2 = true;
                    break;
                case "switchsw3":
                    swL1.setIcon(iconLineOpen);
                    swL1.setBounds(850, 182, 30, 20);
                    sw3 = true;
                    break;
                case "switchsw4":
                    swL2.setIcon(iconLineOpen);
                    swL2.setBounds(1090, 182, 30, 20);
                    sw4 = true;
                    break;
                   // throw new IllegalStateException("Unexpected value: " + open_switcher);
            }

        }

        if(!sw1)
        {
            swm1.setIcon(iconLineRect);
            swm1.setBounds(140,188,30,20);
        }
        if(!sw2)
        {
            swm2.setIcon(iconLineRect);
            swm2.setBounds(370,188,30,20);
        }
        if(!sw3)
        {
            swL1.setIcon(iconLineRect);
            swL1.setBounds(850,188,30,20);
        }
        if(!sw4)
        {
            swL2.setIcon(iconLineRect);
            swL2.setBounds(1090, 188, 30, 20);
        }
        if(!swaux)
        {
            swauxg1.setIcon(iconLineRect);
            swauxg1.setBounds(1310,188,30,20);

        }
        if(!swm)
        {
            swmg1.setIcon(iconLineRect);
            swmg1.setBounds(630,188,30,20);
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
