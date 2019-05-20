package org.icar.h.core.matlab.matEngine;

import com.mathworks.engine.*;
import org.icar.h.core.matlab.SimpleServer;

import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MMProcessor implements MatRemote{

    private MatlabEngine ml = null;


    protected MMProcessor()  {
        super();
    }

    public void print_hello() throws IllegalStateException {
        System.out.println("test");
    }


    public void startEngine() throws IllegalStateException, InterruptedException, ExecutionException {

        //Start MATLAB asynchronously
        Future<MatlabEngine> eng = MatlabEngine.startMatlabAsync();
        // Get engine instance
        ml = eng.get();

        String path = System.getProperty("user.dir") + "\\sps_data";
        String modelPath, model;
        String textFile = null;
        File dir = new File(path);
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith((".slx"))) {
                textFile=file.getName();
            }
        }

        model = (new File(textFile).getName()).substring(0, (new File(textFile).getName()).length() - 4);
        //System.out.println(path);
        ml.putVariable("path", path.toCharArray());
        ml.putVariable("model", model);
        ml.eval("cd(path)");
        ml.eval("startup");
        System.out.println("matlab engine started");
    }

    public HashMap<String,Double> evaluateSolution( ArrayList<String> switchers,ArrayList<String> all_switchers , ArrayList<String> open_switchers, int num_loads ) throws IllegalStateException, InterruptedException, ExecutionException {

        //intanto setto a 1 tutte le variabili, dopodiche setto a 0 quelle OPEN
        //NB: DOVRO' FARLO AD OGNI ITERAZIONE PERCHE' LA SOLUZIONE VIENE CALCOLATA CON LO STATO INIZIALE;

        System.out.println(switchers);
        System.out.println(all_switchers);
        System.out.println(open_switchers);
        for(int i=0;i<all_switchers.size();i++)
            ml.eval(all_switchers.get(i) + "=[0 1];");

        for (int i=0;i<open_switchers.size();i++)
            ml.eval(open_switchers.get(i) + "=[0 0];");

        for (int i = 0; i < switchers.size(); i = i + 2)
        {
            //System.out.println(switchers.get(i)+","+switchers.get(i+1));
            ml.eval(switchers.get(i) + "(1, 1) = 1;");
            ml.eval(switchers.get(i) + "(1, 2) = " + String.valueOf(switchers.get(i + 1))+";");
        }

       ml.eval("execute(model);");

       return this.fetchLoadAndGen();
    }

    public HashMap<String,Double> fetchLoadAndGen() {
        File name = new File(System.getProperty("user.dir") + "\\sps_data\\startup.m");
       HashMap<String, Double> results = new HashMap<String, Double>() ;

        if (name.isFile()) {
            try {
                boolean res = ml.getVariable("genResult");
                if (res)
                    results.put("genResult",1.0);
                else
                    results.put("genResult",0.0);
                int i = 1;
                BufferedReader input = new BufferedReader(new FileReader(name));
                String text;
                String[] elem = new String[2];
                while ((text = input.readLine()) != null)
                    {
                       elem = text.split("=");
                       Double val = ml.getVariable(elem[0].trim());
                       results.put(elem[0].trim(),val) ;
                       System.out.println(val);
                       i++;
                    }
                input.close();

            }
            catch (Exception Ex) {
                System.err.println(Ex);
            }
        }
        return results;
    }

    public static void main (String args[])  {

        MMProcessor obj=new MMProcessor();



        try{
            ResourceBundle properties = PropertyResourceBundle.getBundle("org.icar.h.sps_management.Boot");
            int port = Integer.parseInt(properties.getString("simulator.server.port"));
            Registry registry = LocateRegistry.createRegistry(port);

            //SimpleServer.server(registry);
            MatRemote stub = (MatRemote) UnicastRemoteObject.exportObject(obj,port);

            System.out.println(registry);
            registry.bind("MatRemote",stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}