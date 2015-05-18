package oddymobstar.service.handler;

import org.json.JSONException;

import java.util.Map;

import oddymobstar.database.DBHelper;
import oddymobstar.message.in.Acknowledge;
import oddymobstar.message.in.InCoreMessage;
import oddymobstar.message.out.OutCoreMessage;
import oddymobstar.service.message.AcknowledgeService;
import oddymobstar.service.message.AllianceService;
import oddymobstar.service.message.GridService;
import oddymobstar.util.Configuration;

/**
 * Created by root on 17/03/15.
 */
public class MessageService {

    private DBHelper dbHelper;

    //handlers
    private AcknowledgeService acknowledgeService;
    private AllianceService allianceService;
    private GridService gridService;


    public MessageService(DBHelper dbHelper, Configuration configuration) {
        this.dbHelper = dbHelper;

        acknowledgeService = new AcknowledgeService(configuration, dbHelper);
        allianceService = new AllianceService(dbHelper);
        gridService = new GridService(dbHelper);

    }

    public Map<String, OutCoreMessage> getSentAcks() {
        return acknowledgeService.getSentAcknowledges();
    }

    public Map<String, OutCoreMessage> getSentPosts() {
        return acknowledgeService.getSentPosts();
    }


    public void handleMessage(InCoreMessage coreMessage, Acknowledge ack) throws JSONException {


        if (!coreMessage.getJsonObject().isNull(InCoreMessage.ACKNOWLEDGE)) {

            acknowledgeService.setAcknowledge(ack);
            acknowledgeService.handle(coreMessage);

        } else if (!coreMessage.getJsonObject().isNull(InCoreMessage.GRID)) {

            gridService.handle(coreMessage);

        } else if (!coreMessage.getJsonObject().isNull(InCoreMessage.ALLIANCE)) {

            allianceService.handle(coreMessage);
        }

    }


}
