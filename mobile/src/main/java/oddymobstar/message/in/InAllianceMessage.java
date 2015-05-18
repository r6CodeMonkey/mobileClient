package oddymobstar.message.in;

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

    public InAllianceMessage(JSONObject alliance) throws JSONException {
        this.alliance = alliance;

        create();


    }

    private void create() throws JSONException {


        aid = alliance.getString(InCoreMessage.AID);
        amid = alliance.getString(InCoreMessage.AMID);
        message = alliance.getString(InCoreMessage.MSG);
        type = alliance.getString(InCoreMessage.TYPE);

        try {
            //these are not sent in invite
            utm = alliance.getString(InCoreMessage.UTM);
            subUtm = alliance.getString(InCoreMessage.SUB_UTM);
            latitude = alliance.getDouble(InCoreMessage.LATITUDE);
            longitude = alliance.getDouble(InCoreMessage.LONGITUDE);

        } catch (Exception e) {
        }


        try {
            //and this is only set in invite mode.
            name = alliance.getString(InCoreMessage.ANAME);

        } catch (Exception e) {

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
