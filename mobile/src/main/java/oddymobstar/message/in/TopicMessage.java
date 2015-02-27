package oddymobstar.message.in;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 26/02/15.
 */
public class TopicMessage {

    public final static String TOPIC = "topic";

    private final static String TID = "tid";
    private final static String MSG = "msg";

    private String tid;
    private String message;


    public TopicMessage(String message) throws JSONException{

        JSONObject topic = new JSONObject(message).getJSONObject(TOPIC);

        tid = topic.getString(TID);
        this.message = topic.getString(MSG);
    }

    public String getTid(){return tid;}
    public String getMessage(){return message;}
}
