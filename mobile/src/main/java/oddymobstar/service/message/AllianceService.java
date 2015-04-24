package oddymobstar.service.message;

import org.json.JSONException;

import oddymobstar.database.DBHelper;
import oddymobstar.message.in.InAllianceMessage;
import oddymobstar.message.in.InCoreMessage;
import oddymobstar.message.out.OutAllianceMessage;
import oddymobstar.model.Message;

/**
 * Created by root on 23/04/15.
 */
public class AllianceService implements MessageInterface {

    private DBHelper dbHelper;

    public AllianceService(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void handle(InCoreMessage coreMessage) throws JSONException {

        long time = coreMessage.getJsonObject().getLong(InCoreMessage.TIME);

        InAllianceMessage allianceMessage = new InAllianceMessage(coreMessage.getJsonObject().getJSONObject(InCoreMessage.ALLIANCE));

        Message message = new Message();
        message.setTime(time);
        message.setAuthor(allianceMessage.getAmid());
        message.setMessage(allianceMessage.getMessage());
        message.setMessageType(Message.ALLIANCE_MESSAGE);
        message.setMessageKey(allianceMessage.getAid());

        dbHelper.addMessage(message);

        //need to actually fix this up....next ticket.
        switch (allianceMessage.getType()) {
            case OutAllianceMessage.JOIN:
                dbHelper.addAllianceMember();
                break;
            case OutAllianceMessage.LEAVE:
                dbHelper.deleteAllianceMember();
                break;
            default:
                break;
        }
        //all alliance messages currently leave / join etc.  so can go on the message posts...make sense.


    }
}
