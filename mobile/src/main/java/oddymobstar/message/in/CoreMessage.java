package oddymobstar.message.in;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 01/03/15.
 */
public class CoreMessage {

    public static final String CORE = "core";

    protected JSONObject jsonObject;

    public CoreMessage(String message) throws JSONException{
        jsonObject = new JSONObject(message).getJSONObject(CORE);
    }

    public JSONObject getJsonObject(){return jsonObject;}
}
