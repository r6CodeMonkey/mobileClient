package oddymobstar.graphics.programs;

import oddymobstar.util.graphics.opengles.Geometry;

/**
 * Created by timmytime on 10/11/15.
 */
public interface ProgramInterface {

    public void setUniforms(float[] matrix);

    public void setUniforms(float[] matrix, int textureId);

    public void setUniforms(float[] matrix, float r, float g, float b);

    public void setUniforms(float[] matrix, Geometry.Vector vectorToLight);

    public void setUniforms(float[] mvMatrix,
                            float[] it_mvMatrix,
                            float[] mvpMatrix,
                            float[] vectorToDirectionalLight,
                            float[] pointLightPositions,
                            float[] pointLightColors);
}
