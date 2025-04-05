package com.bhax.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling OpenGL textures in ritual effects
 */
public class TextureLoader {
    private static final String TAG = "TextureLoader";
    private static final Map<Integer, Integer> textureCache = new HashMap<>();

    /**
     * Load texture from resource
     */
    public static int loadTexture(Context context, int resourceId) {
        // Check cache first
        if (textureCache.containsKey(resourceId)) {
            return textureCache.get(resourceId);
        }

        final int[] textureHandle = new int[1];
        GLES30.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] == 0) {
            Log.e(TAG, "Failed to generate texture");
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        if (bitmap == null) {
            Log.e(TAG, "Failed to load texture resource");
            GLES30.glDeleteTextures(1, textureHandle, 0);
            return 0;
        }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0]);

        // Set filtering
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);

        // Set wrapping mode
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);

        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);

        bitmap.recycle();
        
        // Cache the texture
        textureCache.put(resourceId, textureHandle[0]);
        
        return textureHandle[0];
    }

    /**
     * Create noise texture for glitch effects
     */
    public static int createNoiseTexture(int width, int height) {
        final int[] textureHandle = new int[1];
        GLES30.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] == 0) {
            Log.e(TAG, "Failed to generate noise texture");
            return 0;
        }

        // Generate random noise data
        ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);
        buffer.order(ByteOrder.nativeOrder());
        for (int i = 0; i < width * height * 4; i += 4) {
            byte noiseValue = (byte) (Math.random() * 255);
            buffer.put(i, noiseValue);      // R
            buffer.put(i + 1, noiseValue);  // G
            buffer.put(i + 2, noiseValue);  // B
            buffer.put(i + 3, (byte) 255);  // A
        }
        buffer.position(0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0]);

        // Set filtering
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);

        // Set wrapping mode
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);

        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height, 0,
                GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, buffer);

        return textureHandle[0];
    }

    /**
     * Create a dynamic texture for realtime effects
     */
    public static int createDynamicTexture(int width, int height) {
        final int[] textureHandle = new int[1];
        GLES30.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] == 0) {
            Log.e(TAG, "Failed to generate dynamic texture");
            return 0;
        }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0]);

        // Set filtering
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);

        // Allocate storage
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height, 0,
                GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);

        return textureHandle[0];
    }

    /**
     * Delete a texture and remove from cache
     */
    public static void deleteTexture(int textureId) {
        final int[] textureHandle = {textureId};
        GLES30.glDeleteTextures(1, textureHandle, 0);
        textureCache.values().remove(textureId);
    }

    /**
     * Clear texture cache
     */
    public static void clearCache() {
        for (Integer textureId : textureCache.values()) {
            final int[] textureHandle = {textureId};
            GLES30.glDeleteTextures(1, textureHandle, 0);
        }
        textureCache.clear();
    }

    /**
     * Update dynamic texture with new bitmap data
     */
    public static void updateTexture(int textureId, Bitmap bitmap) {
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
        GLUtils.texSubImage2D(GLES30.GL_TEXTURE_2D, 0, 0, 0, bitmap);
    }

    /**
     * Check if texture exists in cache
     */
    public static boolean isTextureCached(int resourceId) {
        return textureCache.containsKey(resourceId);
    }
}
