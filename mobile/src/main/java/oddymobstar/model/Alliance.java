package oddymobstar.model;

import android.database.Cursor;

import oddymobstar.database.DBHelper;

/**
 * Created by root on 25/02/15.
 */
public class Alliance {

    private String key = "";
    private String name = "";

    public Alliance() {

    }

    public Alliance(Cursor alliance) {
        setKey(alliance.getString(alliance.getColumnIndexOrThrow(DBHelper.ALLIANCE_KEY)));
        setName(alliance.getString(alliance.getColumnIndexOrThrow(DBHelper.ALLIANCE_NAME)));

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
