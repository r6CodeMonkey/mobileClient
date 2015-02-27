package oddymobstar.util;

import android.database.Cursor;

import java.util.HashMap;
import java.util.Map;

import oddymobstar.core.Config;
import oddymobstar.database.DBHelper;

/**
 * Created by root on 27/02/15.
 */
public class Configuration {

    /*
    we hold the static configs here.  we hold them in database as there values can change
     */
    public final static String PORT = "PORT";
    public final static String URL = "URL";

    public final static String UUID_ALGORITHM = "UUID_ALGO";
    /*
      we will also need other configs, ie security types...enough to get going tho.
     */
    private Map<String, Config> configs = new HashMap<String, Config>();

    public Configuration(Cursor cursor){

        /*
          load whatever configs we have.  we need to add in some base ones on the create method.
         */
        while(cursor.moveToNext()){
            Config config = new Config(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.CONFIG_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.CONFIG_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.CONFIG_VALUE)));
            configs.put(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.CONFIG_NAME)), config);
        }

        cursor.close();


    }

    public Map<String, Config> getConfigs(){return configs;}
    public Config getConfig(String key){return configs.get(key);}
}
