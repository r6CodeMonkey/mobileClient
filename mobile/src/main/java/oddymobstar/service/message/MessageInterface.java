package oddymobstar.service.message;

import org.json.JSONException;

import oddymobstar.message.in.InCoreMessage;

/**
 * Created by root on 23/04/15.
 */
public interface MessageInterface {

    public void handle(InCoreMessage coreMessage) throws JSONException;
}
