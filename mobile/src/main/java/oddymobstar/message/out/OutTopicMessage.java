package oddymobstar.message.out;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import oddymobstar.core.Topic;

/**
 * Created by root on 26/02/15.  ffs root due to issue with attendance tracker.
 * must ditch root at some point.  when i dont need to see that project.  due to key signing.
 */
public class OutTopicMessage extends OutCoreMessage {


    public OutTopicMessage(LatLng latLng, String uid, String ackId) throws JSONException {
        super(latLng, uid, ackId, TOPIC);
    }

    public void setTopic(Topic topic, String type, String status, String msg) throws JSONException {

        JSONObject json = new JSONObject();

        json.put(TNAME, topic.getName());
        json.put(TUID, topic.getKey());
        json.put(TACT, type);
        json.put(TCONT, msg);
        json.put(TSTAT, status);

        post = type.equals(PUBLISH);


        message.getJSONObject(CORE_OBJECT).put(TOPIC, json);

    }

    public String getContent() throws JSONException{
        return message.getJSONObject(CORE_OBJECT).getJSONObject(TOPIC).getString(TCONT);
    }
}
