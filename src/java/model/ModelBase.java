package model;

public abstract class ModelBase {

    protected int id;

    public String forSql() {
        return null;
    }

    public boolean isNullOrEmpty(String str) {
        if (str != null && !str.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean isNullOrEmpty(Integer number) {
        if (number != null && number != 0) {
            return false;
        }
        return true;
    }

    public boolean isNullOrEmpty(Double number) {
        if (number != null && number != 0.0) {
            return false;
        }
        return true;
    }

    protected String buildString(String str, boolean includeComma) {
        return "'"+str+"'" + (includeComma ? ", " : " ");
    }

    protected String buildString(long str, boolean includeComma) {
        return str + (includeComma ? ", " : " ");
    }

    protected String buildString(int str, boolean includeComma) {
        return str + (includeComma ? ", " : " ");
    }

    protected String buildString(double str, boolean includeComma) {
        return str + (includeComma ? ", " : " ");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
