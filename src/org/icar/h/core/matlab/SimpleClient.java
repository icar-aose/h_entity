package org.icar.h.core.matlab;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.Naming;
import java.rmi.registry.Registry;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class SimpleClient {
    final public static int BUF_SIZE = 1024 * 64;

    /**
     * Copy the input stream to the output stream
     *
     * @param in
     * @param out
     * @throws IOException
     */
    public static void copy(InputStream in, OutputStream out)
            throws IOException {

        byte[] b = new byte[BUF_SIZE];
        int len;
        while ((len = in.read(b)) >= 0) {
            out.write(b, 0, len);
        }
        in.close();
        out.close();
    }

    public static void upload(Simple server, File src, File dest) throws IOException {

        copy(new FileInputStream(src), server.getOutputStream(dest));
    }

    public static void download(Simple server, File src, File dest) throws IOException {
        copy(server.getInputStream(src), new FileOutputStream(dest));
    }

    public static void transfer() throws Exception {

        ResourceBundle properties = PropertyResourceBundle.getBundle("org.icar.h.core.matlab.Simple");

        String serverIP = properties.getString("simulator.server.ip");
        int port = Integer.parseInt(properties.getString("simulator.server.port"));
        System.out.println(port);
        String files = properties.getString("simulator.script.files");
        String path = properties.getString("simulator.script.path");
        String[] file = files.split(",");


        try{
            Simple server = (Simple) Naming.lookup("//" +
                    serverIP +
                    ":" + port +
                    "/SimpleServer");

            for(int i=0;i<file.length;i++)
                upload(server, new File(path+file[i].trim()), new File(path+file[i].trim()));
        }catch (Exception e)
        {
            e.printStackTrace();
        }


        }
    }
