package oddymobstar.graphics.model;

import java.util.List;

import oddymobstar.graphics.data.VertexArray;
import oddymobstar.graphics.programs.ColorShaderProgram;
import oddymobstar.util.graphics.opengles.Geometry;
import oddymobstar.util.graphics.opengles.objects.ObjectBuilder;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by timmytime on 28/10/15.
 */
public class Mallet {

    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * VertexArray.BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            0f, -0.4f, 0f, 0f, 1f,
            0f, 0.4f, 1f, 0f, 0f
    };

    public final float radius, height;

    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawList;


    public Mallet(float radius, float height, int numPointsAroundMallet) {

        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createMallet(new Geometry.Point(0f,0f,0f), radius, height, numPointsAroundMallet);

        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generatedData.getVertexData());
        drawList = generatedData.getDrawList();

    }

    public void bindData(ColorShaderProgram colorProgram) {

        vertexArray.setVertextAttribPointer(0,
                colorProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);

        vertexArray.setVertextAttribPointer(POSITION_COMPONENT_COUNT,
                colorProgram.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT,
                STRIDE);
    }

    public void draw() {
        for (ObjectBuilder.DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}
