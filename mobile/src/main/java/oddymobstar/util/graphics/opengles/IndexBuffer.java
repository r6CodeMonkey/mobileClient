package oddymobstar.util.graphics.opengles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glGenBuffers;

/**
 * Created by timmytime on 25/11/15.
 */
public class IndexBuffer {

    private final int bufferId;

    public static final int BYTES_PER_SHORT = 2;

    public IndexBuffer(short[] indexData) {
        final int buffers[] = new int[1];
        glGenBuffers(buffers.length, buffers, 0);

        if (buffers[0] == 0) {
            throw new RuntimeException("Could not create Index Buffer Object");
        }

        bufferId = buffers[0];

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers[0]);

        ShortBuffer shortBuffer = ByteBuffer.allocateDirect(indexData.length * BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(indexData);

        shortBuffer.position(0);

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, shortBuffer.capacity() * BYTES_PER_SHORT, shortBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }


    public int getBufferId() {
        return bufferId;
    }

}
