package uk.ac.dcs.cs39440.marslandergame;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private float[] rotationMatrix = new float[16];
    private Triangle mTriangle;
    private float previousX;
    private float previousY;
    private GLSurfaceView glView;
    private final float TouchScaleFactor = 1.0f / 320;
    private float mAngle;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - previousX;
                float dy = y - previousY;

                //reverse above mid line
                if (y > glView.getHeight() / 2) {
                    dx = dx * -1;
                }

                //reverse on left of midline
                if (x < glView.getWidth() / 2) {
                    dy = dy * -1;
                }

                setAngle(getAngle() + ((dx + dy) * TouchScaleFactor));
                glView.requestRender();

        }
        return super.onTouchEvent(event);
    }

    private float getAngle() {
            return mAngle;
        }
    private void setAngle(float angle){
        mAngle = angle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        glView = findViewById(R.id.GL_surface);
        glView.setEGLContextClientVersion(2);
        glView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            /**
             * how often we render
             */
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                //we only re render when there is a change
//                glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

                mTriangle = new Triangle();
            }

            @Override
            /**
             * define height and width of the viewport
             */
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                GLES20.glViewport(0, 0, width, height);

                float ratio = (float) width / height;

                // this projection matrix is applied to object coordinates
                // in the onDrawFrame() method
                Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
            }

            @Override
            /**
             * actually drawing the frame
             */
            public void onDrawFrame(GL10 gl) {
                float[] scratch = new float[16];
                // Redraw background color
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

                // Set the camera position (View matrix)
                Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

                // Calculate the projection and view transformation
                Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

                // Create a rotation transformation for the triangle
//                long time = SystemClock.uptimeMillis() % 4000L;
//                float angle = 0.090f * ((int) time);
                Matrix.setRotateM(rotationMatrix, 0, mAngle, 0, 0, -1.0f);

                // Combine the rotation matrix with the projection and camera view
                // Note that the vPMatrix factor *must be first* in order
                // for the matrix multiplication product to be correct.
                Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);

                // Draw triangle
                mTriangle.draw(scratch);
            }
        });

    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
