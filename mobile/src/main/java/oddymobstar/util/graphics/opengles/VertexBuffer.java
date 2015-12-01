package oddymobstar.util.graphics.opengles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import oddymobstar.graphics.data.VertexArray;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by timmytime on 25/11/15.
 */
public class VertexBuffer {

    private final int bufferId;

    public VertexBuffer(float[] vertexData){
        final int buffers[] = new int[1];
        glGenBuffers(buffers.length, buffers, 0);

        if(buffers[0] == 0){
            throw new RuntimeException("Could not create Vertex Buffer Object");
        }

        bufferId = buffers[0];

        glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);

        FloatBuffer vertexArray = ByteBuffer.allocateDirect(vertexData.length * VertexArray.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);

        vertexArray.position(0);

        glBufferData(GL_ARRAY_BUFFER, vertexArray.capacity() * VertexArray.BYTES_PER_FLOAT, vertexArray, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation, int componentCount, int stride){
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, stride, dataOffset);
        glEnableVertexAttribArray(attributeLocation);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
