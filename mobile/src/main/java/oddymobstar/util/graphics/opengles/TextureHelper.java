package oddymobstar.util.graphics.opengles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

/**
 * Created by timmytime on 28/10/15.
 */
public class TextureHelper {

    public static int loadTexture(Context context, int resourceID) {
        final int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            Log.d("texture loader", "failed to load texture");

            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceID, options);

        if (bitmap == null) {
            Log.d("Texture loader", "Bitmap failed");
            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }

        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        bitmap.recycle();

        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);

        return textureObjectIds[0];

    }

    public static int loadCubeMap(Context context, int[] cubeResources) {
        final int[] textureObjectIds = new int[1];

        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            Log.d("Texture loader", "Cube Map failed");
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap[] cubeBitMaps = new Bitmap[6];

        for (int i = 0; i < cubeBitMaps.length; i++) {
            cubeBitMaps[i] = BitmapFactory.decodeResource(context.getResources(), cubeResources[i], options);

            if (cubeBitMaps[i] == null) {
                Log.d("Texture loader", "Bitmap cube face failed");
                glDeleteTextures(1, textureObjectIds, 0);
                return 0;
            }
        }

        glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0]);

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitMaps[0], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitMaps[1], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitMaps[2], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitMaps[3], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitMaps[4], 0);
        texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitMaps[5], 0);

        glBindTexture(GL_TEXTURE_2D, 0);

        for (Bitmap bitmap : cubeBitMaps) {
            bitmap.recycle();
        }

        return textureObjectIds[0];

    }
}
