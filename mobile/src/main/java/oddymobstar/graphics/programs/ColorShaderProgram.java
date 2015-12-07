package oddymobstar.graphics.programs;

import android.content.Context;

import oddymobstar.crazycourier.R;
import oddymobstar.util.graphics.opengles.Geometry;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by timmytime on 10/11/15.
 */
public class ColorShaderProgram extends ShaderProgram {


    public ColorShaderProgram(Context context) {
        super(context, R.raw.vertex_shader, R.raw.fragment_shader);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        uColorLocation = glGetUniformLocation(program, U_COLOR);

    }

    @Override
    public void setUniforms(float[] matrix, float r, float g, float b) {

        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform4f(uColorLocation, r, g, b, 1f);

    }

    @Override
    public void setUniforms(float[] matrix, int textureId) {
        //not used
    }

    public void setUniforms(float[] matrix) {
        //not used
    }

    @Override
    public void setUniforms(float[] matrix, Geometry.Vector vectorToLight){

    }


    @Override
    public void setUniforms(float[] mvMatrix,
                            float[] it_mvMatrix,
                            float[] mvpMatrix,
                            float[] vectorToDirectionalLight,
                            float[] pointLightPositions,
                            float[] pointLightColors){

    }


}
