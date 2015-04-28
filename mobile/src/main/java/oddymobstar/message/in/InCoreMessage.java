package oddymobstar.message.in;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 01/03/15.
 */
public class InCoreMessage {

    public static final String CORE = "core";
    public static final String ACKNOWLEDGE = "acknowledge";
    public static final String UID = "uid";
    public static final String OID = "oid";
    public static final String UTM = "utm";
    public static final String ACK_ID = "ackid";
    public static final String STATE = "state";
    public static final String INFO = "info";
    public static final String ERROR = "error";
    public static final String SUCCESS = "success";
    public static final String ACTIVE = "ACTIVE";
    public static final String UUID = "UUID";
    public final static String ALLIANCE = "alliance";
    public final static String AID = "aid";
    public final static String AMID = "amid";
    public static final String GRID = "grid";
    public static final String GRID_UTM = "utm";
    public static final String SUB_UTM = "subutm";
    public final static String TOPIC = "topic";
    public final static String MSG = "msg";
    public final static String POST = "post";
    public final static String TIME = "time";
    public final static String TYPE = "type";
    public static final String ANAME = "aname";


    protected JSONObject jsonObject;

    public InCoreMessage(String message) throws JSONException {
        jsonObject = new JSONObject(message).getJSONObject(CORE);
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
