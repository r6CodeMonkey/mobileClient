package oddymobstar.model;

import android.database.Cursor;

import oddymobstar.database.DBHelper;

/**
 * Created by root on 25/02/15.
 */
public class Grid {

    private String key = "";
    private String utm = "";
    private String subUtm = "";

    //

    public Grid() {

    }

    public Grid(Cursor grid) {
        setKey(grid.getString(grid.getColumnIndexOrThrow(DBHelper.GRID_KEY)));
        setUtm(grid.getString(grid.getColumnIndexOrThrow(DBHelper.UTM)));
        setSubUtm(grid.getString(grid.getColumnIndexOrThrow(DBHelper.SUBUTM)));
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setUtm(String utm) {
        this.utm = utm;
    }

    public void setSubUtm(String subUtm) {
        this.subUtm = subUtm;
    }

    public String getKey() {
        return key;
    }

    public String getUtm() {
        return utm;
    }

    public String getSubUtm() {
        return subUtm;
    }

}
