package oddymobstar.message.out;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 26/02/15.
 */
public class OutCoreMessage {

    protected JSONObject message = new JSONObject();

    protected boolean post = false;

    public static final String CORE_OBJECT = "core";
    public static final String PLAYER = "player";
    public static final String UID = "uid";
    public static final String TYPE = "type";
    public static final String LONG = "long";
    public static final String LAT = "lat";
    public static final String ACK_ID = "ackid";
    public static final String ALLIANCE = "alliance";
    public static final String AID = "aid";
    public static final String ASTAT = "astat";
    public static final String ATYPE = "atype";
    public static final String ACONTENT = "acontent";
    //override the zones we want to send the information to
    public static final String ALAT = "along";
    public static final String ALONG = "alat";
    public static final String ANAME = "aname";
    //types of message
    public static final String JOIN = "join";
    public static final String LEAVE = "leave";
    public static final String PUBLISH = "publish";
    public static final String CREATE = "create";
    //add more when i think about it more.
    //also status to do.  could be priority etc.
    //status .. to implement these.
    public static final String GLOBAL = "global";
    public static final String UTM = "utm";
    public static final String SUBUTM = "subutm";
    public static final String PACKAGE = "package";
    public static final String TOPIC = "topic";
    public static final String TCONT = "tcont";
    public static final String TACT = "tact";
    public static final String TNAME = "tname";
    public static final String TUID = "tuid";
    public static final String TSTAT = "tstat";
    //override the zones we are registering to or sending to
    public static final String TLAT = "tlat";
    public static final String TLONG = "tlong";
    //action
    public static final String TOPIC_GET = "get";


    public OutCoreMessage(LatLng latLng, String uid, String ackId, String type) throws JSONException {

        JSONObject core = new JSONObject();
        core.put(UID, uid);
        core.put(TYPE, type);
        core.put(LAT, latLng.latitude);
        core.put(LONG, latLng.longitude);
        core.put(ACK_ID, ackId);

        message.put(CORE_OBJECT, core);

    }

    public boolean isPost(){ return post; }


    public JSONObject getMessage() {
        return message;
    }
}
