package oddymobstar.message.in;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 26/02/15.
 */
public class InTopicMessage {


    private String tid;
    private String message;
    private String title;
    private String filter;

    private JSONObject topic;


    public InTopicMessage(JSONObject topic) {

        this.topic = topic;
    }

    public void create() throws JSONException {

        tid = topic.getString(InCoreMessage.TID);
        this.message = topic.getString(InCoreMessage.MSG);
        title = topic.getString(InCoreMessage.TNAME);
        filter = topic.getString(InCoreMessage.FILTER);
    }

    public String getTid() {
        return tid;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public String getFilter() {
        return filter;
    }
}
