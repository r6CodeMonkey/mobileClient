package oddymobstar.graphics.programs;

import android.content.Context;

import oddymobstar.util.graphics.opengles.RawResourceLoader;
import oddymobstar.util.graphics.opengles.ShaderHelper;

import static android.opengl.GLES20.glUseProgram;

/**
 * Created by timmytime on 10/11/15.
 */
public abstract class ShaderProgram implements ProgramInterface {


    protected static final String U_COLOR = "u_Color";
    protected static final String A_POSITION = "a_Position";
    protected static final String U_MATRIX = "u_Matrix";


    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    protected int aPositionLocation, uColorLocation, aTextureCoordinatesLocation, uTextureUnitLocation, uMatrixLocation;


    protected int program;

    protected ShaderProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId) {

        program = ShaderHelper.buildProgram(RawResourceLoader.readRawResource(context, vertexShaderResourceId),
                RawResourceLoader.readRawResource(context, fragmentShaderResourceId));
    }

    public void useProgram() {
        glUseProgram(program);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getColorAttributeLocation() {
        return uColorLocation;
    }

    public int getaTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }

}
