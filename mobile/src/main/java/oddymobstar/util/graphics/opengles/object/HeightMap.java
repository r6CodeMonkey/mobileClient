package oddymobstar.util.graphics.opengles.object;

import android.graphics.Bitmap;
import android.graphics.Color;

import oddymobstar.graphics.model.ModelInterface;
import oddymobstar.graphics.programs.ShaderProgram;
import oddymobstar.util.graphics.opengles.IndexBuffer;
import oddymobstar.util.graphics.opengles.VertexBuffer;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glDrawElements;

/**
 * Created by timmytime on 25/11/15.
 */
public class HeightMap implements ModelInterface {

    private static final int POSITION_COMPONENT_COUNT = 3;

    private final int width, height, numElements;
    private final VertexBuffer vertexBuffer;
    private final IndexBuffer indexBuffer;

    public HeightMap(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        if (width * height > 65536) {
            throw new RuntimeException("Height map too large for index buffer "+(width * height ));
        }

        numElements = calculateNumElements();
        vertexBuffer = new VertexBuffer(loadBitmapData(bitmap));
        indexBuffer = new IndexBuffer(createIndexData());
    }

    private float[] loadBitmapData(Bitmap bitmap) {
        final int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();

        final float[] heightMapVertices = new float[width * height * POSITION_COMPONENT_COUNT];
        int offset = 0;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {

                final float xPosition = ((float) col / (float) (width - 1)) - 0.5f;
                final float yPosition = (float) Color.red(pixels[(row * height) + col]) / (float) 255;
                final float zPosition = ((float) row / (float) (height - 1)) - 0.5f;

                heightMapVertices[offset++] = xPosition;
                heightMapVertices[offset++] = yPosition;
                heightMapVertices[offset++] = zPosition;
            }
        }
        return heightMapVertices;
    }


    private int calculateNumElements() {
        return (width - 1) * (height - 1) * 2 * 3;
    }

    private short[] createIndexData() {
        final short[] indexData = new short[numElements];
        int offset = 0;
        for (int row = 1; row < height -1; row++) {
            for (int col = 1; col < width -1; col++) {
                short topLeftIndex = (short) (row * width + col);
                short topRightIndex = (short) (row * width + col + 1);
                short bottomLeftIndex = (short) ((row+1) * width + col);
                short bottomRightIndex = (short) ((row+1)* width + col+1);

                indexData[offset++] = topLeftIndex;
                indexData[offset++] = bottomLeftIndex;
                indexData[offset++] = topRightIndex;

                indexData[offset++] = topRightIndex;
                indexData[offset++] = bottomLeftIndex;
                indexData[offset++] = bottomRightIndex;

            }
        }

        return indexData;

    }

    @Override
    public void draw() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getBufferId());
        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

    }

    @Override
    public void bindData(ShaderProgram shaderProgram) {
        vertexBuffer.setVertexAttribPointer(0, shaderProgram.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, 0);
    }
}
