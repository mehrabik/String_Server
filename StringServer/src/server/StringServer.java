package server;

import java.awt.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by masoud on 4/29/14.
 */
public class StringServer implements Runnable {

    private static Properties prop;
    private static RequestProxy proxy;
    private static String password;
    private static String flag;
    private Socket csocket;
    private boolean updated;

    StringServer(Socket csocket, String password) {
        this.csocket = csocket; this.password = password;
    }


    public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {

        loadStrings();

        if(args.length != 3){

            System.out.println(prop.getProperty("usage"));
            return;
        }

        flag = args[2];
        proxy = new RequestProxy("Request.jar");

        ServerSocket ssock;

        try {

            ssock = new ServerSocket(Integer.parseInt(args[0]));
            System.out.println("Listening on port " + ssock.getLocalPort() + "\n");

            while (true) {
                Socket sock = ssock.accept();
                System.out.println(sock.getInetAddress().getHostAddress() +":" + sock.getPort() + " Connected");
                new Thread(new StringServer(sock, args[1])).start();
                //new StringServer(sock).run();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        try {

            while(true) {

                ObjectInputStream in = new ObjectInputStream(csocket.getInputStream());
                Object inst = in.readObject();

                if (proxy.getArgument(inst).equals("") || (proxy.getType(inst) > 5) || (proxy.getType(inst) < 1)) {
                    proxy.setResult(inst, prop.getProperty("error") + "\n" + prop.getProperty("secret"));
                    proxy.setStatus(inst, 1);
                } else {
                    proxy.setStatus(inst, 0);
                    switch (proxy.getType(inst)) {
                        case 1:
                            proxy.setResult(inst, new StringBuilder(proxy.getArgument(inst)).reverse().toString());
                            break;
                        case 2:
                            proxy.setResult(inst, proxy.getArgument(inst).toUpperCase());
                            break;
                        case 3:
                            proxy.setResult(inst, proxy.getArgument(inst).toLowerCase());
                            break;
                        case 4:
                            if (update(inst) == true) {
                                updated = true;
                                inst = proxy.getReqClass().newInstance();
                                proxy.setStatus(inst, 0);
                                proxy.setResult(inst, prop.getProperty("updateOK") + "\nFlag1: " + flag + "\n");
                            } else {
                                proxy.setResult(inst, prop.getProperty("updateFailed"));
                                proxy.setStatus(inst, 1);
                            }
                            break;
                        case 5:
                            proxy.setResult(inst, prop.getProperty("reset"));
                            ObjectOutputStream out = new ObjectOutputStream(csocket.getOutputStream());
                            out.writeObject(inst);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.exit(1);
                            break;
                    }
                }

                ObjectOutputStream out = new ObjectOutputStream(csocket.getOutputStream());
                out.writeObject(inst);

                if(updated == true){
                    proxy = new RequestProxy("Request.jar");
                    updated = false;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } finally {
            try {
                csocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean update(Object inst) throws InvocationTargetException, IllegalAccessException {

        if (proxy.getArgument(inst).equals(this.password) == false){
            return false;
        }

        try{
            proxy.update(this.csocket);
        }catch (Exception ex){
            return false;
        }

        return true;
    }

    private static void loadStrings() {

        prop = new Properties();
        InputStream input = null;

        try {

            //input = new FileInputStream(StringServer.class.getResource("strings.properties").getPath());
            input = StringServer.class.getResourceAsStream("strings.properties");

            // load a properties file
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void restart() {
        StringBuilder cmd = new StringBuilder();
        cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ");
        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            cmd.append(jvmArg + " ");
        }
        cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
        cmd.append(Window.class.getName()).append(" ");

        try {
            Runtime.getRuntime().exec(cmd.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void restartApplication() throws URISyntaxException, IOException {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(StringServer.class.getProtectionDomain().getCodeSource().getLocation().toURI());


        if(!currentJar.getName().endsWith(".jar"))
            return;

        final ArrayList<String> command = new ArrayList<String>();
        command.add(javaBin);
        command.add("-jar");
        command.add(currentJar.getPath());

        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
        System.exit(0);
    }
}
