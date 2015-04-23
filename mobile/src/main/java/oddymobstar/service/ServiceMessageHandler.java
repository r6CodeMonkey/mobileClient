package oddymobstar.service;

import android.util.Log;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import oddymobstar.core.Alliance;
import oddymobstar.core.Config;
import oddymobstar.core.Message;
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
    private Map<String, OutCoreMessage> sentPosts = new HashMap<>();


    public ServiceMessageHandler(DBHelper dbHelper, Configuration configuration) {
        this.dbHelper = dbHelper;
        this.configuration = configuration;
    }

    public Map<String, OutCoreMessage> getSentAcks() {
        return sentAcks;
    }

    public Map<String, OutCoreMessage> getSentPosts(){return sentPosts; }


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

                    }else{
                        //its player id
                        Config config = configuration.getConfig(Configuration.PLAYER_KEY);
                        config.setValue(ack.getUid());
                        dbHelper.updateConfig(config);
                    }
                }else {

                        //
                        if (sentPosts.containsKey(ack.getAckId())) {


                            Message message = new Message();
                            message.setMyMessage("Y");
                            message.setTime(time);
                            message.setMessageKey(ack.getOid());
                            message.setAuthor("Me");


                            String type = "";
                            String post = "";


                            //now we need to work out what type parent was and set key and type...
                            OutCoreMessage out = sentPosts.get(ack.getAckId());

                            if (out instanceof OutTopicMessage) {
                                type = Message.TOPIC_MESSAGE;
                                post = ((OutTopicMessage)out).getContent();

                            } else if (out instanceof OutAllianceMessage) {
                                type = Message.ALLIANCE_MESSAGE;
                                post = ((OutAllianceMessage)out).getContent();
                            }

                            message.setMessageType(type);
                            message.setMessage(post);

                            dbHelper.addMessage(message);
                            sentPosts.remove(ack.getAckId());
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

        } else if (!coreMessage.getJsonObject().isNull(InCoreMessage.ALLIANCE)) {

            InAllianceMessage allianceMessage = new InAllianceMessage(coreMessage.getJsonObject().getJSONObject(InCoreMessage.ALLIANCE));

            Message message = new Message();
            message.setTime(time);
            message.setAuthor(allianceMessage.getAmid());
            message.setMessage(allianceMessage.getMessage());
            message.setMessageType(Message.ALLIANCE_MESSAGE);
            message.setMessageKey(allianceMessage.getAid());

            dbHelper.addMessage(message);

            //need to actually fix this up....next ticket.
            switch(allianceMessage.getType()){
                case OutAllianceMessage.JOIN:
                    dbHelper.addAllianceMember();
                    break;
                case OutAllianceMessage.LEAVE:
                    dbHelper.deleteAllianceMember();
                    break;
                default:
                    break;
            }
            //all alliance messages currently leave / join etc.  so can go on the message posts...make sense.

        } else if (!coreMessage.getJsonObject().isNull(InCoreMessage.TOPIC)) {

            InTopicMessage topicMessage = new InTopicMessage(coreMessage.getJsonObject().getJSONObject(InCoreMessage.TOPIC));

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
            }else{
                //its a proper publish....so its based on global / utm / subutm....not implemented yet.
                if(topicMessage.getFilter().equals(OutTopicMessage.GLOBAL)){

                    Message message = new Message();
                    message.setTime(time);
                    message.setAuthor("");
                    message.setMessage(topicMessage.getMessage());
                    message.setMessageType(Message.TOPIC_MESSAGE);
                    message.setMessageKey(topicMessage.getTid());

                    dbHelper.addMessage(message);


                }else{
                    //its utm or sub utm...so we need to see if subscribed to it.  im also not sure if we need this anyway
                    //i may well park it in a bit.
                }

            }

        }

    }


}
