package oddymobstar.message.in;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 26/02/15.
 */
public class TopicMessage {

    public final static String TOPIC = "topic";

    private final static String TID = "tid";
    private final static String TNAME = "tname";
    private final static String MSG = "msg";
    private final static String FILTER = "filter";

    public static final String POST = "post";

    private String tid;
    private String message;
    private String title;
    private String filter;

    private JSONObject topic;


    public TopicMessage(JSONObject topic) {

        this.topic = topic;
    }

    public void create() throws JSONException{

        tid = topic.getString(TID);
        this.message = topic.getString(MSG);
        title = topic.getString(TNAME);
        filter = topic.getString(FILTER);
    }

    public String getTid(){return tid;}
    public String getMessage(){return message;}
    public String getTitle(){ return title;}
    public String getFilter(){return filter;}
}
