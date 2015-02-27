package oddymobstar.message.in;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 25/02/15.
 */
public class Acknowledge {

    public static final String ACKNOWLEDGE = "acknowledge";
    private static final String UID = "uid";
    private static final String OID = "oid";
    private static final String ACK_ID ="ackid";
    private static final String STATE = "state";
    private static final String INFO = "info";

    public static final String ERROR = "error";
    public static final String SUCCESS = "success";

    private String uid;
    private String oid;
    private String ackId;
    private String state;
    private String info;



    public Acknowledge(String message) throws JSONException{

        JSONObject acknowledge = new JSONObject(message).getJSONObject(ACKNOWLEDGE);

        uid = acknowledge.getString(UID);
        oid = acknowledge.getString(OID);
        ackId = acknowledge.getString(ACK_ID);
        state = acknowledge.getString(STATE);
        info = acknowledge.getString(INFO);

    }

    public String getUid(){return uid;}
    public String getOid(){return oid;}
    public String getAckId(){return ackId;}
    public String getState(){return state;}
    public String getInfo(){return info;}



}
