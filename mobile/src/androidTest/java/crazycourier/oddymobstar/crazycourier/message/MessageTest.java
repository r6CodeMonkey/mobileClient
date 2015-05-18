package crazycourier.oddymobstar.crazycourier.message;


import android.location.Location;
import android.test.AndroidTestCase;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import oddymobstar.message.in.Acknowledge;
import oddymobstar.message.in.GridMessage;
import oddymobstar.message.in.InAllianceMessage;
import oddymobstar.message.in.InCoreMessage;
import oddymobstar.message.out.OutAllianceMessage;
import oddymobstar.message.out.OutCoreMessage;
import oddymobstar.model.Alliance;

import static org.junit.Assert.*;
/**
 * Created by timmytime on 05/05/15.
 */
public class MessageTest extends AndroidTestCase {


    //fuck it i guess we could mess up an assignment in the class at some point

    @Test
    public void testInAcknowledge(){

        JSONObject msg = new JSONObject();
        try {
            msg.put(InCoreMessage.ACK_ID, "ackid");
            msg.put(InCoreMessage.OID, "oid");
            msg.put(InCoreMessage.UID, "uid");
            msg.put(InCoreMessage.STATE, "state");
            msg.put(InCoreMessage.INFO, "info");
            msg.put(InCoreMessage.UTM, "utm");
            msg.put(InCoreMessage.SUB_UTM, "subutm");

            Acknowledge acknowledge = new Acknowledge(msg);

            assertEquals("ackid", acknowledge.getAckId());
            assertEquals("oid", acknowledge.getOid());
            assertEquals("uid", acknowledge.getUid());
            assertEquals("state", acknowledge.getState());
            assertEquals("info", acknowledge.getInfo());
            assertEquals("utm", acknowledge.getUtm());
            assertEquals("subutm", acknowledge.getSubUtm());



        }catch (JSONException jse){
            throw new AssertionError(jse.getMessage());
        }

    }

    @Test
    public void testGridMessage(){

        JSONObject msg = new JSONObject();

        try{


            msg.put(InCoreMessage.UTM, "utm");
            msg.put(InCoreMessage.SUB_UTM, "subutm");
            msg.put(InCoreMessage.MSG, "msg");

            GridMessage gridMessage = new GridMessage(msg);


            assertEquals("utm", gridMessage.getUtm());
            assertEquals("subutm", gridMessage.getSubUtm());
            assertEquals("msg", gridMessage.getMessage());



        }catch(JSONException jse){
            throw  new AssertionError(jse.getMessage());
        }

    }

    @Test
    public void testInAllianceMessage(){

        JSONObject msg = new JSONObject();

        try{


            msg.put(InCoreMessage.AID, "aid");
            msg.put(InCoreMessage.AMID, "amid");
            msg.put(InCoreMessage.MSG, "msg");
            msg.put(InCoreMessage.TYPE, "type");
            msg.put(InCoreMessage.UTM, "utm");
            msg.put(InCoreMessage.SUB_UTM, "subutm");
            msg.put(InCoreMessage.LATITUDE, 0.0);
            msg.put(InCoreMessage.LONGITUDE, 0.0);
            msg.put(InCoreMessage.ANAME, "name");

            InAllianceMessage allianceMessage = new InAllianceMessage(msg);

            assertEquals("aid", allianceMessage.getAid());
            assertEquals("amid", allianceMessage.getAmid());
            assertEquals("msg", allianceMessage.getMessage());
            assertEquals("type", allianceMessage.getType());
            assertEquals("utm", allianceMessage.getUtm());
            assertEquals("subutm", allianceMessage.getSubUtm());
            assertEquals(0.0, allianceMessage.getLatitude(), 1.0);
            assertEquals(0.0, allianceMessage.getLongitude(),1.0);
            assertEquals("name", allianceMessage.getName());


        }catch(JSONException jse){
            throw  new AssertionError(jse.getMessage());
        }


    }

    @Test
    public void testInCoreMessage(){
//not required it just takes the wrapper out
    }

    @Test
    public void tesstInPackageMessage(){
        //currently blank

    }

    @Test
    public void testOutAcknowledge(){

        JSONObject msg = new JSONObject();

        try{

            msg.put(OutCoreMessage.ACK_ID, "ackid");
            msg.put(OutCoreMessage.NAME, "name");

            oddymobstar.message.out.Acknowledge acknowledge = new oddymobstar.message.out.Acknowledge(msg);

            assertEquals("ackid", acknowledge.getAckId());
            assertEquals("name", acknowledge.getName());



        }catch (JSONException jse){
            throw new AssertionError(jse.getMessage());
        }


    }

    @Test
    public void testOutAllianceMessage(){

        try{

            Location location = new Location("");
            location.setLatitude(0.0);
            location.setLongitude(0.0);
            location.setAltitude(0.0);
            location.setSpeed(0.0f);



            OutAllianceMessage allianceMessage = new OutAllianceMessage(location, "uid", "ackid");
            Alliance alliance = new Alliance();
            alliance.setName("name");
            alliance.setKey("key");
            allianceMessage.setAlliance(alliance, "type", "status", "msg");

            JSONObject core = allianceMessage.getMessage().getJSONObject(OutCoreMessage.CORE_OBJECT);
            JSONObject msg = core.getJSONObject(OutCoreMessage.ALLIANCE);

            assertEquals("type", msg.getString(OutCoreMessage.TYPE));
            assertEquals("uid", core.getString(OutCoreMessage.UID));
            assertEquals("msg", msg.getString(OutCoreMessage.MSG));
            assertEquals("status", msg.getString(OutCoreMessage.STAT));
            assertEquals("ackid", core.getString(OutCoreMessage.ACK_ID));
            assertEquals("name", msg.getString(OutCoreMessage.NAME));
            assertEquals("key", msg.getString(OutCoreMessage.AID));
            assertEquals(0.0, core.getDouble(OutCoreMessage.LAT), 1.0);
            assertEquals(0.0, core.getDouble(OutCoreMessage.LONG), 1.0);



        }catch (JSONException jse){
            throw new AssertionError(jse.toString());
        }

    }

    @Test
    public void testOutCoreMessage(){
      //not required see above...
    }

    @Test
    public void testOutPackageMessage(){
    //not set up yet..
    }

}
