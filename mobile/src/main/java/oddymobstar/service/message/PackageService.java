package oddymobstar.service.message;

import org.json.JSONException;

import oddymobstar.database.DBHelper;
import oddymobstar.message.in.InCoreMessage;

/**
 * Created by root on 23/04/15.
 */
public class PackageService implements MessageInterface {


    private DBHelper dbHelper;

    public PackageService(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void handle(InCoreMessage coreMessage) throws JSONException {

    }
}
