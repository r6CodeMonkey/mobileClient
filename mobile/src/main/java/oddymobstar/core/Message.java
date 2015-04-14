package oddymobstar.core;

import org.json.JSONObject;

import java.sql.Timestamp;

/**
 * Created by root on 08/04/15.
 */
public class Message {

    public static final String ALLIANCE_MESSAGE = "A";
    public static final String TOPIC_MESSAGE = "T";


    private long id;
    private long time;
    private String message;
    private String messageType;
    private String messageKey;
    private String myMessage;

    public Message() {

    }

    public Message(JSONObject message) {

    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public void setMyMessage(String myMessage) {
        this.myMessage = myMessage;
    }

    public long getTime() {
        return time;
    }

    public long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public boolean isMyMessage() {
        return myMessage.equals("Y");
    }

    public String getTimeStamp() {
        Timestamp ts = new Timestamp(time);

        return ts.toString();
    }

}
