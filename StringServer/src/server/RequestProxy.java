package server;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by masoud on 5/18/14.
 */
public class RequestProxy {

    private RequestClassLoader reqLoader;
    private Class reqClass;
    private int index[] = new int[8];

    public RequestProxy(String filename) {

        try {
            File file = new File(filename);
            URL url = file.toURL();
            URL[] urls = new URL[]{url};
            URLClassLoader loader = new URLClassLoader(urls);
            reqLoader = new RequestClassLoader(loader);

            this.reqClass = reqLoader.loadClass("request.Request");
            setIndex();

        }catch (Exception ex){
            System.err.println("Error in loading Request.jar");
            System.exit(1);
        }
    }

    private void setIndex(){
        Method[] methods = reqClass.getDeclaredMethods();

        for(int i = 0; i < methods.length ; i++){

            if(methods[i].toString().contains("getType"))
                index[0] = i;
            else if(methods[i].toString().contains("setType"))
                index[1] = i;
            else if(methods[i].toString().contains("getStatus"))
                index[2] = i;
            else if(methods[i].toString().contains("getResult"))
                index[3] = i;
            else if(methods[i].toString().contains("setArgument"))
                index[4] = i;
            else if(methods[i].toString().contains("getArgument"))
                index[5] = i;
            else if(methods[i].toString().contains("setResult"))
                index[6] = i;
            else if(methods[i].toString().contains("setStatus"))
                index[7] = i;
        }
    }

    public Class getReqClass() {
        return reqClass;
    }

    public void setObject(Object in){
        this.setObject(in);
    }

    public void update(Socket s) throws ClassNotFoundException {
        this.reqLoader = new RequestClassLoader();
        this.reqClass = this.reqLoader.loadClass("request.Request", s);
        setIndex();
    }

    public int getType(Object inst) throws InvocationTargetException, IllegalAccessException {
        return (Integer) reqClass.getDeclaredMethods()[index[0]].invoke(inst);
    }

    public void setType(Object inst, int type) throws InvocationTargetException, IllegalAccessException {
        reqClass.getDeclaredMethods()[index[1]].invoke(inst, type);
    }

    public int getStatus(Object inst) throws InvocationTargetException, IllegalAccessException {
        return (Integer) reqClass.getDeclaredMethods()[index[2]].invoke(inst);
    }

    public String getResult(Object inst) throws InvocationTargetException, IllegalAccessException {
        return (String) reqClass.getDeclaredMethods()[index[3]].invoke(inst);
    }

    public void setArgument(Object inst, String arg) throws InvocationTargetException, IllegalAccessException {
        reqClass.getDeclaredMethods()[index[4]].invoke(inst, arg);
    }

    public String getArgument(Object inst) throws InvocationTargetException, IllegalAccessException {
        return (String) reqClass.getDeclaredMethods()[index[5]].invoke(inst);
    }

    public void setResult(Object inst, String arg) throws InvocationTargetException, IllegalAccessException {
        reqClass.getDeclaredMethods()[index[6]].invoke(inst, arg);
    }

    public void setStatus(Object inst, int status) throws InvocationTargetException, IllegalAccessException {
        reqClass.getDeclaredMethods()[index[7]].invoke(inst, status);
    }
}
