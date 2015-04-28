package oddymobstar.service.message;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import oddymobstar.database.DBHelper;
import oddymobstar.message.in.Acknowledge;
import oddymobstar.message.in.InCoreMessage;
import oddymobstar.message.out.OutAllianceMessage;
import oddymobstar.message.out.OutCoreMessage;
import oddymobstar.message.out.OutPackageMessage;
import oddymobstar.model.Alliance;
import oddymobstar.model.Config;
import oddymobstar.model.Message;
import oddymobstar.util.Configuration;

/**
 * Created by root on 23/04/15.
 */
public class AcknowledgeService implements MessageInterface {

    private Configuration configuration;
    private Acknowledge acknowledge;
    private Map<String, OutCoreMessage> sentAcknowledges = new HashMap<>();
    private Map<String, OutCoreMessage> sentPosts = new HashMap<>();
    private DBHelper dbHelper;


    public AcknowledgeService(Configuration configuration, DBHelper dbHelper) {
        this.configuration = configuration;
        this.dbHelper = dbHelper;
    }

    public void setAcknowledge(Acknowledge acknowledge) {
        this.acknowledge = acknowledge;
    }

    public Map<String, OutCoreMessage> getSentAcknowledges() {
        return sentAcknowledges;
    }

    public Map<String, OutCoreMessage> getSentPosts() {
        return sentPosts;
    }


    @Override
    public void handle(InCoreMessage coreMessage) throws JSONException {

        long time = coreMessage.getJsonObject().getLong(InCoreMessage.TIME);


        OutCoreMessage ackSent = sentAcknowledges.get(acknowledge.getAckId());

        //is it a UUID message
        if (acknowledge.getInfo().equals(InCoreMessage.UUID)) {
            if (ackSent instanceof OutAllianceMessage) {

                Alliance alliance = new Alliance();
                alliance.setKey(acknowledge.getOid());
                alliance.setName(ackSent.getMessage().getJSONObject(OutCoreMessage.CORE_OBJECT).getJSONObject(OutAllianceMessage.ALLIANCE).getString(OutAllianceMessage.NAME));
                dbHelper.addAlliance(alliance, false);

            } else if (ackSent instanceof OutPackageMessage) {

            } else {
                //its player id
                Config config = configuration.getConfig(Configuration.PLAYER_KEY);
                config.setValue(acknowledge.getUid());
                dbHelper.updateConfig(config);
            }
        } else {

            //
            if (sentPosts.containsKey(acknowledge.getAckId())) {


                Message message = new Message();
                message.setMyMessage("Y");
                message.setTime(time);
                message.setMessageKey(acknowledge.getOid());
                message.setAuthor("Me");


                String type = "";
                String post = "";


                //now we need to work out what type parent was and set key and type...
                OutCoreMessage out = sentPosts.get(acknowledge.getAckId());

                if (out instanceof OutAllianceMessage) {
                    type = Message.ALLIANCE_MESSAGE;
                    post = ((OutAllianceMessage) out).getContent();
                }

                message.setMessageType(type);
                message.setMessage(post);

                dbHelper.addMessage(message);
                sentPosts.remove(acknowledge.getAckId());
            }


        }

                                  /*
                                   also we have the current UTM / SUBUTM so we need to update this too.

                                   */
        if (!(ackSent instanceof OutAllianceMessage
                || ackSent instanceof OutPackageMessage)) {
            Config config = configuration.getConfig(Configuration.CURRENT_UTM);
            config.setValue(acknowledge.getUtm());
            dbHelper.updateConfig(config);

            config = configuration.getConfig(Configuration.CURRENT_SUBUTM);
            config.setValue(acknowledge.getSubUtm());
            dbHelper.updateConfig(config);
        }

        sentAcknowledges.remove(acknowledge.getAckId());

    }
}
