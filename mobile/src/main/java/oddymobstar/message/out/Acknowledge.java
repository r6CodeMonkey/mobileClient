package oddymobstar.message.out;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 27/04/15.
 */
public class Acknowledge {

    //used by bluetooth etc.

    private JSONObject ack = new JSONObject();

    private String ackId;
    private String name;

    public static final String SUCCESS = "SUCCESS";


    public Acknowledge(String ackId, String name
    ) throws JSONException {

        ack.put(OutCoreMessage.ACK_ID, ackId);
        ack.put(OutCoreMessage.NAME, name);

    }

    public JSONObject getAcknowledge() {
        return ack;
    }

    public Acknowledge(JSONObject ack) throws JSONException {

        ackId = ack.getString(OutCoreMessage.ACK_ID);
        name = ack.getString(OutCoreMessage.NAME);

    }

    public String getAckId() {
        return ackId;
    }

    public String getName() {
        return name;
    }


}
