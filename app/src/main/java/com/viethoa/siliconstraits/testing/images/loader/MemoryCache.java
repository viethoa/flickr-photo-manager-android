package com.viethoa.siliconstraits.testing.images.loader;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by VietHoa on 29/07/15.
 */
public class MemoryCache {

    private Map<String, SoftReference<Bitmap>> cache;

    public MemoryCache() {
        cache = Collections.synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());
    }

    public Bitmap get(String id) {
        if (!cache.containsKey(id))
            return null;
        SoftReference<Bitmap> ref = cache.get(id);
        return ref.get();
    }

    public void put(String id, Bitmap bitmap) {
        cache.put(id, new SoftReference<Bitmap>(bitmap));
    }

    public void clear() {
        cache.clear();
    }
}
