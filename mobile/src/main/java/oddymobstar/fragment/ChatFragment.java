package oddymobstar.fragment;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;

import oddymobstar.activity.DemoActivity;
import oddymobstar.adapter.ChatAdapter;
import oddymobstar.crazycourier.R;
import oddymobstar.database.DBHelper;
import oddymobstar.util.ChatPost;

/**
 * Created by root on 04/04/15.
 */
public class ChatFragment extends Fragment {

    private Cursor chat;
    private String key;

    private ChatPost chatPost;

    private CursorAdapter adapter = null;


    public ChatFragment() {
        setRetainInstance(true);
        this.chat = chat;
    }

    public String getKey(){
        return key;
    }

    public void setCursor(Cursor chat, String key) {
        this.key = key;
        this.chat = chat;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.chat_fragment, container, false);


        //need to sort out the list shit too....
        adapter = new ChatAdapter(this.getActivity(), chat, false);

        ListView lv = (ListView) view.findViewById(R.id.list);

        chatPost = (ChatPost) view.findViewById(R.id.chat_post);


        if(!view.isInEditMode()) {
            Button deletePosts = (Button) view.findViewById(R.id.delete_posts);
            deletePosts.setTypeface(DemoActivity.getFont());
        }

        lv.setDivider(null);
        lv.setDividerHeight(0);

        lv.setAdapter(adapter);


        return view;

    }

    public void refreshAdapter(Cursor cursor){

        adapter.changeCursor(cursor);
    }



    public void cancelPost() {
        chatPost.cancelPost();
    }

    public String getPost() {
        return chatPost.getPost();
    }




}
