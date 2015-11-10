package crazycourier.oddymobstar.crazycourier.database;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;

import oddymobstar.database.DBHelper;
import oddymobstar.model.Alliance;
import oddymobstar.model.Config;
import oddymobstar.model.Grid;
import oddymobstar.model.Message;
import oddymobstar.model.Package;

/**
 * Created by timmytime on 05/05/15.
 */
public class DBHelperTest extends AndroidTestCase {

    private DBHelper dbHelper;


    public void setUp() throws Exception {
        super.setUp();

        final SQLiteDatabase db = SQLiteDatabase.create(null);

        Context context = new MockContext() {
            public SQLiteDatabase openOrCreateDatabase(String file, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
                return db;
            }

            ;
        };


        dbHelper = new DBHelper(context);

        assertTrue("database", db.isOpen());

    }

    public void tearDown() {
        dbHelper.close();
    }


    //now we have a database we need to test all the calls etc...
    public void testDatabase() {

        //test the adds
        dbHelper.addBaseConfiguration();

        Config config = new Config("test", "test", "markup", 1, false);
        dbHelper.addConfig(config);

        Alliance alliance = new Alliance();
        alliance.setKey("key");
        alliance.setName("alliance");

        dbHelper.addAlliance(alliance, false);

      /*  InAllianceMessage allianceMessage = new InAllianceMessage();


        dbHelper.addAllianceMember(alliance); */
        //dbHelper.addGridInfo();
        Grid grid = new Grid();
        grid.setKey("key");
        grid.setUtm("utm");
        grid.setSubUtm("subutm");
        dbHelper.addGrid(grid);

        Message message = new Message();
        message.setAuthor("author");
        message.setMyMessage("N");
        message.setMessageKey("key");
        message.setTime(1000l);


        dbHelper.addMessage(message);

        oddymobstar.model.Package pack = new Package();
        pack.setKey("key");
        pack.setName("pack");

        dbHelper.addPackage(pack);

        //test the cursors
        Cursor c = dbHelper.getAllianceMembers();
        c = dbHelper.getAlliances();
        c = dbHelper.getConfigs();
        c = dbHelper.getGridItems();
        c = dbHelper.getGrids();
        c = dbHelper.getPackages();
        c = dbHelper.getMessages("A", "key");

        //these test models..to
        //now test individual cursors... note must are still stubs...
        //  assertTrue("grid utm", dbHelper.getGrid("key").getUtm().equals("utm"));
        //  assertTrue("grid sub", dbHelper.getGrid("key").getSubUtm().equals("subutm"));
        //  assertTrue("package", dbHelper.getPackage("key").getName().equals("pack"));
        assertTrue("alliance", dbHelper.getAlliance("key").getName().equals("alliance"));
        //   assertTrue("config", dbHelper.getConfig("test").getName().equals("test"));

        //test the update
        dbHelper.updateAlliance(alliance);
        dbHelper.updateConfig(config);
        dbHelper.updatePackage(pack);


        //test the delete
        dbHelper.deleteAlliance(alliance);
        dbHelper.deleteGrid(grid);
        dbHelper.deleteMessages("key");
        dbHelper.deletePackage(pack);

    }


}
