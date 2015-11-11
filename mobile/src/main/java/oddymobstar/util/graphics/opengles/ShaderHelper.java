package oddymobstar.util.graphics.opengles;

import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

/**
 * Created by timmytime on 06/10/15.
 */
public class ShaderHelper {

    public static int compileShader(int type, String shader) {

        final int shaderId = glCreateShader(type);

        if (shaderId == 0) {
            Log.d("Shader Loader", "Shader Failed ");
        } else {
            glShaderSource(shaderId, shader);
            glCompileShader(shaderId);

            final int[] compileStatus = new int[1];
            glGetShaderiv(shaderId, GL_COMPILE_STATUS, compileStatus, 0);

            if (compileStatus[0] == 0) {
                Log.d("Compile Status Failed", glGetShaderInfoLog(shaderId));
                glDeleteShader(shaderId);
            }

        }

        return shaderId;
    }

    public static int linkProgram(int vertextShaderId, int fragmentShaderId) {
        final int programId = glCreateProgram();

        if (programId == 0) {
            Log.d("Link Program", "Create Program Failed");
        } else {
            glAttachShader(programId, vertextShaderId);
            glAttachShader(programId, fragmentShaderId);

            glLinkProgram(programId);

            final int[] linkStatus = new int[1];
            glGetProgramiv(programId, GL_LINK_STATUS, linkStatus, 0);

            if (linkStatus[0] == 0) {
                Log.d("Link Status", glGetProgramInfoLog(programId));
                glDeleteProgram(programId);
            }
        }

        return programId;
    }

    public static boolean validateProgram(int programId) {
        glValidateProgram(programId);

        final int[] validateStatus = new int[1];
        glGetProgramiv(programId, GL_VALIDATE_STATUS, validateStatus, 0);

        if (validateStatus[0] == 0) {
            Log.d("Validate Status", glGetProgramInfoLog(programId));
        }

        return validateStatus[0] != 0;
    }

    public static int buildProgram(String vertexShaderSource, String fragmentShaderSource) {
        int program = 0;

        int vertexShaderId = ShaderHelper.compileShader(GL_VERTEX_SHADER, vertexShaderSource);
        int fragmentShaderId = ShaderHelper.compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShaderId, fragmentShaderId);

        ShaderHelper.validateProgram(program);


        return program;

    }

}
