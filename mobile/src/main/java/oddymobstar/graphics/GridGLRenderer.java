package oddymobstar.graphics;

import android.content.Context;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import oddymobstar.crazycourier.R;
import oddymobstar.util.RawResourceLoader;
import oddymobstar.util.ShaderHelper;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;


/**
 * Created by timmytime on 06/10/15.
 */
public class GridGLRenderer implements GLSurfaceView.Renderer {

    private Context context;

    //shaders info
    private String vertexShaderCode, fragmentShaderCode;
    private static final String A_COLOR = "a_Color";
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation, aColorLocation;

    private static final int POSITION_COMPONENT_COUNT = 2;  //3 if xyz of course..
    private static final int COLOR_COMPONENT_COUNT = 3;  //4 if rgba
    private static final int BYTES_PER_FLOAT = 4;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private FloatBuffer vertexData;
    private int programId;

    public GridGLRenderer(Context context) {
        this.context = context;

        float[] tableVertices = {
                //triangle fan now x,y,r,g,b,
                0f, 0f, 1f, 1f, 1f,
                -0.5f,0f,0.9f,0.9f,0.9f,
                -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
                0f,-0.5f,0.9f,0.9f,0.9f,
                0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
                0.5f,0f,0.9f,0.9f,0.9f,
                0.5f, 0.5f, 0.7f, 0.7f, 0.7f,
                0f,0.5f,0.9f,0.9f,0.9f,
                -0.5f, 0.5f, 0.7f, 0.7f, 0.7f,
                -0.5f,0f,0.9f,0.9f,0.9f,

                //line 1
                -0.5f, 0f, 1f, 0f, 0f,
                0.5f, 0f, 1f, 0f, 0f,
                //mallets
                0f, -0.25f, 0f, 0f, 1f,
                0f, 0.25f, 1f, 0f, 0f
        };

        vertexData = ByteBuffer
                .allocateDirect(tableVertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();


        vertexData.put(tableVertices);

        //grab shaders
        vertexShaderCode = RawResourceLoader.readRawResource(context, R.raw.vertext_shader);
        fragmentShaderCode = RawResourceLoader.readRawResource(context, R.raw.fragment_shader);

    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        int vertexShaderId = ShaderHelper.compileShader(GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShaderId = ShaderHelper.compileShader(GL_FRAGMENT_SHADER, fragmentShaderCode);

        boolean valid = false;

        if (vertexShaderId != 0 && fragmentShaderId != 0) {
            //if they are zero its broken.
            programId = ShaderHelper.linkProgram(vertexShaderId, fragmentShaderId);

            if (programId != 0) {
                valid = ShaderHelper.validateProgram(programId);
            }

        }

        if (valid) {
            glUseProgram(programId);
            aColorLocation = glGetAttribLocation(programId, A_COLOR);
            aPositionLocation = glGetAttribLocation(programId, A_POSITION);

            vertexData.position(0);
            glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
            glEnableVertexAttribArray(aPositionLocation);

            vertexData.position(POSITION_COMPONENT_COUNT);
            glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
            glEnableVertexAttribArray(aColorLocation);


        }


    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {

        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {

        glClear(GL_COLOR_BUFFER_BIT);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 10); //0 is first element, 6 is last element of triangles.  ie 3 vertices per triangle.

        glDrawArrays(GL_LINES, 10, 2);

        glDrawArrays(GL_POINTS, 12, 1);

        glDrawArrays(GL_POINTS, 13, 1);


    }
}
