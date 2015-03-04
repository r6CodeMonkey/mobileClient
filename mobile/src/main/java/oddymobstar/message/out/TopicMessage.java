package oddymobstar.message.out;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import oddymobstar.core.Topic;

/**
 * Created by root on 26/02/15.  ffs root due to issue with attendance tracker.
 * must ditch root at some point.  when i dont need to see that project.  due to key signing.
 */
public class TopicMessage extends CoreMessage {

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
    public static final String TOPIC_CREATE = "create";
    public static final String TOPIC_PUBLISH = "publish";
    public static final String TOPIC_JOIN = "join";
    public static final String TOPIC_LEAVE = "leave";
    public static final String TOPIC_GET = "get";

    //status
    public static final String TOPIC_GLOBAL = "global";
    public static final String TOPIC_UTM = "utm";
    public static final String TOPIC_SUB_UTM = "subutm";


    public TopicMessage(LatLng latLng, String uid, String ackId) throws JSONException {
        super(latLng, uid, ackId, TOPIC);
    }

    public void setTopic(Topic topic, String type, String status, String msg) throws JSONException {

        JSONObject json = new JSONObject();

        json.put(TNAME, topic.getName());
        json.put(TUID, topic.getKey());
        json.put(TACT, type);
        json.put(TCONT, msg);
        json.put(TSTAT, status);


        message.getJSONObject(CORE_OBJECT).put(TOPIC, json);

    }
}
