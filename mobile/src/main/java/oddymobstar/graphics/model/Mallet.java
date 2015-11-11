package oddymobstar.graphics.model;

import java.util.List;

import oddymobstar.graphics.data.VertexArray;
import oddymobstar.graphics.programs.ColorShaderProgram;
import oddymobstar.util.graphics.opengles.Geometry;
import oddymobstar.util.graphics.opengles.ObjectBuilder;

/**
 * Created by timmytime on 28/10/15.
 */
public class Mallet {

    private static final int POSITION_COMPONENT_COUNT = 3;


    public final float radius, height;

    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawList;


    public Mallet(float radius, float height, int numPointsAroundMallet) {

        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createMallet(new Geometry.Point(0f, 0f, 0f), radius, height, numPointsAroundMallet);

        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generatedData.getVertexData());
        drawList = generatedData.getDrawList();

    }

    public void bindData(ColorShaderProgram colorProgram) {

        vertexArray.setVertextAttribPointer(0,
                colorProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                0);

    }

    public void draw() {
        for (ObjectBuilder.DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}
