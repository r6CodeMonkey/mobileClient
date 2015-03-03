package oddymobstar.message.out;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import oddymobstar.core.Alliance;

/**
 * Created by root on 26/02/15.
 */
public class AllianceMessage extends CoreMessage {

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
    public static final String ALLIANCE_JOIN = "join";
    public static final String ALLIANCE_LEAVE = "leave";
    public static final String ALLIANCE_PUBLISH = "publish";
    public static final String ALLIANCE_CREATE = "create";
    //add more when i think about it more.
    //also status to do.  could be priority etc.
    //status .. to implement these.
    public static final String ALLIANCE_GLOBAL = "global";
    public static final String ALLIANCE_UTM = "utm";
    public static final String ALLIANCE_SUBUTM = "subutm";

    public AllianceMessage(LatLng latLng, String uid, String ackId) throws JSONException{
        super(latLng,uid,ackId,ALLIANCE);


    }

    public void setAlliance(Alliance alliance, String type, String status, String msg) throws JSONException{

        JSONObject json = new JSONObject();

        json.put(AID, alliance.getKey());
        json.put(ACONTENT,msg);
        json.put(ATYPE, type);
        json.put(ASTAT, status);
        json.put(ANAME, alliance.getName());

        //also need lat long etc.

        message.getJSONObject(CORE_OBJECT).put(ALLIANCE,json);

    }
}
