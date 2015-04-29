package oddymobstar.message.in;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 26/02/15.
 */
public class InAllianceMessage {


    private String aid;
    private String amid;
    private String message;
    private String type;
    private String name;
    private String utm;
    private String subUtm;
    private double latitude;
    private double longitude;


    private JSONObject alliance;

    public InAllianceMessage(JSONObject alliance) {
        this.alliance = alliance;

        try {
            create();
        } catch (JSONException jse) {
            Log.d(this.getClass().getName(), "json exception " + jse.getMessage());
        }


    }

    private void create() throws JSONException {


        aid = alliance.getString(InCoreMessage.AID);
        amid = alliance.getString(InCoreMessage.AMID);
        message = alliance.getString(InCoreMessage.MSG);
        type = alliance.getString(InCoreMessage.TYPE);
        utm = alliance.getString(InCoreMessage.UTM);
        subUtm = alliance.getString(InCoreMessage.SUB_UTM);
        latitude = alliance.getDouble(InCoreMessage.LATITUDE);
        longitude = alliance.getDouble(InCoreMessage.LONGITUDE);

        try {
            name = alliance.getString(InCoreMessage.ANAME);
        } catch (Exception e) {
            //we dont always set this its used for invite....need to refactor..
        }


    }

    public String getAid() {
        return aid;
    }

    public String getAmid() {
        return amid;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public String getUtm() {
        return utm;
    }

    public String getSubUtm() {
        return subUtm;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
