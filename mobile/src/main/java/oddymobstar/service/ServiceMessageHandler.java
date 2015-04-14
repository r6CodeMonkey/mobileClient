package oddymobstar.service;

import android.util.Log;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import oddymobstar.core.Alliance;
import oddymobstar.core.Config;
import oddymobstar.core.Topic;
import oddymobstar.database.DBHelper;
import oddymobstar.message.in.Acknowledge;
import oddymobstar.message.in.GridMessage;
import oddymobstar.message.in.InAllianceMessage;
import oddymobstar.message.in.InCoreMessage;
import oddymobstar.message.in.InTopicMessage;
import oddymobstar.message.out.OutAllianceMessage;
import oddymobstar.message.out.OutCoreMessage;
import oddymobstar.message.out.OutPackageMessage;
import oddymobstar.message.out.OutTopicMessage;
import oddymobstar.util.Configuration;

/**
 * Created by root on 17/03/15.
 */
public class ServiceMessageHandler {

    private DBHelper dbHelper;
    private Configuration configuration;
    private Map<String, OutCoreMessage> sentAcks = new HashMap<>();


    public ServiceMessageHandler(DBHelper dbHelper, Configuration configuration) {
        this.dbHelper = dbHelper;
        this.configuration = configuration;
    }

    public Map<String, OutCoreMessage> getSentAcks() {
        return sentAcks;
    }


    public void handleMessage(InCoreMessage coreMessage, OutCoreMessage ackSent, Acknowledge ack) throws JSONException {

        long time = coreMessage.getJsonObject().getLong(InCoreMessage.TIME);

        if (!coreMessage.getJsonObject().isNull(InCoreMessage.ACKNOWLEDGE)) {
            if (ackSent != null) {
                //is it a UUID message
                if (ack.getInfo().equals(InCoreMessage.UUID)) {
                    if (ackSent instanceof OutTopicMessage) {

                        Topic topic = new Topic();
                        topic.setKey(ack.getOid());
                        topic.setName(ackSent.getMessage().getJSONObject(OutCoreMessage.CORE_OBJECT).getJSONObject(OutTopicMessage.TOPIC).getString(OutTopicMessage.TNAME));
                        dbHelper.addTopic(topic);

                    } else if (ackSent instanceof OutAllianceMessage) {

                        Alliance alliance = new Alliance();
                        alliance.setKey(ack.getOid());
                        alliance.setName(ackSent.getMessage().getJSONObject(OutCoreMessage.CORE_OBJECT).getJSONObject(OutAllianceMessage.ALLIANCE).getString(OutAllianceMessage.ANAME));
                        dbHelper.addAlliance(alliance);

                    } else if (ackSent instanceof OutPackageMessage) {

                    } else {
                        //its core.  therefore player.
                        Config config = configuration.getConfig(Configuration.PLAYER_KEY);
                        config.setValue(ack.getUid());
                        dbHelper.updateConfig(config);
                    }
                }

                                  /*
                                   also we have the current UTM / SUBUTM so we need to update this too.

                                   */
                if (!(ackSent instanceof OutAllianceMessage
                        || ackSent instanceof OutTopicMessage
                        || ackSent instanceof OutPackageMessage)) {
                    Config config = configuration.getConfig(Configuration.CURRENT_UTM);
                    config.setValue(ack.getUtm());
                    dbHelper.updateConfig(config);

                    config = configuration.getConfig(Configuration.CURRENT_SUBUTM);
                    config.setValue(ack.getSubUtm());
                    dbHelper.updateConfig(config);
                }

                sentAcks.remove(ack.getAckId());
            }

        } else if (!coreMessage.getJsonObject().isNull(InCoreMessage.GRID)) {

            GridMessage gridMessage = new GridMessage(coreMessage.getJsonObject().getJSONObject(InCoreMessage.GRID));
            gridMessage.create();

        } else if (!coreMessage.getJsonObject().isNull(InCoreMessage.ALLIANCE)) {

            InAllianceMessage allianceMessage = new InAllianceMessage(coreMessage.getJsonObject().getJSONObject(InCoreMessage.ALLIANCE));
            allianceMessage.create();


        } else if (!coreMessage.getJsonObject().isNull(InCoreMessage.TOPIC)) {

            InTopicMessage topicMessage = new InTopicMessage(coreMessage.getJsonObject().getJSONObject(InCoreMessage.TOPIC));
            topicMessage.create();

            if (topicMessage.getFilter().equals(InCoreMessage.POST)) {

                try {
                    Topic topic = new Topic();
                    topic.setKey(topicMessage.getTid());
                    topic.setName(topicMessage.getTitle());
                    Topic checkGlobal = dbHelper.getGlobalTopic(topic.getKey());
                    if (!checkGlobal.getKey().equals(topic.getKey())) {
                        dbHelper.addGlobalTopic(topic);
                    }
                    //this may not be catching it lol.  it seems not.  otherwise it all works (ie get global topics)
                } catch (android.database.sqlite.SQLiteConstraintException e) {
                    Log.d("topic error", "sql exception  " + e.toString());
                }
            }

        }

    }


}
