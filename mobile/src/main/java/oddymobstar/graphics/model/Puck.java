package oddymobstar.graphics.model;

import java.util.List;

import oddymobstar.graphics.data.VertexArray;
import oddymobstar.graphics.programs.ShaderProgram;
import oddymobstar.util.graphics.opengles.Geometry;
import oddymobstar.util.graphics.opengles.ObjectBuilder;

/**
 * Created by timmytime on 10/11/15.
 */
public class Puck implements ModelInterface {

    private static final int POSITION_COMPONENT_COUNT = 3;
    public final float radius, height;

    private VertexArray vertexArray;
    private List<ObjectBuilder.DrawCommand> drawList;

    public Puck(float radius, float height, int numPointsAroundPuck) {
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createPuck(
                new Geometry.Cylinder(new Geometry.Point(0f, 0f, 0f), radius, height), numPointsAroundPuck);

        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generatedData.getVertexData());
        drawList = generatedData.getDrawList();


    }

    @Override
    public void draw() {
        for (ObjectBuilder.DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }

    @Override
    public void bindData(ShaderProgram shaderProgram) {
        vertexArray.setVertextAttribPointer(0, shaderProgram.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, 0);
    }
}
