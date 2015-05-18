package oddymobstar.message.out;

import android.location.Location;

import org.json.JSONException;

/**
 * Created by root on 26/02/15.
 */
public class OutPackageMessage extends OutCoreMessage {


    public OutPackageMessage(Location location, String uid, String ackId, String type) throws JSONException {
        super(location, uid, ackId, PACKAGE);

    }

    public void setPackage(oddymobstar.model.Package pack) {

    }
}
