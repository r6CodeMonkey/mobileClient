package oddymobstar.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Created by timmytime on 06/10/15.
 */
public class RawResourceLoader {

    public static String readRawResource(Context context, int resourceId){

        StringBuilder stringBuilder = new StringBuilder();

        try{

            InputStream is = context.getResources().openRawResource(resourceId);
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader buffer = new BufferedReader(reader);

            String nextLine;
            while((nextLine = buffer.readLine()) != null){
                    stringBuilder.append(nextLine + '\n');

            }


        }catch(IOException ioe){
            throw new RuntimeException(ioe);

        }catch(Resources.NotFoundException nf){
            throw new RuntimeException(nf);
        }

        Log.d("shader", stringBuilder.toString());

        return stringBuilder.toString();
    }

}
