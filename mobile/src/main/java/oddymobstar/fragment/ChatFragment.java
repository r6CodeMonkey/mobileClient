package oddymobstar.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import oddymobstar.adapter.ChatAdapter;
import oddymobstar.crazycourier.R;
import oddymobstar.util.ChatPost;

/**
 * Created by root on 04/04/15.
 */
public class ChatFragment extends Fragment {

    private Cursor chat;

    private ChatPost chatPost;

    public ChatFragment() {
        this.chat = chat;
    }

    public void setCursor(Cursor chat) {
        this.chat = chat;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.chat_fragment, container, false);

        //need to sort out the list shit too....
        ChatAdapter chatAdapter = new ChatAdapter(this.getActivity(), chat, false);

        ListView lv = (ListView) view.findViewById(R.id.list);

        chatPost = (ChatPost) view.findViewById(R.id.chat_post);


        lv.setDivider(null);
        lv.setDividerHeight(0);

        lv.setAdapter(chatAdapter);


        return view;

    }

    public void cancelPost() {
        chatPost.cancelPost();
    }

    public String getPost() {
        return chatPost.getPost();
    }


}
