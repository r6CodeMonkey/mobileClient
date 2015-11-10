package oddymobstar.graphics.programs;

import android.content.Context;

import oddymobstar.crazycourier.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by timmytime on 10/11/15.
 */
public class ColorShaderProgram extends ShaderProgram {


    public ColorShaderProgram(Context context) {
        super(context, R.raw.vertex_shader, R.raw.fragment_shader);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);

    }

    @Override
    public void setUniforms(float[] matrix) {

        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

    }

    @Override
    public void setUniforms(float[] matrix, int textureId) {
        //not used
    }


}
