package oddymobstar.activity.handler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;

import oddymobstar.activity.DemoActivity;
import oddymobstar.activity.helper.MaterialsHelper;
import oddymobstar.fragment.ChatFragment;
import oddymobstar.fragment.GridFragment;
import oddymobstar.util.widget.ChatPost;
import oddymobstar.util.widget.CreateView;
import oddymobstar.util.widget.GridDialog;

/**
 * Created by timmytime on 03/12/15.
 */
public class MaterialsHandler {

    //private Context context;
    private DemoActivity main;
    private MaterialsHelper materialsHelper;

    public MaterialsHandler(DemoActivity main, MaterialsHelper materialsHelper){
        this.main = main;
        this.materialsHelper = materialsHelper;
    }


    public void handleChatFAB(ChatFragment chatFrag, final boolean hide) {

        final ChatPost hiddenChatPost = chatFrag.getHiddenChatPost();

        int cxIn = (hiddenChatPost.getLeft() + hiddenChatPost.getRight()) / 2;
        int cyIn = (hiddenChatPost.getTop() + hiddenChatPost.getBottom()) / 2;

        int radiusIn = Math.max(hiddenChatPost.getWidth(), hiddenChatPost.getHeight());

        int cxOut = (materialsHelper.floatingActionButton.getLeft() + materialsHelper.floatingActionButton.getRight()) / 2;
        int cyOut = (materialsHelper.floatingActionButton.getTop() + materialsHelper.floatingActionButton.getBottom()) / 2;

        int radiusOut = materialsHelper.floatingActionButton.getWidth();

        Animator animatorIn, animatorOut = null;

        if (hide) {
            animatorIn = ViewAnimationUtils.createCircularReveal(hiddenChatPost, cxIn, cyIn, 0, radiusIn);
        } else {
            animatorIn = ViewAnimationUtils.createCircularReveal(materialsHelper.floatingActionButton, cxOut, cyOut, 0, radiusOut);
        }
        //   animatorIn.setDuration(500);
        animatorIn.setInterpolator(new AccelerateInterpolator());

        if (hide) {
            animatorOut = ViewAnimationUtils.createCircularReveal(materialsHelper.floatingActionButton, cxOut, cyOut, radiusOut, 0);
        } else {
            animatorOut = ViewAnimationUtils.createCircularReveal(hiddenChatPost, cxIn, cyIn, radiusIn, 0);
        }
        //   animatorOut.setDuration(300);
        animatorOut.setInterpolator(new AccelerateInterpolator());

        animatorOut.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (hide) {
                    materialsHelper.floatingActionButton.setVisibility(View.INVISIBLE);

                    InputMethodManager imm = (InputMethodManager) main.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                    hiddenChatPost.requestFocus();

                } else {
                    hiddenChatPost.setVisibility(View.GONE);

                    InputMethodManager imm = (InputMethodManager) main.getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(hiddenChatPost.getWindowToken(), 0);


                }

            }
        });

        if (hide) {
            hiddenChatPost.setVisibility(View.VISIBLE);
        } else {
            materialsHelper.floatingActionButton.setVisibility(View.VISIBLE);
        }


        animatorOut.start();
        animatorIn.start();

    }


    public void handleAllianceFAB(GridFragment gridFrag, final boolean hide) {

        final CreateView hiddenCreateView = gridFrag.getHiddenCreateView();


        int cxIn = (hiddenCreateView.getLeft() + hiddenCreateView.getRight()) / 2;
        int cyIn = (hiddenCreateView.getTop() + hiddenCreateView.getBottom()) / 2;

        int radiusIn = Math.max(hiddenCreateView.getWidth(), hiddenCreateView.getHeight());

        int cxOut = (materialsHelper.floatingActionButton.getLeft() + materialsHelper.floatingActionButton.getRight()) / 2;
        int cyOut = (materialsHelper.floatingActionButton.getTop() + materialsHelper.floatingActionButton.getBottom()) / 2;

        int radiusOut = materialsHelper.floatingActionButton.getWidth();

        Animator animatorIn, animatorOut = null;

        if (hide) {
            animatorIn = ViewAnimationUtils.createCircularReveal(hiddenCreateView, cxIn, cyIn, 0, radiusIn);
        } else {
            animatorIn = ViewAnimationUtils.createCircularReveal(materialsHelper.floatingActionButton, cxOut, cyOut, 0, radiusOut);
        }

        //   animatorIn.setDuration(500);
        animatorIn.setInterpolator(new AccelerateInterpolator());

        if (hide) {
            animatorOut = ViewAnimationUtils.createCircularReveal(materialsHelper.floatingActionButton, cxOut, cyOut, radiusOut, 0);
        } else {
            animatorOut = ViewAnimationUtils.createCircularReveal(hiddenCreateView, cxIn, cyIn, radiusIn, 0);
        }

        // animatorOut.setDuration(300);
        animatorOut.setInterpolator(new AccelerateInterpolator());

        animatorOut.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);


                if (hide) {
                    materialsHelper.floatingActionButton.setVisibility(View.INVISIBLE);

                    InputMethodManager imm = (InputMethodManager) main.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                    hiddenCreateView.requestFocus();

                } else {
                    hiddenCreateView.setVisibility(View.GONE);

                    InputMethodManager imm = (InputMethodManager) main.getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(hiddenCreateView.getWindowToken(), 0);


                }


            }
        });

        if (hide) {
            hiddenCreateView.setVisibility(View.VISIBLE);
        } else {
            materialsHelper.floatingActionButton.setVisibility(View.VISIBLE);
        }


        animatorOut.start();
        animatorIn.start();


    }


    public void handleSearchFab() {


        int cxOut = (materialsHelper.floatingActionButton.getLeft() + materialsHelper.floatingActionButton.getRight()) / 2;
        int cyOut = (materialsHelper.floatingActionButton.getTop() + materialsHelper.floatingActionButton.getBottom()) / 2;

        int radiusOut = materialsHelper.floatingActionButton.getWidth();


        Animator animatorOut = ViewAnimationUtils.createCircularReveal(materialsHelper.floatingActionButton, cxOut, cyOut, radiusOut, 0);

        //animatorOut.setDuration(300);
        animatorOut.setInterpolator(new AccelerateInterpolator());

        animatorOut.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                materialsHelper.floatingActionButton.setVisibility(View.INVISIBLE);

                FragmentTransaction transaction = main.getSupportFragmentManager()
                        .beginTransaction();

               main.createGridDialog();

                main.getGridDialog().show(transaction, "dialog");


            }
        });

        animatorOut.start();


    }


}
