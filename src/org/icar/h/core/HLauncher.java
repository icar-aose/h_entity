package org.icar.h.core;

import cartago.*;
import cartago.util.agent.CartagoBasicContext;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jason.mas2j.AgentParameters;
import org.icar.h.core.jason_jade.JadeAgArch;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class HLauncher {
    private ResourceBundle run=null;
    private AgentContainer ac = null;
    private List<AgentController> running_entities = new LinkedList();

    public HLauncher(String boot_properties) {
        run = ResourceBundle.getBundle(boot_properties);

        if (run != null) {
            init_jade_infrastructure();
            //init_cartago();
        }
        if (ac != null)
            create_agent_controllers();

    }

    public void start() {
        for (AgentController a : running_entities) {
            try {
                a.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }
    }

    private void init_jade_infrastructure() {
        boolean setCloseVW = Boolean.parseBoolean(getPropertyOrDefault("closeVM","true"));
        String JasonGUI = getPropertyOrDefault("jasonGUI","true");

        // Start a new JADE runtime system
        Runtime.instance().setCloseVM(setCloseVW);		//	Causes the local JVM to be closed when the last container in this JVM terminates

        // Start a main-container on the localhost
        Properties props = new ExtendedProperties();
        props.setProperty(Profile.GUI, JasonGUI);
        ProfileImpl p = new ProfileImpl(props);


        ac = Runtime.instance().createMainContainer(p); //* RMI internal Message Transport Protocol, port number 1099, HTTP MTP.
    }

    private void init_cartago() {
        try {
            NodeId node = CartagoService.startNode();
            CartagoService.installInfrastructureLayer("default");
            CartagoBasicContext my_context = new CartagoBasicContext("my_agent");
            ArtifactId my_device;
            try {
                my_device = my_context.makeArtifact("CaptainGui", "org.icar.h.sps_management.artifact.CaptainInterface");
               // my_context.doAction(my_device, new Op("welcome"));
            } catch (CartagoException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        } catch (CartagoException e) {
            e.printStackTrace();
        }
    }

    private void create_agent_controllers() {
        String[] entities = run.getString("entities").split(",");
        for (String s : entities) {
            String name = s.trim();
            //System.out.println("dealing with "+name);
            if (!run.containsKey(name + ".type"))
                System.out.println("error loading [" + name + "] because of missing type");
            else {
                String type = run.getString(name + ".type");
                if (type.equals("jason")) {
                    AgentController a = createJasonAgent(name);
                    if (a!=null)
                        running_entities.add(a);
                } else if (type.equals("jade")) {
                    AgentController a = createJadeAgent(name);
                    if (a!=null)
                        running_entities.add(a);
                } else if (type.equals("akka")) {
                    AgentController a = createActorSystem(name);
                    if (a!=null)
                        running_entities.add(a);
                } else {
                    System.out.println("error loading [" + name + "] because of unknown type");
                }
            }

        }
    }

    private AgentController createJasonAgent(String name) {
        AgentController controller = null;

        if (run.containsKey(name + ".asl")) {
            try {
                AgentParameters ap = new AgentParameters();
                ap.asSource = new File(run.getString(name + ".asl"));
                controller = ac.createNewAgent(name, JadeAgArch.class.getName(), new Object[] { ap });
                System.out.println("loaded [" + name + "] jason agent");
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("error loading [" + name + "] jason agent because of missing asl");
        }

        return controller;
    }

    private AgentController createJadeAgent(String name) {
        AgentController controller  = null;

        if (run.containsKey(name + ".class")) {
            try {
                String classname = run.getString(name + ".class");
                controller = ac.createNewAgent(name, classname , new Object[] {} );
                System.out.println("loaded [" + name + "] jade agent");
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("error loading [" + name + "] jade agent because of missing class name");
        }

        return controller;
    }

    private AgentController createActorSystem(String name) {
        AgentController controller  = null;

        if (run.containsKey(name + ".class")) {
            try {
                String classname = run.getString(name + ".class");
                controller = ac.createNewAgent(name, classname, new Object[] {} );
                System.out.println("loaded [" + name + "] actor system");
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("error loading [" + name + "] actorsystem because of missing class name");
        }

        return controller;
    }

    private String getPropertyOrDefault(String key,String def) {
        if (run.containsKey(key))
            return run.getString(key);
        else
            return def;
    }

}
