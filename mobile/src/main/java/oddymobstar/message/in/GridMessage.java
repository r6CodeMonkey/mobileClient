package oddymobstar.message.in;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 26/02/15.
 */
public class GridMessage {


    private String utm;
    private String subUtm;
    private String message;

    private JSONObject grid;

    public GridMessage(JSONObject grid) {
        this.grid = grid;
    }

    public void create() throws JSONException {


        utm = grid.getString(InCoreMessage.GRID_UTM);
        subUtm = grid.getString(InCoreMessage.SUB_UTM);
        this.message = grid.getString(InCoreMessage.MSG);
    }

    public String getUtm() {
        return utm;
    }

    public String getSubUtm() {
        return subUtm;
    }

    public String getMessage() {
        return message;
    }

}
