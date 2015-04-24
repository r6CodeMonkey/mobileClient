package oddymobstar.model;

/**
 * Created by root on 25/02/15.
 */
public class Config {

    private int id;
    private String name;
    private String value;


    public Config(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Config(int id, String name, String value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
