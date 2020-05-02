package server;

public class IncorrectDataException extends Exception {

    private String details;

    public IncorrectDataException(String string) {
        super(string);
        details = string;
    }

    public String getExceptionInfo() {
        return details;
    }

}
