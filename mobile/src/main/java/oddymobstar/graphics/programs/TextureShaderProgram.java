package oddymobstar.graphics.programs;


import android.content.Context;

import oddymobstar.crazycourier.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by timmytime on 10/11/15.
 */
public class TextureShaderProgram extends ShaderProgram {


    public TextureShaderProgram(Context context) {
        super(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);

    }

    @Override
    public void setUniforms(float[] matrix, int textureId) {

        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

        glActiveTexture(GL_TEXTURE0);

        glBindTexture(GL_TEXTURE_2D, textureId);

        glBindTexture(uTextureUnitLocation, 0);

    }

    @Override
    public void setUniforms(float[] matrix) {
        //not used
    }

    @Override
    public void setUniforms(float[] matrix, float r, float g, float b) {
        //not used
    }


}
