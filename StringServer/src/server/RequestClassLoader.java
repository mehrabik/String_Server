package server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by masoud on 5/7/14.
 */
public class RequestClassLoader extends ClassLoader {


    public RequestClassLoader() {
    }

    public RequestClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class loadClass(String name) throws ClassNotFoundException {

        if(!"request.Request".equals(name))
            return super.loadClass(name);

        return this.getParent().loadClass("request.Request");
    }

    //public Class loadClass(String name, String serverIP, int serverPort) throws ClassNotFoundException {
    public Class loadClass(String name, Socket s) throws ClassNotFoundException {
        if(!"request.Request".equals(name))
            return super.loadClass(name);

        try {
            InputStream input = s.getInputStream();

            int size = input.available();
            byte[] buff = new byte[size];
            input.read(buff, 0, size);

            return defineClass("request.Request",
                    buff, 0, buff.length);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}