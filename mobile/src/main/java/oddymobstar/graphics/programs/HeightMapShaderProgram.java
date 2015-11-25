package oddymobstar.graphics.programs;

import android.content.Context;

import oddymobstar.crazycourier.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by timmytime on 25/11/15.
 */
public class HeightMapShaderProgram extends ShaderProgram {


    public HeightMapShaderProgram(Context context){
        super(context, R.raw.heightmap_vertex_shader, R.raw.heightmap_fragment_shader);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);

    }

    @Override
    public void setUniforms(float[] matrix) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

    }

    @Override
    public void setUniforms(float[] matrix, int textureId) {

    }

    @Override
    public void setUniforms(float[] matrix, float r, float g, float b) {

    }
}
