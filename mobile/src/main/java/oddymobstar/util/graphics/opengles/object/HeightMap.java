package oddymobstar.util.graphics.opengles.object;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.android.gms.maps.model.Polygon;

import oddymobstar.graphics.data.VertexArray;
import oddymobstar.graphics.model.ModelInterface;
import oddymobstar.graphics.programs.ShaderProgram;
import oddymobstar.util.graphics.opengles.Geometry;
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
    private static final int NORMAL_COMPONENT_COUNT = 3;
    private static final int TOTAL_COMPONENT_COUNT = POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * VertexArray.BYTES_PER_FLOAT;

    private final int width, height, numElements;
    private final VertexBuffer vertexBuffer;
    private final IndexBuffer indexBuffer;

    public HeightMap(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        if (width * height > 65536) {
            throw new RuntimeException("Height map too large for index buffer " + (width * height));
        }

        numElements = calculateNumElements();
        vertexBuffer = new VertexBuffer(loadBitmapData(bitmap));
        indexBuffer = new IndexBuffer(createIndexData());
    }

    private float[] loadBitmapData(Bitmap bitmap) {
        final int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            bitmap.recycle(); // is issue with crash i think!!.   need to sort this out due to removing fragment.

        final float[] heightMapVertices = new float[width * height * TOTAL_COMPONENT_COUNT];
        int offset = 0;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {

                final Geometry.Point point = getPoint(pixels, row, col);


                heightMapVertices[offset++] = point.x;
                heightMapVertices[offset++] = point.y;
                heightMapVertices[offset++] = point.z;


                final Geometry.Point top = getPoint(pixels, row - 1, col);
                final Geometry.Point left = getPoint(pixels, row, col - 1);
                final Geometry.Point right = getPoint(pixels, row, col + 1);
                final Geometry.Point bottom = getPoint(pixels, row + 1, col);

                final Geometry.Vector rightToLeft = Geometry.vectorBetween(right, left);
                final Geometry.Vector topToBottom = Geometry.vectorBetween(top, bottom);
                final Geometry.Vector normal = rightToLeft.crossProduct(topToBottom).normalize();

                heightMapVertices[offset++] = normal.x;
                heightMapVertices[offset++] = normal.y;
                heightMapVertices[offset++] = normal.z;
            }
        }
        return heightMapVertices;
    }

    private Geometry.Point getPoint(int[] pixels, int row, int col) {
        float x = ((float)col / (float)(width - 1)) - 0.5f;
        float z = ((float)row / (float)(height - 1)) - 0.5f;

        row = clamp(row, 0, width - 1);
        col = clamp(col, 0, height - 1);

        float y = (float)Color.red(pixels[(row * height) + col]) / (float)255;

        return new Geometry.Point(x, -y, z);
    }

    private int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }



    private int calculateNumElements() {
        return (width - 1) * (height - 1) * 2 * 3;
    }

    private short[] createIndexData() {
        final short[] indexData = new short[numElements];
        int offset = 0;
        for (int row = 1; row < height - 1; row++) {
            for (int col = 1; col < width - 1; col++) {
                short topLeftIndex = (short) (row * width + col);
                short topRightIndex = (short) (row * width + col + 1);
                short bottomLeftIndex = (short) ((row + 1) * width + col);
                short bottomRightIndex = (short) ((row + 1) * width + col + 1);

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
        vertexBuffer.setVertexAttribPointer(0,
                shaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, STRIDE);

        vertexBuffer.setVertexAttribPointer(
                POSITION_COMPONENT_COUNT * VertexArray.BYTES_PER_FLOAT,
                shaderProgram.getNormalAttributeLocation(),
                NORMAL_COMPONENT_COUNT, STRIDE);
    }
}
