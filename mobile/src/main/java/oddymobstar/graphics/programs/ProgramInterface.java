package oddymobstar.graphics.programs;

/**
 * Created by timmytime on 10/11/15.
 */
public interface ProgramInterface {

    public void setUniforms(float[] matrix);

    public void setUniforms(float[] matrix, int textureId);

    public void setUniforms(float[] matrix, float r, float g, float b);
}
