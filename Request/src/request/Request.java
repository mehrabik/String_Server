package request;

import java.io.Serializable;

/**
 * Created by masoud on 4/29/14.
 */
public class Request implements Serializable{

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
        this.result = result;
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
