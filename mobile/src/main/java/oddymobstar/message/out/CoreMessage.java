package oddymobstar.message.out;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 26/02/15.
 */
public class CoreMessage {

    protected JSONObject message = new JSONObject();

    public static final String CORE_OBJECT = "core";
    public static final String UID = "uid";
    public static final String TYPE = "type";
    public static final String LONG = "long";
    public static final String LAT = "lat";
    public static final String ACK_ID ="ackid";

    public CoreMessage(LatLng latLng, String uid, String ackId, String type) throws JSONException{

        JSONObject core = new JSONObject();
        core.put(UID, uid);
        core.put(TYPE, type);
        core.put(LAT, latLng.latitude);
        core.put(LONG, latLng.longitude);
        core.put(ACK_ID, ackId);

        message.put(CORE_OBJECT,core);

    }

    public JSONObject getMessage(){
        return message;
    }
}
