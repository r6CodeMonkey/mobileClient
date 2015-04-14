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

    private JSONObject alliance;

    public InAllianceMessage(JSONObject alliance) {
        this.alliance = alliance;
    }

    public void create() throws JSONException {


        aid = alliance.getString(InCoreMessage.AID);
        amid = alliance.getString(InCoreMessage.AMID);
        message = alliance.getString(InCoreMessage.MSG);

    }

    public String getAid() {
        return aid;
    }

    public String getAmid() {
        return amid;
    }

    public String getMessage() {
        return message;
    }
}
