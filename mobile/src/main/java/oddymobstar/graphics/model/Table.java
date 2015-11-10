package oddymobstar.graphics.model;

import oddymobstar.graphics.data.VertexArray;
import oddymobstar.graphics.programs.TextureShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;

/**
 * Created by timmytime on 28/10/15.
 */
public class Table {

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDS_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDS_COMPONENT_COUNT) * VertexArray.BYTES_PER_FLOAT;

    //ok bored. need to think about this too. XYST
    private static final float[] VERTEX_DATA = {
            0f, 0f, 0.5f, 0.5f,
            -0.5f, -0.8f, 0f, 0.9f,
            0.5f, -0.8f, 1f, 0.9f,
            0.5f, 0.8f, 1f, 0.1f,
            -0.5f, 0.8f, 0f, 0.1f,
            -0.5f, -0.8f, 0f, 0.9f
    };

    private final VertexArray vertexArray = new VertexArray(VERTEX_DATA);

    public Table() {

    }

    public void bindData(TextureShaderProgram textureProgram) {

        vertexArray.setVertextAttribPointer(0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);

        vertexArray.setVertextAttribPointer(POSITION_COMPONENT_COUNT,
                textureProgram.getaTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDS_COMPONENT_COUNT,
                STRIDE);

    }

    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }


}
