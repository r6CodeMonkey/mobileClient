package oddymobstar.message.out;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by timmytime on 13/07/15.
 */
public class OutImageMessage extends OutCoreMessage {

    public OutImageMessage(Location location, String uid, String ackId) throws JSONException {
        super(location, uid, ackId, IMAGE);
    }

    public void setImage(String image) throws JSONException {

        JSONObject json = new JSONObject();

        json.put(IMAGE, image);

        message.getJSONObject(CORE_OBJECT).put(IMAGE, json);

    }

}
