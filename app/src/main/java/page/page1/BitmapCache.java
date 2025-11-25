package page.page1;

import android.graphics.Bitmap;
import android.util.LruCache;


public class BitmapCache {
    private static BitmapCache instance;
    private LruCache<String, Bitmap> memoryCache;

    private BitmapCache() {

        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;

        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {

                return bitmap.getByteCount() / 1024;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldBitmap, Bitmap newBitmap) {

                if (evicted && oldBitmap != null && !oldBitmap.isRecycled()) {
                    oldBitmap.recycle();
                }
            }
        };
    }

    public static synchronized BitmapCache getInstance() {
        if (instance == null) {
            instance = new BitmapCache();
        }
        return instance;
    }


    public void addBitmapToCache(String key, Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) return;
        if (getBitmapFromCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }


    public Bitmap getBitmapFromCache(String key) {
        Bitmap bitmap = memoryCache.get(key);
        if (bitmap != null && bitmap.isRecycled()) {
            memoryCache.remove(key);
            return null;
        }
        return bitmap;
    }


    public void clearCache() {
        memoryCache.evictAll();
    }
}