
package server;

public class DataBaseException extends Exception{
    private String details;
    
    public DataBaseException(String string) {
        super(string);
        details = string;
    }
    
    public String getExceptionInfo(){
        return details;
    }
    
}
