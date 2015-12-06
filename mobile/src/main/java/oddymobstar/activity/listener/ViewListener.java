package oddymobstar.activity.listener;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import oddymobstar.activity.controller.DemoActivityController;
import oddymobstar.activity.handler.ViewHandler;
import oddymobstar.database.DBHelper;
import oddymobstar.fragment.GridFragment;
import oddymobstar.model.Message;

/**
 * Created by timmytime on 06/12/15.
 */
public class ViewListener {

    private AppCompatActivity main;
    private DemoActivityController controller;

    public ViewListener(AppCompatActivity main, DemoActivityController controller){
        this.main = main;
        this.controller = controller;
    }

    public AdapterView.OnItemClickListener getListClickListener() {

        return new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> listView, View v, int position,
                                    long id) {

            /*
            basically if they select an item we launch chat frag with an ID...
             */
                Cursor cursor = (Cursor) controller.gridFrag.getListAdapter().getItem(position);
                controller.removeFragments(false);

                android.support.v4.app.FragmentTransaction transaction = main.getSupportFragmentManager().beginTransaction();

                String key = "";
                switch (controller.gridFrag.getType()) {
                    case GridFragment.MY_ALLIANCES:
                        key = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ALLIANCE_KEY));
                        String title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.ALLIANCE_NAME));
                        controller.chatFrag.setCursor(controller.dbHelper.getMessages(Message.ALLIANCE_MESSAGE, key), key, title);

                        //and show
                        controller.viewHandler.showChat(title);

                        break;

                }


                transaction.commit();


            }
        };
    }
}
