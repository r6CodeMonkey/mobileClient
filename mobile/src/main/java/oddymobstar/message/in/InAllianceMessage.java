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
}
