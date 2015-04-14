package oddymobstar.message.out;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import oddymobstar.core.Alliance;

/**
 * Created by root on 26/02/15.
 */
public class OutAllianceMessage extends OutCoreMessage {


    public OutAllianceMessage(LatLng latLng, String uid, String ackId) throws JSONException {
        super(latLng, uid, ackId, ALLIANCE);


    }

    public void setAlliance(Alliance alliance, String type, String status, String msg) throws JSONException {

        JSONObject json = new JSONObject();

        json.put(AID, alliance.getKey());
        json.put(ACONTENT, msg);
        json.put(ATYPE, type);
        json.put(ASTAT, status);
        json.put(ANAME, alliance.getName());

        //also need lat long etc.

        message.getJSONObject(CORE_OBJECT).put(ALLIANCE, json);

    }
}
