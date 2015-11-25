package oddymobstar.graphics.programs;

import android.content.Context;

import oddymobstar.crazycourier.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by timmytime on 22/11/15.
 */
public class SkyBoxShaderProgram extends ShaderProgram {

    public SkyBoxShaderProgram(Context context){
        super(context, R.raw.skybox_vertex_shader, R.raw.skybox_fragment_shader);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
    }

    @Override
    public void setUniforms(float[] matrix) {
          //not used
    }

    @Override
    public void setUniforms(float[] matrix, int textureId) {
       glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId);
        glUniform1i(uTextureUnitLocation, 0);
    }

    @Override
    public void setUniforms(float[] matrix, float r, float g, float b) {
          //not used
    }
}
