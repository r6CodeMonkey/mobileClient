package oddymobstar.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by root on 25/02/15.
 */
public class UTM {

    private int utmLong;
    private String utmLat = "";

    public UTM(String utm) {

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(utm);

        while (matcher.find()) {
            utmLong = Integer.valueOf(matcher.group());
        }

        pattern = Pattern.compile("[A-Z]");
        matcher = pattern.matcher(utm);

        while (matcher.find()) {
            utmLat = matcher.group();
        }


    }


    public int getUtmLong() {
        return utmLong;
    }

    public String getUtmLat() {
        return utmLat;
    }


    public static void main(String[] args) {

        UTM utm = new UTM("E31");

        System.out.println(utm.getUtmLat());
        System.out.println(utm.getUtmLong());


    }


}
