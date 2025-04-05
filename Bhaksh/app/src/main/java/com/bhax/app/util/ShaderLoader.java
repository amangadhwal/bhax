package com.bhax.app.util;

import android.content.Context;
import android.opengl.GLES30;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class for loading and compiling GLSL shaders
 */
public final class ShaderLoader {
    private static final String TAG = "ShaderLoader";

    private ShaderLoader() {
        // Private constructor to prevent instantiation
    }

    /**
     * Load shader source code from raw resource
     */
    public static String loadShaderSource(Context context, int resourceId) {
        StringBuilder shaderSource = new StringBuilder();
        
        try (InputStream is = context.getResources().openRawResource(resourceId);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to load shader source", e);
            return null;
        }
        
        return shaderSource.toString();
    }

    /**
     * Compile a shader program
     */
    public static int compileShader(int type, String shaderCode) {
        final int shader = GLES30.glCreateShader(type);
        if (shader == 0) {
            Log.e(TAG, "Failed to create shader");
            return 0;
        }

        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);

        final int[] compiled = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, "Shader compilation failed: " + GLES30.glGetShaderInfoLog(shader));
            GLES30.glDeleteShader(shader);
            return 0;
        }

        return shader;
    }

    /**
     * Create and link a shader program from vertex and fragment shaders
     */
    public static int createProgram(String vertexSource, String fragmentSource) {
        final int vertexShader = compileShader(GLES30.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        final int fragmentShader = compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource);
        if (fragmentShader == 0) {
            GLES30.glDeleteShader(vertexShader);
            return 0;
        }

        final int program = GLES30.glCreateProgram();
        if (program == 0) {
            Log.e(TAG, "Failed to create program");
            return 0;
        }

        GLES30.glAttachShader(program, vertexShader);
        GLES30.glAttachShader(program, fragmentShader);
        GLES30.glLinkProgram(program);

        final int[] linked = new int[1];
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linked, 0);
        if (linked[0] == 0) {
            Log.e(TAG, "Program linking failed: " + GLES30.glGetProgramInfoLog(program));
            GLES30.glDeleteProgram(program);
            return 0;
        }

        // Clean up shaders
        GLES30.glDeleteShader(vertexShader);
        GLES30.glDeleteShader(fragmentShader);

        return program;
    }

    /**
     * Load and create a shader program from raw resources
     */
    public static int loadProgram(Context context, int vertexResourceId, int fragmentResourceId) {
        String vertexSource = loadShaderSource(context, vertexResourceId);
        String fragmentSource = loadShaderSource(context, fragmentResourceId);

        if (vertexSource == null || fragmentSource == null) {
            return 0;
        }

        return createProgram(vertexSource, fragmentSource);
    }

    /**
     * Check for OpenGL errors
     */
    public static void checkGLError(String operation) {
        int error;
        int errorCount = 0;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) {
            Log.e(TAG, operation + ": glError " + GLUtils.getEGLErrorString(error));
            errorCount++;
            if (errorCount > 10) {
                Log.e(TAG, "Too many GL errors, breaking");
                break;
            }
        }
        if (errorCount > 0) {
            throw new RuntimeException(operation + ": GL errors encountered");
        }
    }

    /**
     * Get uniform location with error checking
     */
    public static int getUniformLocation(int program, String name) {
        final int location = GLES30.glGetUniformLocation(program, name);
        if (location == -1) {
            Log.w(TAG, "Could not find uniform: " + name);
        }
        return location;
    }

    /**
     * Get attribute location with error checking
     */
    public static int getAttributeLocation(int program, String name) {
        final int location = GLES30.glGetAttribLocation(program, name);
        if (location == -1) {
            Log.w(TAG, "Could not find attribute: " + name);
        }
        return location;
    }
}
