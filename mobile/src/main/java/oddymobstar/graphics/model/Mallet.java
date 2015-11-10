package oddymobstar.graphics.model;

import oddymobstar.graphics.data.VertexArray;
import oddymobstar.graphics.programs.ColorShaderProgram;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by timmytime on 28/10/15.
 */
public class Mallet {

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * VertexArray.BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            0f, -0.4f, 0f, 0f, 1f,
            0f, 0.4f, 1f, 0f, 0f
    };

    private final VertexArray vertexArray = new VertexArray(VERTEX_DATA);


    public Mallet() {

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
        glDrawArrays(GL_POINTS, 0, 2);
    }
}
