package org.icar.h.sps_management.rpi_ina219;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class  SelectedSolutionGUI extends javax.swing.JFrame{

    private JLabel swm1bus, swm2bus, swmg1bus, swL1bus, swL2bus, swauxg1bus;
    private JLabel swm1, swm2, swmg1, swL1, swL2, swauxg1;


    private URL url_lineDown = getClass().getResource("circuit_file/line_down_small_red.png");
    private File lineDown = new File(url_lineDown.getPath());
    private BufferedImage imgLineDown = ImageIO.read(lineDown);
    private ImageIcon iconLineDown = new ImageIcon(imgLineDown);


    private URL url_lineUp = getClass().getResource("circuit_file/line_up_small_red.png");
    private File lineUp = new File(url_lineUp.getPath());
    private BufferedImage imgLineUp = ImageIO.read(lineUp);
    private ImageIcon iconLineUp = new ImageIcon(imgLineUp);

    private URL url_lineRect = getClass().getResource("circuit_file/line_rect_small_red.png");
    private File lineRect = new File(url_lineRect.getPath());
    private BufferedImage imgLineRect = ImageIO.read(lineRect);
    private ImageIcon iconLineRect = new ImageIcon(imgLineRect);


    private URL url_lineOpen = getClass().getResource("circuit_file/line_open_sw_small_red.png");
    private File lineOpen = new File(url_lineOpen.getPath());
    private BufferedImage imgLineOpen = ImageIO.read(lineOpen);
    private ImageIcon iconLineOpen = new ImageIcon(imgLineOpen);


    private JFrame f = new JFrame();

    public  SelectedSolutionGUI() throws IOException {


        Dimension size = new Dimension(100, 20);
        f.setSize(750, 250);//400 width and 500 height
        //f.setLayout(null);//using no layout managers
        URL urlCircuit = getClass().getResource("circuit_file/circuit_small.png");
        File circuit = new File(urlCircuit.getPath());
        BufferedImage img = ImageIO.read(circuit);
        ImageIcon iconCircuit = new ImageIcon(img);
        JLabel background = new JLabel(iconCircuit);
        f.setContentPane(background);

        swm1 = new JLabel();
        f.add(swm1);

        swm2 = new JLabel();
        f.add(swm2);

        swmg1 = new JLabel();
        f.add(swmg1);

        swL1 = new JLabel();
        f.add(swL1);

        swL2 = new JLabel();
        f.add(swL2);

        swauxg1 = new JLabel();
        f.add(swauxg1);

        swm1bus = new JLabel();
        f.add(swm1bus);

        swm2bus = new JLabel();
        f.add(swm2bus);

        swmg1bus = new JLabel();
        f.add(swmg1bus);

        swL1bus = new JLabel();
        f.add(swL1bus);

        swL2bus = new JLabel();
        f.add(swL2bus);

        swauxg1bus = new JLabel();
        f.add(swauxg1bus);



        f.setVisible(false);
    }


    public static void main(String[] args) throws IOException {

        SelectedSolutionGUI test = new SelectedSolutionGUI();

        String sol = "solution_1: sol[\"CLOSE_switchswmg1\",\"OPEN_switchswp6_&_CLOSE_switchsws4\",\"OPEN_switchswp3_&_OPEN_switchsws3\",\"OPEN_switchswauxg1\"]";


        test.SelectSol(sol);


    }

    public void hide_frame ()
    {
         f.setVisible(false);
    }


    public void SelectSol(String sol)
    {
        ArrayList<String> sol_list = new ArrayList<>();

        //System.out.println(sol);
        sol_list=SolParser(sol);

        swm1.setIcon(null);
        swm1bus.setIcon(null);
        swm2.setIcon(null);
        swm2bus.setIcon(null);
        swL1.setIcon(null);
        swL1bus.setIcon(null);
        swL2.setIcon(null);
        swL2bus.setIcon(null);
        swmg1.setIcon(null);
        swmg1bus.setIcon(null);
        swauxg1.setIcon(null);
        swauxg1bus.setIcon(null);

        for(int i=1;i<sol_list.size();i=i+2)
        {
            //System.out.println(sol_list.get(i));
            switch(sol_list.get(i)){
                    case "switchswp1":
                        if(sol_list.get(i-1).equals("OPEN")) {
                            swm1bus.setIcon(iconLineUp);
                            swm1bus.setBounds(41, 92, 30, 20);
                        }
                        else {
                            swm1bus.setIcon(iconLineDown);
                            swm1bus.setBounds(39, 98, 30, 20);
                        }
                        break;
                    case "switchswp2":
                        if(sol_list.get(i-1).equals("OPEN")) {
                            swm2bus.setIcon(iconLineUp);
                            swm2bus.setBounds(162, 92, 30, 20);
                        }
                        else {
                            swm2bus.setIcon(iconLineDown);
                            swm2bus.setBounds(162, 98, 30, 20);
                        }
                        break;
                    case "switchswp3":
                        if(sol_list.get(i-1).equals("OPEN")) {
                            swmg1bus.setIcon(iconLineUp);
                            swmg1bus.setBounds(290, 92, 30, 20);
                        }
                        else{
                            swmg1bus.setIcon(iconLineDown);
                            swmg1bus.setBounds(292, 98, 30, 20);
                        }

                        break;
                    case "switchswp4":
                        if(sol_list.get(i-1).equals("OPEN")) {
                            swL1bus.setIcon(iconLineUp);
                            swL1bus.setBounds(412, 90, 30, 20);
                        }
                        else {

                            swL1bus.setIcon(iconLineDown);
                            swL1bus.setBounds(412, 98, 30, 20);
                        }
                        break;
                    case "switchswp5":
                        if(sol_list.get(i-1).equals("OPEN")) {
                            swL2bus.setIcon(iconLineUp);
                            swL2bus.setBounds(540, 92, 30, 20);
                        }
                        else {

                            swL2bus.setIcon(iconLineDown);
                            swL2bus.setBounds(538, 98, 30, 20);
                        }
                        break;
                    case "switchswp6":
                        if(sol_list.get(i-1).equals("OPEN")) {
                            swauxg1bus.setIcon(iconLineUp);
                            swauxg1bus.setBounds(660, 92, 30, 20);
                        }
                        else {

                            swauxg1bus.setIcon(iconLineDown);
                            swauxg1bus.setBounds(660, 98, 30, 20);
                        }
                        break;
                    case "switchsws1":
                        if(sol_list.get(i-1).equals("OPEN")) {
                            swm1bus.setIcon(iconLineDown);
                            swm1bus.setBounds(39, 98, 30, 20);
                        }
                        else {

                            swm1bus.setIcon(iconLineUp);
                            swm1bus.setBounds(41, 92, 30, 20);
                        }
                        break;
                    case "switchsws2":
                        if(sol_list.get(i-1).equals("OPEN")) {
                            swm2bus.setIcon(iconLineDown);
                            swm2bus.setBounds(162, 98, 30, 20);
                        }
                        else {
                            swm2bus.setIcon(iconLineUp);
                            swm2bus.setBounds(162, 92, 30, 20);
                        }
                        break;
                    case "switchsws3":
                        if(sol_list.get(i-1).equals("OPEN")) {
                            swmg1bus.setIcon(iconLineDown);
                            swmg1bus.setBounds(292, 98, 30, 20);
                        }
                        else {
                            swmg1bus.setIcon(iconLineUp);
                            swmg1bus.setBounds(290, 92, 30, 20);
                        }
                        break;
                    case "switchsws4":
                        if(sol_list.get(i-1).equals("OPEN")) {
                            swL1bus.setIcon(iconLineDown);
                            swL1bus.setBounds(412, 98, 30, 20);
                        }
                        else {
                            swL1bus.setIcon(iconLineUp);
                            swL1bus.setBounds(412, 90, 30, 20);
                        }
                        break;
                    case "switchsws5":
                        if(sol_list.get(i-1).equals("OPEN")) {
                            swL2bus.setIcon(iconLineDown);
                            swL2bus.setBounds(538, 98, 30, 20);
                        }
                        else {

                            swL2bus.setIcon(iconLineUp);
                            swL2bus.setBounds(540, 92, 30, 20);
                        }
                        break;
                    case "switchsws6":
                        if(sol_list.get(i-1).equals("OPEN")) {
                            swauxg1bus.setIcon(iconLineDown);
                            swauxg1bus.setBounds(660, 98, 30, 20);
                        }
                        else {
                            swauxg1bus.setIcon(iconLineUp);
                            swauxg1bus.setBounds(660, 92, 30, 20);
                        }
                        break;
                    case "switchswauxg1":
                        if(sol_list.get(i-1).equals("OPEN")) {
                            swauxg1.setIcon(iconLineOpen);
                            swauxg1.setBounds(715, 90, 30, 20);
                        }
                        else {

                            swauxg1.setIcon(iconLineRect);
                            swauxg1.setBounds(715, 94, 30, 20);
                        }
                        break;
                    case "switchswmg1":
                        if(sol_list.get(i-1).equals("OPEN")) {
                            swmg1.setIcon(iconLineOpen);
                            swmg1.setBounds(350, 90, 30, 20);
                        }
                        else {

                            swmg1.setIcon(iconLineRect);
                            swmg1.setBounds(350, 95, 30, 20);
                        }
                        break;
                    case "switchsw1":
                        if(sol_list.get(i-1).equals("OPEN")) {
                            swm1.setIcon(iconLineOpen);
                            swm1.setBounds(90, 90, 30, 20);
                        }
                        else {

                            swm1.setIcon(iconLineRect);
                            swm1.setBounds(90, 95, 30, 20);
                        }
                        break;
                    case "switchsw2":
                        if(sol_list.get(i-1).equals("OPEN")) {
                            swm2.setIcon(iconLineOpen);
                            swm2.setBounds(210, 90, 30, 20);
                        }
                        else {
                            swm2.setIcon(iconLineRect);
                            swm2.setBounds(210, 95, 30, 20);
                        }
                        break;
                    case "switchsw3":
                        if(sol_list.get(i-1).equals("OPEN")) {
                            swL1.setIcon(iconLineOpen);
                            swL1.setBounds(467, 90, 30, 20);
                        }
                        else {
                            swL1.setIcon(iconLineRect);
                            swL1.setBounds(467, 94, 30, 20);
                        }
                        break;
                    case "switchsw4":
                        if(sol_list.get(i-1).equals("OPEN")) {
                            swL2.setIcon(iconLineOpen);
                            swL2.setBounds(600, 90, 30, 20);
                        }
                        else {
                            swL2.setIcon(iconLineRect);
                            swL2.setBounds(600, 94, 30, 20);
                        }

                        break;
                    // throw new IllegalStateException("Unexpected value: " + open_switcher);
                }
            }


        //swm1bus.setIcon(iconLineUp);
        //swm1bus.setBounds(39, 92, 30, 20);
        f.setLocation(10, 500);
        f.setVisible(true);
        f.pack();
    }



    public ArrayList SolParser (String sol)
    {
        String[] sol_array;
        ArrayList<String> sol_list = new ArrayList<>();
        ArrayList<String> sol_list_2 = new ArrayList<>();

        for(int i=0; i< sol.length();i++)
        {
            if(sol.charAt(i)=='[')
            {
                sol=sol.substring(i);
            }
        }

        sol_array = sol.split("\"");


        for(int i=1 ; i< sol_array.length; i=i+2)
        {
            sol_list.add(sol_array[i]);
        }


        for(int i=0 ; i< sol_list.size(); i=i+1)
        {
            sol_array=sol_list.get(i).split("_");
            sol_list_2.add(sol_array[0]);
            sol_list_2.add(sol_array[1]);

        }


        return sol_list_2;
    }
}