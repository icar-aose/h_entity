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


public class FaultAmpGui {

    private JLabel amp1,amp2,amp3, amp4, amp5, amp6;

    private JLabel swm1, swm2, swmg1, swL1, swL2, swauxg1;

    private JLabel swm1bus, swm2bus, swmg1bus, swL1bus, swL2bus, swauxg1bus;

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
        //swm1bus.setBounds(41,182,30,20);
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
                    break;
                case "switchswmg1":
                    swmg1.setIcon(iconLineOpen);
                    swmg1.setBounds(625, 181, 30, 20);
                    break;
                case "switchsw1":
                    swm1.setIcon(iconLineOpen);
                    swm1.setBounds(140, 182, 30, 20);
                    break;
                case "switchsw2":
                    swm2.setIcon(iconLineOpen);
                    swm2.setBounds(370, 182, 30, 20);
                    break;
                case "switchsw3":
                    swL1.setIcon(iconLineOpen);
                    swL1.setBounds(850, 182, 30, 20);
                    break;
                case "switchsw4":
                    swL2.setIcon(iconLineOpen);
                    swL2.setBounds(1090, 182, 30, 20);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + open_switcher);
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
