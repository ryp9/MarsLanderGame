package uk.ac.dcs.cs39440.marslandergame;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GLSurfaceView glView = findViewById(R.id.GL_surface);
        glView.setEGLContextClientVersion(2);

    }
}
