package oddymobstar.message.in;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by root on 25/02/15.
 */
public class Acknowledge {


    private String uid;
    private String oid;
    private String ackId;
    private String state;
    private String info;
    private String utm;
    private String subUtm;


    private JSONObject acknowledge;


    public Acknowledge(JSONObject acknowledge) {

        this.acknowledge = acknowledge;

        try{
            create();
        }catch(JSONException jse){
            Log.d(this.getClass().getName(), "json exception "+jse.getMessage());
        }



    }

    private void create() throws JSONException {


        uid = acknowledge.getString(InCoreMessage.UID);
        oid = acknowledge.getString(InCoreMessage.OID);
        ackId = acknowledge.getString(InCoreMessage.ACK_ID);
        state = acknowledge.getString(InCoreMessage.STATE);
        info = acknowledge.getString(InCoreMessage.INFO);
        utm = acknowledge.getString(InCoreMessage.UTM);
        subUtm = acknowledge.getString(InCoreMessage.SUB_UTM);


    }

    public String getUid() {
        return uid;
    }

    public String getOid() {
        return oid;
    }

    public String getAckId() {
        return ackId;
    }

    public String getState() {
        return state;
    }

    public String getInfo() {
        return info;
    }

    public String getUtm() {
        return utm;
    }

    public String getSubUtm() {
        return subUtm;
    }



}
