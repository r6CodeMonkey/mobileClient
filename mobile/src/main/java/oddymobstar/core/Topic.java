package oddymobstar.core;

/**
 * Created by root on 25/02/15.
 */
public class Topic {

    private String key = "";
    private String name = "";

    public Topic(){

    }

    public void setKey(String key){
        this.key = key;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getKey(){
        return key;
    }

    public String getName(){
        return name;
    }

}
