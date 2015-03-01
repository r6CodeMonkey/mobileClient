package oddymobstar.message.in;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by root on 26/02/15.
 */
public class GridMessage{

    public static final String GRID = "grid";
    private static final String GRID_UTM = "utm";
    private static final String SUB_UTM = "subutm";
    private static final String MSG = "msg";

    private String utm;
    private String subUtm;
    private String message;

    private JSONObject grid;

    public GridMessage(JSONObject grid) {
        this.grid = grid;
    }

    public void create() throws JSONException{



        utm = grid.getString(GRID_UTM);
        subUtm = grid.getString(SUB_UTM);
        this.message = grid.getString(MSG);
    }

    public String getUtm(){return utm;}
    public String getSubUtm(){return subUtm;}
    public String getMessage(){return message;}

}
