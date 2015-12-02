package oddymobstar.activity.helpers;

import android.content.Context;
import android.content.res.ColorStateList;

/**
 * Created by timmytime on 02/12/15.
 */
public class Materials {

    private Context context;

    private ColorStateList subUtmColorList;
    private ColorStateList utmColorList;
    private ColorStateList allianceColorList;
    private ColorStateList chatColorList;



    public Materials(Context context){
        this.context = context;
    }


    public void setUpColorLists() {
        subUtmColorList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed}, //1
                        new int[]{android.R.attr.state_focused}, //2
                        new int[]{android.R.attr.state_focused, android.R.attr.state_pressed} //3
                },
                new int[]{
                        context.getResources().getColor(android.R.color.holo_orange_dark), //1
                        context.getResources().getColor(android.R.color.holo_orange_dark), //2
                        context.getResources().getColor(android.R.color.holo_orange_dark) //3
                });

        utmColorList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed}, //1
                        new int[]{android.R.attr.state_focused}, //2
                        new int[]{android.R.attr.state_focused, android.R.attr.state_pressed} //3
                },
                new int[]{
                        context.getResources().getColor(android.R.color.holo_purple), //1
                        context.getResources().getColor(android.R.color.holo_purple), //2
                        context.getResources().getColor(android.R.color.holo_purple) //3
                }
        );

        allianceColorList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed}, //1
                        new int[]{android.R.attr.state_focused}, //2
                        new int[]{android.R.attr.state_focused, android.R.attr.state_pressed} //3
                },
                new int[]{
                        context.getResources().getColor(android.R.color.holo_red_dark), //1
                        context.getResources().getColor(android.R.color.holo_red_dark), //2
                        context.getResources().getColor(android.R.color.holo_red_dark) //3
                }
        );

        chatColorList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed}, //1
                        new int[]{android.R.attr.state_focused}, //2
                        new int[]{android.R.attr.state_focused, android.R.attr.state_pressed} //3
                },
                new int[]{
                        context.getResources().getColor(android.R.color.holo_green_dark), //1
                        context.getResources().getColor(android.R.color.holo_green_dark), //2
                        context.getResources().getColor(android.R.color.holo_green_dark) //3
                }
        );

    }



    public ColorStateList getSubUtmColorList(){
        return subUtmColorList;
    }

    public ColorStateList getUtmColorList(){
        return utmColorList;
    }

    public ColorStateList getAllianceColorList(){
        return allianceColorList;
    }

    public ColorStateList getChatColorList(){
        return chatColorList;
    }


}
