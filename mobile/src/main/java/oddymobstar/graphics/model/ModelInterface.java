package oddymobstar.graphics.model;

import oddymobstar.graphics.programs.ShaderProgram;

/**
 * Created by timmytime on 10/11/15.
 */
public interface ModelInterface {

    public void draw();

    public void bindData(ShaderProgram shaderProgram);
}
