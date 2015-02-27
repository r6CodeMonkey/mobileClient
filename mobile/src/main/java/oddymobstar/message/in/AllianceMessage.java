package oddymobstar.message.in;

import org.json.JSONException;
import org.json.JSONObject;

import oddymobstar.core.Alliance;

/**
 * Created by root on 26/02/15.
 */
public class AllianceMessage {

    public final static String ALLIANCE = "alliance";
    private final static String AID = "aid";
    private final static String MSG = "msg";

    private String aid;
    private String message;

    public AllianceMessage(String message) throws JSONException{

        JSONObject alliance = new JSONObject(message).getJSONObject(ALLIANCE);

        aid = alliance.getString(AID);
        this.message = alliance.getString(MSG);
    }

    public String getAid(){return aid;}
    public String getMessage(){return message;}
}
