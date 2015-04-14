package oddymobstar.message.out;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

/**
 * Created by root on 26/02/15.
 */
public class OutPackageMessage extends OutCoreMessage {


    public OutPackageMessage(LatLng latLng, String uid, String ackId, String type) throws JSONException {
        super(latLng, uid, ackId, PACKAGE);

    }

    public void setPackage(oddymobstar.core.Package pack) {

    }
}
