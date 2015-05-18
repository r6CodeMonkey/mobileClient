package oddymobstar.service.message;

import org.json.JSONException;

import oddymobstar.database.DBHelper;
import oddymobstar.message.in.GridMessage;
import oddymobstar.message.in.InCoreMessage;
import oddymobstar.model.AllianceMember;

/**
 * Created by root on 23/04/15.
 */
public class GridService implements MessageInterface {

    private DBHelper dbHelper;

    public GridService(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }


    @Override
    public void handle(InCoreMessage coreMessage) throws JSONException {

        GridMessage gridMessage = new GridMessage(coreMessage.getJsonObject().getJSONObject(InCoreMessage.GRID));

        switch (gridMessage.getType()) {
            case GridMessage.PACKAGE:
                break;
            case GridMessage.PLAYER:
                //we update our alliance member...
                AllianceMember allianceMember = new AllianceMember(gridMessage);
                dbHelper.updateAllianceMember(allianceMember);
                break;
        }


    }

}
