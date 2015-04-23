package oddymobstar.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import oddymobstar.activity.DemoActivity;
import oddymobstar.core.Alliance;
import oddymobstar.core.Config;
import oddymobstar.core.Grid;
import oddymobstar.core.Message;
import oddymobstar.core.Package;
import oddymobstar.core.Topic;
import oddymobstar.util.Configuration;

/**
 * Created by root on 23/02/15.
 */
public class DBHelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CHESERVERTEST";

    public static final String CONFIG_TABLE = "CONFIG";


    public static final String GRIDS_TABLE = "GRIDS";
    public static final String GRID_INFO_TABLE = "GRID_INFO";
    public static final String TOPICS_TABLE = "TOPICS";
    public static final String ALLIANCES_TABLE = "ALLIANCES";
    public static final String ALLIANCE_MEMBERS_TABLE = "ALLIANCE_MEMBERS";
    public static final String PACKAGES_TABLE = "PACKAGES";
    //global topics are called from server......to consider if this gets hectic.
    //ie we allow a search filter?  otherwise, how do we maintain it? .. well we could store locally...maybe we need a local table
    public static final String GLOBAL_TOPICS_TABLE = "GLOBAL_TOPICS";

    public static final String MESSAGE_TABLE = "MESSAGES";


    /*
  key names
   */

    public static final String UTM = "utm";
    public static final String SUBUTM = "subutm";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    public static final String CONFIG_ID = "config_id";
    public static final String CONFIG_NAME = "config_name";
    public static final String CONFIG_VALUE = "config_value";


    public static final String GRID_KEY = "grid_key";
    public static final String TOPIC_KEY = "topic_key";
    public static final String ALLIANCE_KEY = "alliance_key";
    public static final String PACKAGE_KEY = "package_key";
    public static final String PLAYER_KEY = "player_key";

    public static final String TOPIC_NAME = "topic_name";
    public static final String ALLIANCE_NAME = "alliance_name";
    public static final String PLAYER_NAME = "player_name";
    public static final String PACKAGE_NAME = "package_name";

    public static final String INFO_KEY = "grid_info_key";
    public static final String INFO_TYPE = "grid_info_type";

    public static final String MESSAGE_ID = "message_id";
    public static final String MESSAGE_CONTENT = "message_content";
    public static final String MESSAGE_TIME = "message_time";
    public static final String MESSAGE_KEY = "message_key";
    public static final String MESSAGE_TYPE = "message_type";
    public static final String MY_MESSAGE = "my_message";
    public static final String MESSAGE_AUTHOR = "message_author";


    private static final String CREATE_CONFIG = "CREATE TABLE " + CONFIG_TABLE + " (" + CONFIG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + CONFIG_NAME + " VARCHAR2(30)," + CONFIG_VALUE + " VARCHAR2(30))";
    private static final String CREATE_GRIDS = "CREATE TABLE " + GRIDS_TABLE + " (" + GRID_KEY + " VARCHAR2(200) UNIQUE NOT NULL," + UTM + " VARCHAR2(10)," + SUBUTM + " VARCHAR2(10))";
    private static final String CREATE_GRID_INFO = "CREATE TABLE " + GRID_INFO_TABLE + " (" + GRID_KEY + " VARCHAR2(200) UNIQUE NOT NULL," + INFO_TYPE + " VARCHAR2(30), " + INFO_KEY + " VARCHAR2(30), " + LATITUDE + " NUMBER, " + LONGITUDE + " NUMBER)";
    private static final String CREATE_TOPICS = "CREATE TABLE " + TOPICS_TABLE + " (" + TOPIC_KEY + " VARCHAR2(200) UNIQUE NOT NULL," + TOPIC_NAME + " VARCHAR2(30)," + UTM + " VARCHAR2(10)," + SUBUTM + " VARCHAR2(10))";
    private static final String CREATE_GLOBAL_TOPICS = "CREATE TABLE " + GLOBAL_TOPICS_TABLE + " (" + TOPIC_KEY + " VARCHAR2(200) UNIQUE NOT NULL," + TOPIC_NAME + " VARCHAR2(30))";
    private static final String CREATE_ALLIANCES = "CREATE TABLE " + ALLIANCES_TABLE + " (" + ALLIANCE_KEY + " VARCHAR2(200) UNIQUE NOT NULL," + ALLIANCE_NAME + " VARCHAR2(30))";
    private static final String CREATE_ALLIANCE_MEMBERS = "CREATE TABLE " + ALLIANCE_MEMBERS_TABLE + " (" + ALLIANCE_KEY + " VARCHAR2(200)," + PLAYER_KEY + " VARCHAR2(200)," + PLAYER_NAME + " VARCHAR2(30)," + LATITUDE + " NUMBER, " + LONGITUDE + " NUMBER)";
    private static final String CREATE_PACKAGES = "CREATE TABLE " + PACKAGES_TABLE + " (" + PACKAGE_KEY + " VARCHAR2(200) UNIQUE NOT NULL," + PACKAGE_NAME + " VARCHAR2(30))";  //need to flesh this out later
    private static final String CREATE_MESSAGES = "CREATE TABLE " + MESSAGE_TABLE + "(" + MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + MESSAGE_CONTENT + " VARCHAR2(300), " + MESSAGE_KEY + " VARCHAR2(200)," + MESSAGE_TYPE + " CHAR(1), " + MESSAGE_TIME + " INTEGER," + MY_MESSAGE + " CHAR(1),"+MESSAGE_AUTHOR+" VARCHAR2(200) )";


    private DemoActivity.MessageHandler messageHandler;


    /*
    need to put other shit here.  bear in mind this will likely become a defacto class that is deliverable
    and also ported to iOS / Windows set up.  im sure they also use SQLite.
     */

    private static DBHelper dbHelper = null;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void setMessageHandler(DemoActivity.MessageHandler messageHandler){
        this.messageHandler = messageHandler;
    }

    public static DBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }

        return dbHelper;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONFIG);
        db.execSQL(CREATE_GRIDS);
        db.execSQL(CREATE_GRID_INFO);
        db.execSQL(CREATE_TOPICS);
        db.execSQL(CREATE_GLOBAL_TOPICS);
        db.execSQL(CREATE_ALLIANCES);
        db.execSQL(CREATE_ALLIANCE_MEMBERS);
        db.execSQL(CREATE_PACKAGES);
        db.execSQL(CREATE_MESSAGES);


    }

    public void test() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_MESSAGES);
    }


    public void addBaseConfiguration() {
        Config config = new Config(Configuration.PORT, "8085");
        addConfig(config);
        config = new Config(Configuration.URL, "82.23.41.68");
        addConfig(config);
        config = new Config(Configuration.UUID_ALGORITHM, "MD5");
        addConfig(config);
        config = new Config(Configuration.SSL_ALGORITHM, "");
        addConfig(config);
        config = new Config(Configuration.PLAYER_KEY, "");
        addConfig(config);
        config = new Config(Configuration.CURRENT_UTM, "");
        addConfig(config);
        config = new Config(Configuration.CURRENT_SUBUTM, "");
        addConfig(config);
        config = new Config(Configuration.GPS_UPDATE_INTERVAL, String.valueOf(DemoActivity.TWO_MINUTES));
        addConfig(config);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //give a shit.  well yes but not now.
    }

    /*
    so really i just need to flesh all of this out some more.  boring task but once done
    can then do the interesting shit.

    fleshed out enough really.  we do need helper methods...

     */


    /*
    add methods
     */

    public void addMessage(Message message) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(MESSAGE_KEY, message.getMessageKey());
        values.put(MESSAGE_CONTENT, message.getMessage());
        values.put(MESSAGE_TYPE, message.getMessageType());
        values.put(MESSAGE_TIME, message.getTime());
        values.put(MESSAGE_AUTHOR, message.getAuthor());
        values.put(MY_MESSAGE, message.isMyMessage() ? "Y" : "N");

        Log.d("adding message", "values are "+message.getMessageKey()+", "+message.getMessage()+", "+message.getTimeStamp()+" ,"+message.getMessageType());


        db.insert(MESSAGE_TABLE, null, values);

        messageHandler.handleChat(message.getMessageType());
    }

    public void addConfig(Config config) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        //values.put(CONFIG_ID, config.getId());
        values.put(CONFIG_NAME, config.getName());
        values.put(CONFIG_VALUE, config.getValue());


        db.insert(CONFIG_TABLE, null, values);

    }

    public void addGrid(Grid grid) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(GRID_KEY, grid.getKey());

        db.insert(GRIDS_TABLE, null, values);

    }

    public void addGridInfo() {


    }

    public void addAlliance(Alliance alliance) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ALLIANCE_KEY, alliance.getKey());
        values.put(ALLIANCE_NAME, alliance.getName());

        db.insert(ALLIANCES_TABLE, null, values);

        messageHandler.handleList();


    }

    public void addAllianceMember() {

    }

    public void addTopic(Topic topic) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(TOPIC_KEY, topic.getKey());
        values.put(TOPIC_NAME, topic.getName());

        db.insert(TOPICS_TABLE, null, values);

        //our topics are also global. to filter out after test phase.
        this.addGlobalTopic(topic);

        messageHandler.handleList();

    }

    public void addGlobalTopic(Topic topic) throws android.database.sqlite.SQLiteConstraintException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(TOPIC_KEY, topic.getKey());
        values.put(TOPIC_NAME, topic.getName());

        db.insert(GLOBAL_TOPICS_TABLE, null, values);

    }

    public void addPackage(Package pack) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(PACKAGE_KEY, pack.getKey());
        values.put(PACKAGE_NAME, pack.getName());

        db.insert(PACKAGES_TABLE, null, values);

    }

    /*
    delete methods..we dont delete configs?
     */

    public void deleteMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(MESSAGE_TABLE, MESSAGE_ID + " = ?", new String[]{String.valueOf(message.getId())});

    }

    public void deleteMessages(String key){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(MESSAGE_TABLE, MESSAGE_KEY + " = ?", new String[]{key});

    }


    public void deleteGrid(Grid grid) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(GRIDS_TABLE, GRID_KEY + " = ?", new String[]{grid.getKey()});
    }

    public void deleteGridInfo(Grid grid) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(GRID_INFO_TABLE, GRID_KEY + " = ?", new String[]{grid.getKey()});

    }

    public void deleteAlliance(Alliance alliance) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(ALLIANCES_TABLE, ALLIANCE_KEY + " = ?", new String[]{alliance.getKey()});
    }

    public void deleteAllianceMember() {

        SQLiteDatabase db = this.getWritableDatabase();
        //todo
    }

    public void deleteGlobalTopic(Topic topic) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(GLOBAL_TOPICS_TABLE, TOPIC_KEY + " = ?", new String[]{topic.getKey()});

    }

    public void deleteTopic(Topic topic) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TOPICS_TABLE, TOPIC_KEY + " = ?", new String[]{topic.getKey()});

    }

    public void deletePackage(oddymobstar.core.Package pack) {

        SQLiteDatabase db = this.getWritableDatabase();
    }

    /*
    update methods...no point updating a grid.  most will simply updte user dfined names etc.
     */

    public void updateConfig(Config config) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CONFIG_VALUE, config.getValue());

        db.update(CONFIG_TABLE, values, CONFIG_ID + " = ?", new String[]{String.valueOf(config.getId())});


    }

    public void updateGridItem() {

    }

    public void updateAlliance(Alliance alliance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ALLIANCE_NAME, alliance.getName());

        db.update(ALLIANCES_TABLE, values, ALLIANCE_KEY + " = ?", new String[]{alliance.getKey()});

    }

    public void updateAllianceMember() {

    }

    public void updateGlobalTopic(Topic topic) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(TOPIC_NAME, topic.getName());

        db.update(GLOBAL_TOPICS_TABLE, values, TOPIC_KEY + " = ?", new String[]{topic.getKey()});

    }

    public void updateTopic(Topic topic) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(TOPIC_NAME, topic.getName());

        db.update(TOPICS_TABLE, values, TOPIC_KEY + " = ?", new String[]{topic.getKey()});

    }

    public void updatePackage(Package pack) {

    }


    /*
    we need to get our display lists
     */

    public Cursor getMessages(String messageType, String messageKey) {
        return this.getReadableDatabase().rawQuery("SELECT " + MESSAGE_ID + " as _id," + MESSAGE_ID + "," + MESSAGE_CONTENT + "," + MESSAGE_TIME + "," + MY_MESSAGE + ","+MESSAGE_AUTHOR+" FROM " + MESSAGE_TABLE + " WHERE " + MESSAGE_TYPE + "=? AND " + MESSAGE_KEY + "=? ORDER BY " + MESSAGE_TIME + " ASC", new String[]{messageType, messageKey});
    }

    public Cursor getConfigs() {
        return this.getReadableDatabase().rawQuery("SELECT " + CONFIG_ID + " as _id," + CONFIG_ID + "," + CONFIG_NAME + "," + CONFIG_VALUE + " FROM " + CONFIG_TABLE + " ORDER BY " + CONFIG_NAME + " ASC", null);
    }


    public Cursor getTopics() {
        return this.getReadableDatabase().rawQuery("SELECT " + TOPIC_KEY + " as _id," + TOPIC_KEY + "," + TOPIC_NAME + "," + UTM + "," + SUBUTM + " FROM " + TOPICS_TABLE + " ORDER BY " + TOPIC_NAME + " ASC", null);
    }

    public Cursor getGrids() {
        return this.getReadableDatabase().rawQuery("SELECT " + GRID_KEY + " as _id," + GRID_KEY + "," + UTM + "," + SUBUTM + " FROM " + GRIDS_TABLE + " ORDER BY " + ALLIANCE_NAME + " ASC", null);
    }

    public Cursor getGridItems() {
        return null;
    }

    public Cursor getAlliances() {
        return this.getReadableDatabase().rawQuery("SELECT " + ALLIANCE_KEY + " as _id," + ALLIANCE_KEY + "," + ALLIANCE_NAME + " FROM " + ALLIANCES_TABLE + " ORDER BY " + ALLIANCE_NAME + " ASC", null);
    }

    public Cursor getPackages() {
        return this.getReadableDatabase().rawQuery("SELECT " + PACKAGE_KEY + " as _id," + PACKAGE_KEY + "," + PACKAGE_NAME + " FROM " + PACKAGES_TABLE + " ORDER BY " + PACKAGE_NAME + " ASC", null);
    }

    public Cursor getAllianceMembers() {
        return null;
    }

    public Cursor getGlobalTopics() {
        /*
        this needs to be filtered against user topics.  and maintained on request
         */
        return this.getReadableDatabase().rawQuery("SELECT " + TOPIC_KEY + " as _id," + TOPIC_KEY + "," + TOPIC_NAME + " FROM " + GLOBAL_TOPICS_TABLE + " ORDER BY " + TOPIC_NAME + " ASC", null);
    }

    /*
    get specific object.  using key.  probably to check it actually exists etc before writing a new
    entry etc in the database table.....unlikely to require other than to check something exists.
     */
    public Grid getGrid(String key) {
        return null;
    }

    public Alliance getAlliance(String key) {

        Cursor alliance = this.getReadableDatabase().rawQuery("SELECT " + ALLIANCE_KEY + " as _id," + ALLIANCE_KEY + "," + ALLIANCE_NAME + " FROM " + ALLIANCES_TABLE + " WHERE " + ALLIANCE_KEY + " =? " + " ORDER BY " + ALLIANCE_NAME + " ASC", new String[]{key});

        Alliance returnAlliance = new Alliance();

        while (alliance.moveToNext()) {
            returnAlliance.setName(alliance.getString(alliance.getColumnIndexOrThrow((DBHelper.ALLIANCE_NAME))));
            returnAlliance.setKey(alliance.getString(alliance.getColumnIndexOrThrow((DBHelper.ALLIANCE_KEY))));

        }

        alliance.close();

        return returnAlliance;

    }

    public Package getPackage(String key) {
        return null;
    }

    public Topic getTopic(String key) {

        Cursor topic = this.getReadableDatabase().rawQuery("SELECT " + TOPIC_KEY + " as _id," + TOPIC_KEY + "," + TOPIC_NAME + " FROM " + TOPICS_TABLE + " WHERE " + TOPIC_KEY + " =? " + " ORDER BY " + TOPIC_NAME + " ASC", new String[]{key});

        Topic returnTopic = new Topic();

        while (topic.moveToNext()) {
            returnTopic.setName(topic.getString(topic.getColumnIndexOrThrow((DBHelper.TOPIC_NAME))));
            returnTopic.setKey(topic.getString(topic.getColumnIndexOrThrow((DBHelper.TOPIC_KEY))));

        }

        topic.close();

        return returnTopic;

    }

    public Topic getGlobalTopic(String key) {

        Cursor topic = this.getReadableDatabase().rawQuery("SELECT " + TOPIC_KEY + " as _id," + TOPIC_KEY + "," + TOPIC_NAME + " FROM " + GLOBAL_TOPICS_TABLE + " WHERE " + TOPIC_KEY + " =? " + " ORDER BY " + TOPIC_NAME + " ASC", new String[]{key});

        Topic returnTopic = new Topic();

        while (topic.moveToNext()) {
            returnTopic.setName(topic.getString(topic.getColumnIndexOrThrow((DBHelper.TOPIC_NAME))));
            returnTopic.setKey(topic.getString(topic.getColumnIndexOrThrow((DBHelper.TOPIC_KEY))));

        }
        topic.close();

        return returnTopic;

    }

    public Config getConfig(String key) {
        return null;
    }

    /*
    due to locking issues etc and i dont want to pass the instance around but use standard methods.
     */
    public boolean hasPreLoad() {
        Cursor cursor = this.getReadableDatabase().rawQuery(
                "SELECT COUNT(1) as id FROM " + CONFIG_TABLE, null);
        int count = 0;
        while (cursor.moveToNext()) {
            count = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            cursor.moveToLast();
        }

        cursor.close();

        return count != 0;
    }


    //on shut down.
    public void close() {
        this.getWritableDatabase().close();
    }
}
