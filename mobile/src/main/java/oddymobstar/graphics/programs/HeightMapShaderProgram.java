package oddymobstar.graphics.programs;

import android.content.Context;

import oddymobstar.crazycourier.R;
import oddymobstar.util.graphics.opengles.Geometry;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by timmytime on 25/11/15.
 */
public class HeightMapShaderProgram extends ShaderProgram {


    public HeightMapShaderProgram(Context context) {
        super(context, R.raw.heightmap_vertex_shader, R.raw.heightmap_fragment_shader);

        uVectorToLightLocation = glGetUniformLocation(program, U_VECTOR_TO_LIGHT);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
  /*      uIT_MVMatrixLocation = glGetUniformLocation(program, U_IT_MV_MATRIX);
        uMVPMatrixLocation = glGetUniformLocation(program, U_MVP_MATRIX);
        uPointLightPositionsLocation = glGetUniformLocation(program, U_POINT_LIGHT_POSITIONS);
        uPointLightColorsLocation = glGetUniformLocation(program, U_POINT_LIGHT_COLORS);
    */    aNormalLocation = glGetAttribLocation(program, A_NORMAL);

    }

    @Override
    public void setUniforms(float[] matrix) {
     //   glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

    }

    @Override
    public void setUniforms(float[] matrix, int textureId) {

    }

    @Override
    public void setUniforms(float[] matrix, float r, float g, float b) {

    }

    @Override
    public void setUniforms(float[] matrix, Geometry.Vector vectorToLight){
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform3f(uVectorToLightLocation,
                vectorToLight.x, vectorToLight.y, vectorToLight.z);
    }

    @Override
    public void setUniforms(float[] mvMatrix,
                            float[] it_mvMatrix,
                            float[] mvpMatrix,
                            float[] vectorToDirectionalLight,
                            float[] pointLightPositions,
                            float[] pointLightColors){

        glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0);
        glUniformMatrix4fv(uIT_MVMatrixLocation, 1, false, it_mvMatrix, 0);
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);
        glUniform3fv(uVectorToLightLocation, 1, vectorToDirectionalLight, 0);

        glUniform4fv(uPointLightPositionsLocation, 3, pointLightPositions, 0);
        glUniform3fv(uPointLightColorsLocation, 3, pointLightColors, 0);

    }
}
