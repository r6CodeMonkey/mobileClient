package oddymobstar.message.in;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 26/02/15.
 */
public class GridMessage {

    public static final String PLAYER = "player";
    public static final String PACKAGE = "package";


    private String utm;
    private String subUtm;
    private String key;
    private String type;
    private double latitude;
    private double longitude;
    private double speed;
    private double altitude;
    private String message;

    private JSONObject grid;

    public GridMessage(JSONObject grid) throws JSONException {
        this.grid = grid;

        create();

    }

    private void create() throws JSONException {


        utm = grid.getString(InCoreMessage.UTM);
        subUtm = grid.getString(InCoreMessage.SUB_UTM);
        message = grid.getString(InCoreMessage.MSG);
        key = grid.getString(InCoreMessage.KEY);
        type = grid.getString(InCoreMessage.TYPE);
        latitude = grid.getDouble(InCoreMessage.LATITUDE);
        longitude = grid.getDouble(InCoreMessage.LONGITUDE);
        speed = grid.getDouble(InCoreMessage.SPEED);
        altitude = grid.getDouble(InCoreMessage.ALTITUDE);


    }

    public String getUtm() {
        return utm;
    }

    public String getSubUtm() {
        return subUtm;
    }

    public String getMessage() {
        return message;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getSpeed() {
        return speed;
    }

    public double getAltitude() {
        return altitude;
    }

}
