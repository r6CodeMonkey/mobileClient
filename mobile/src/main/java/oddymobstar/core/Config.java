package oddymobstar.core;

/**
 * Created by root on 25/02/15.
 */
public class Config {

    private int id;
    private String name;
    private String value;

    public Config(){

    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setValue(String value){
        this.value = value;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getValue(){
        return value;
    }
}
