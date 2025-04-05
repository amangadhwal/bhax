package com.bhax.app.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import androidx.core.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Manages file storage operations for ritual content and effects
 */
public class RitualStorageManager {
    private static final String TAG = "RitualStorageManager";
    private static final String RITUAL_DIR = "ritual";
    private static final String EFFECTS_DIR = "effects";
    private static final String CACHE_DIR = "ritual_cache";
    private static final String DEBUG_DIR = "debug";
    private static final String EXPORT_DIR = "ritual_exports";
    private static final String SHADER_DIR = "shaders";
    
    private final Context context;
    private final File ritualDir;
    private final File effectsDir;
    private final File cacheDir;
    private final File exportDir;
    private final File shaderDir;

    public RitualStorageManager(Context context) {
        this.context = context;
        this.ritualDir = new File(context.getFilesDir(), RITUAL_DIR);
        this.effectsDir = new File(context.getFilesDir(), EFFECTS_DIR);
        this.cacheDir = new File(context.getCacheDir(), CACHE_DIR);
        this.exportDir = new File(context.getExternalCacheDir(), EXPORT_DIR);
        this.shaderDir = new File(context.getFilesDir(), SHADER_DIR);
        
        createDirectories();
    }

    private void createDirectories() {
        ritualDir.mkdirs();
        effectsDir.mkdirs();
        cacheDir.mkdirs();
        exportDir.mkdirs();
        shaderDir.mkdirs();
    }

    /**
     * Create a temporary file for ritual effects
     */
    public File createTempEffectFile(String prefix, String suffix) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String fileName = prefix + "_" + timeStamp + suffix;
        return new File(cacheDir, fileName);
    }

    /**
     * Get file for storing shader code
     */
    public File getShaderFile(String shaderName) {
        return new File(shaderDir, shaderName + ".glsl");
    }

    /**
     * Get file for ritual effect resources
     */
    public File getRitualResourceFile(String resourceName) {
        return new File(ritualDir, resourceName);
    }

    /**
     * Get URI for sharing debug data
     */
    public Uri getDebugFileUri(File file) {
        return FileProvider.getUriForFile(context,
                context.getPackageName() + ".provider",
                file);
    }

    /**
     * Create export file for ritual effect
     */
    public File createExportFile(String effectName) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String fileName = "ritual_" + effectName + "_" + timeStamp + ".zip";
        return new File(exportDir, fileName);
    }

    /**
     * Clear temporary files
     */
    public void clearTempFiles() {
        try {
            deleteRecursive(cacheDir);
            cacheDir.mkdirs();
        } catch (Exception e) {
            Log.e(TAG, "Error clearing temp files", e);
        }
    }

    /**
     * Delete old exports
     */
    public void cleanupExports() {
        File[] exports = exportDir.listFiles();
        if (exports != null) {
            long now = System.currentTimeMillis();
            long maxAge = 7 * 24 * 60 * 60 * 1000L; // 7 days
            
            for (File export : exports) {
                if (now - export.lastModified() > maxAge) {
                    export.delete();
                }
            }
        }
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] children = fileOrDirectory.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        fileOrDirectory.delete();
    }

    /**
     * Check if external storage is available
     */
    public boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Get available space for ritual effects
     */
    public long getAvailableSpace() {
        return context.getFilesDir().getFreeSpace();
    }

    /**
     * Check if enough space is available
     */
    public boolean hasEnoughSpace(long requiredBytes) {
        return getAvailableSpace() > requiredBytes;
    }

    /**
     * Get total size of ritual files
     */
    public long getRitualFilesSize() {
        return getFolderSize(ritualDir) + getFolderSize(effectsDir);
    }

    private long getFolderSize(File directory) {
        long size = 0;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    size += file.length();
                } else {
                    size += getFolderSize(file);
                }
            }
        }
        return size;
    }
}
