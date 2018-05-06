package client;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;
import request.Request;


/**
 * Created by masoud on 4/29/14.
 */
public class StringClient {

    private static Properties prop;

    public static void main(String args[]){

        loadStrings();

        if(args.length != 2){
            System.out.print(prop.getProperty("usage"));
            return;
        }

        //Preparation
        Scanner scan = new Scanner(System.in);
        Request request;

        //Print welcome
        System.out.println(prop.getProperty("welcome"));

        //Main loop
        while(true){
            //Build request
            request = new Request();

            System.out.print(prop.getProperty("main") + " ");
            request.setType(scan.nextInt());

            System.out.print(prop.getProperty("argument") + " ");
            request.setArgument(scan.next());

            //Creating socket
            Socket s = null;
            try {
                s = new Socket(args[0], Integer.parseInt(args[1]));
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                //Write object
                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                out.writeObject(request);
                //out.flush();

                //Read object
                ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                request = (Request)in.readObject();

                //Print result
                if(request.getStatus() == 0){

                    System.out.print(prop.getProperty("success"));
                    System.out.print(prop.getProperty("result") + " ");
                    System.out.println(" " + request.getResult());
                    System.out.println("\n");
                }else{
                    System.out.print(prop.getProperty("fail"));
                    System.out.print(prop.getProperty("error") + " ");
                    System.out.println(" " + request.getResult());
                    System.out.println("\n");
                }
            } catch (IOException e) {

                System.out.print(prop.getProperty("connect"));
                e.printStackTrace();

            } catch (ClassNotFoundException e) {

                System.out.print(prop.getProperty("malform"));
                e.printStackTrace();
            }
        }
    }

    private static void loadStrings() {

        prop = new Properties();
        InputStream input = null;

        try {

            input = StringClient.class.getResourceAsStream("strings.properties");

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
}
