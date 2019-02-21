package uk.ac.dcs.cs39440.marslandergame;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Lander {

    private List<String> verticesList;
    private List<String> faceList;

    private FloatBuffer verticesBuffer;
    private ShortBuffer facesBuffer;

    private int program;

    private int vertexShader;
    private int fragmentShader;

    public Lander(Context context) {
        verticesList = new ArrayList<>();
        faceList = new ArrayList<>();
        readOBJFile(context);
        setUpBufferObjects();
        compileShaders(context);
        //talks to the shaders
        createProgram();
    }

    private void createProgram() {
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);
    }

    /**
     * TODO refactor this method
     *
     * @param context
     */
    private void compileShaders(Context context) {
        //vertex shader to text
        InputStream vertexShaderStream = context.getResources().openRawResource(R.raw.vertex_shader);
        String vertexShaderCode = null;
        try {
            vertexShaderCode = IOUtils.toString(vertexShaderStream, Charset.defaultCharset());
            vertexShaderStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //fragment shader to text
        InputStream fragmentShaderStream = context.getResources().openRawResource(R.raw.fragment_shader);
        String fragmentShaderCode = null;
        try {
            fragmentShaderCode = IOUtils.toString(fragmentShaderStream, Charset.defaultCharset());
            fragmentShaderStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, vertexShaderCode);

        fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode);

        GLES20.glCompileShader(vertexShader);
        GLES20.glCompileShader(fragmentShader);
    }

    private void setUpBufferObjects() {
        //vertices buffer
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(verticesList.size() * 3 * 4);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        verticesBuffer = vertexByteBuffer.asFloatBuffer();

        //face buffer
        ByteBuffer faceByteBuffer = ByteBuffer.allocateDirect(faceList.size() * 3 * 2);
        faceByteBuffer.order(ByteOrder.nativeOrder());
        facesBuffer = faceByteBuffer.asShortBuffer();

        populateVerticesBuffer();
        populateFacesBuffer();
    }

    private void populateVerticesBuffer() {
        for (String vertex : verticesList) {
            String coords[] = vertex.split(" ");
            float x = Float.parseFloat(coords[1]);
            float y = Float.parseFloat(coords[2]);
            float z = Float.parseFloat(coords[3]);
            verticesBuffer.put(x);
            verticesBuffer.put(y);
            verticesBuffer.put(z);
        }
        //reset buffer position
        verticesBuffer.position(0);
    }

    private void populateFacesBuffer() {
        for (String face : faceList) {
            String coords[] = face.split(" ");
            //face is 3 vertices to make a triangle
            float vertex1 = Short.parseShort(coords[1]);
            float vertex2 = Short.parseShort(coords[2]);
            float vertex3 = Short.parseShort(coords[3]);
            //indices start from 1 not 0 so we take 1
            verticesBuffer.put(vertex1 - 1);
            verticesBuffer.put(vertex2 - 1);
            verticesBuffer.put(vertex3 - 1);
        }
        //reset buffer position
        verticesBuffer.position(0);
    }

    private void readOBJFile(Context context) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(context.getAssets().open("LanderwHeatShield.obj"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert scanner != null;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("v ")) {
                verticesList.add(line);
            } else if (line.startsWith("f ")) {
                faceList.add(line);
            }
        }
        scanner.close();
    }


    public void draw() {
        int position = GLES20.glGetAttribLocation(program, "position");
        GLES20.glEnableVertexAttribArray(position);
        GLES20.glVertexAttribPointer(position, 3, GLES20.GL_FLOAT, false, 3 * 4, verticesBuffer);

        //projection matrices
        float[] projectionMatrix = new float[16];
        float[] viewMatrix = new float[16];
        float[] productMatrix = new float[16];

        //left,right,bottom,top,near and far clip planes

        Matrix.frustumM(projectionMatrix, 0,
                -1, 1,
                -1, 1,
                2, 9);

        //camera position and points it is looking at
        Matrix.setLookAtM(viewMatrix, 0,
                0, 3, -4,
                0, 0, 0,
                0, 1, 0);


        Matrix.multiplyMM(productMatrix, 0,
                projectionMatrix, 0,
                viewMatrix, 0);

        //a handle to vertex shaders matrix variable
        int matrix = GLES20.glGetUniformLocation(program, "matrix");
        GLES20.glUniformMatrix4fv(matrix, 1, false, productMatrix, 0);

        //link vertices
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, faceList.size() * 3, GLES20.GL_UNSIGNED_SHORT, facesBuffer);

        //disable attribute handler
        GLES20.glDisableVertexAttribArray(position);

    }
}
