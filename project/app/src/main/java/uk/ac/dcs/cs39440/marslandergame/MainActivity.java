package uk.ac.dcs.cs39440.marslandergame;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    private Lander lander;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final GLSurfaceView glView = findViewById(R.id.GL_surface);
        glView.setEGLContextClientVersion(2);
        glView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            /**
             * how often we render
             */
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                //we only re render when there is a change
                glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                lander = new Lander(getApplicationContext());
            }

            @Override
            /**
             * define height and width of the viewport
             */
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                GLES20.glViewport(0, 0, width, height);
            }

            @Override
            /**
             * actually drawing the frame
             */
            public void onDrawFrame(GL10 gl) {
                lander.draw();
            }
        });

    }
}
