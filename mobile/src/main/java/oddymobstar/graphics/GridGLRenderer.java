package oddymobstar.graphics;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import oddymobstar.crazycourier.R;
import oddymobstar.graphics.model.Mallet;
import oddymobstar.graphics.model.Puck;
import oddymobstar.graphics.model.Table;
import oddymobstar.graphics.programs.ColorShaderProgram;
import oddymobstar.graphics.programs.HeightMapShaderProgram;
import oddymobstar.graphics.programs.SkyBoxShaderProgram;
import oddymobstar.graphics.programs.TextureShaderProgram;
import oddymobstar.util.graphics.opengles.Geometry;
import oddymobstar.util.graphics.opengles.MatrixHelper;
import oddymobstar.util.graphics.opengles.TextureHelper;
import oddymobstar.util.graphics.opengles.object.HeightMap;
import oddymobstar.util.graphics.opengles.object.SkyBox;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LESS;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;


/**
 * Created by timmytime on 06/10/15.
 */
public class GridGLRenderer implements GLSurfaceView.Renderer {

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    private final float[] invertedViewProjectionMatrix = new float[16];
    private final float[] it_modelViewMatrix = new float[16];
    private final float[] viewMatrixForSkybox = new float[16];
    private final float[] tempMatrix = new float[16];
    private final float leftBound = -0.5f;
    private final float rightBound = 0.5f;
    private final float farBound = -0.8f;
    private final float nearBound = 0.8f;
    private Context context;
    private SkyBox skyBox;
    private HeightMap heightMap;
    private Table table;
    private Mallet mallet;
    private Puck puck;
    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;
    private SkyBoxShaderProgram skyBoxShaderProgram;
    private HeightMapShaderProgram heightMapShaderProgram;
    private int texture, skyBoxTexture;
    private boolean malletPressed = false;
    private Geometry.Point blueMalletPosition, previousBlueMalletPosition, puckPosition;
    private Geometry.Vector puckVector;
    private float xRotation, yRotation;

    private final Geometry.Vector vectorToLight = new Geometry.Vector(0.61f, 0.64f, -0.47f).normalize();


    public GridGLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glEnable(GL_DEPTH_TEST);


        skyBox = new SkyBox();
        heightMap = new HeightMap(((BitmapDrawable) context.getDrawable(R.drawable.heightmap)).getBitmap());

        table = new Table();
        mallet = new Mallet(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);

        blueMalletPosition = new Geometry.Point(0f, mallet.height / 2f, 0.4f);
        puckPosition = new Geometry.Point(0f, puck.height / 2f, 0f);
        puckVector = new Geometry.Vector(0f, 0f, 0f);

        colorShaderProgram = new ColorShaderProgram(context);
        textureShaderProgram = new TextureShaderProgram(context);
        skyBoxShaderProgram = new SkyBoxShaderProgram(context);
        heightMapShaderProgram = new HeightMapShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.drawable.airhockey);

        skyBoxTexture = TextureHelper.loadCubeMap(context,
                new int[]{R.drawable.sea_lf, R.drawable.sea_rt, R.drawable.sea_dn, R.drawable.sea_up, R.drawable.sea_ft, R.drawable.sea_bk});


    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {


        glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 100f);
        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);

    }

    @Override
    public void onDrawFrame(GL10 glUnused) {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        puckPosition = puckPosition.translate(puckVector);

        //pretty crude shit.  i can use my proper stuff eventually on this, ie physics etc.  not required tho here
        if (puckPosition.x < leftBound + puck.radius || puckPosition.x > rightBound - puck.radius) {
            puckVector = new Geometry.Vector(-puckVector.x, puckVector.y, puckVector.z);
            puckVector = puckVector.scale(0.9f);
        }

        if (puckPosition.z < farBound + puck.radius || puckPosition.z > nearBound - puck.radius) {
            puckVector = new Geometry.Vector(puckVector.x, puckVector.y, -puckVector.z);
            puckVector = puckVector.scale(0.9f);
        }

        puckPosition = new Geometry.Point(clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius),
                puckPosition.y,
                clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius));

        puckVector = puckVector.scale(0.99f);

        drawHeightMap();
        drawSkyBox();

        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);


        drawAirHockey();


    }


    private void drawHeightMap() {

        //still not working.  probably the crappy image itself.  upside, i dont need this for what i am about to do
        //therefore, fuck this off and simply use a texture as the floor, as we want the texture to scroll, rather than
        //scroll the non rendering height map.  debug confirms it exists so it must be file.  not using file from demo which
        //actually violates the test routine anyway (ie its too big).  note is another GL bug report to review!!
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0f, -2f, 0f);
        scaleM(modelMatrix, 0, 10f, 0f, 10f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);

        heightMapShaderProgram.useProgram();

        heightMapShaderProgram.setUniforms(modelViewProjectionMatrix,vectorToLight);
        heightMap.bindData(heightMapShaderProgram);
        heightMap.draw();

     }

    private void drawSkyBox() {
        //draw the skybox.
        setIdentityM(modelMatrix, 0);
        //pointless get height map working  translateM(modelMatrix, 0, 0f, -5f, 0f);
        scaleM(modelMatrix, 0, 100f, 100f, 100f);
        rotateM(modelMatrix, 0, -yRotation, 1f, 0f, 0f);
        rotateM(modelMatrix, 0, -xRotation, 0f, 1f, 0f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);

        glDepthFunc(GL_LEQUAL);
        skyBoxShaderProgram.useProgram();
        skyBoxShaderProgram.setUniforms(modelViewProjectionMatrix, skyBoxTexture);
        skyBox.bindData(skyBoxShaderProgram);
        skyBox.draw();
        glDepthFunc(GL_LESS);
    }

    private void drawAirHockey() {


        //set table position
        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);

        textureShaderProgram.useProgram();
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(textureShaderProgram);
        table.draw();

        //set mallet 1
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0f, mallet.height / 2f, -0.4f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);

        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(colorShaderProgram);
        mallet.draw();


        //set mallet 2
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        mallet.draw();


        //set puck
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, puckPosition.x, puckPosition.y, puckPosition.z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);

        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 0f, 1f, 0f);
        puck.bindData(colorShaderProgram);
        puck.draw();


    }

    /*
     touch event handlers
     */
    public void handleTouchPress(float normalizedX, float normalizedY) {

        Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
        Geometry.Sphere malletBoundingSphere = new Geometry.Sphere(new Geometry.Point(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z), mallet.height / 2f);

        malletPressed = Geometry.intersects(malletBoundingSphere, ray);
    }


    public void handleCamera(float deltaX, float deltaY) {
        xRotation += deltaX / 16f;
        yRotation += deltaY / 16f;

        if (yRotation < -90) {
            yRotation = -90;
        } else if (yRotation > 90) {
            yRotation = 90;
        }

        //   updateViewMatrices();
    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {

        if (malletPressed) {

            Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
            Geometry.Plane plane = new Geometry.Plane(new Geometry.Point(0, 0, 0), new Geometry.Vector(0, 1, 0));
            Geometry.Point touchedPoint = Geometry.intersectionPoint(ray, plane);

            previousBlueMalletPosition = blueMalletPosition;
            blueMalletPosition = new Geometry.Point(
                    clamp(touchedPoint.x, leftBound + mallet.radius, rightBound - mallet.radius),
                    mallet.height / 2f,
                    clamp(touchedPoint.z, 0f + mallet.radius, nearBound - mallet.radius));

            float distance = Geometry.vectorBetween(blueMalletPosition, puckPosition).length();

            if (distance < (puck.radius + mallet.radius)) {

                puckVector = Geometry.vectorBetween(previousBlueMalletPosition, blueMalletPosition);
            }
        }
    }

    /*
     create a Ray
     */
    private Geometry.Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY) {

        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};

        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);

        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Geometry.Point nearPointRay = new Geometry.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Geometry.Point farPointRay = new Geometry.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        return new Geometry.Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay));
    }

    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];

    }

    private float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }


}
