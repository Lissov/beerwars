package com.pl.beerwars.visual.painter;

import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapManager {
	
	private static HashMap<Integer, BitmapData> cache = new HashMap<Integer, BitmapManager.BitmapData>();
	/*int map_w = -1;
	int map_h = -1;
	int map_resId = -1;
	Bitmap map_cache;
	private Bitmap getMap(float w, float h, int resId){
		int rw = (int)w;
		int rh = (int)h;
		
		if (rw != map_w || rh != map_h || resId != map_resId) {
			Resources res = _context.getResources();
			Bitmap bmp = BitmapFactory.decodeResource(res, resId);
			map_cache = Bitmap.createScaledBitmap(bmp, rw, rh, true);
			map_w = rw;
			map_h = rh;
			map_resId = resId;
		}
		return map_cache;
	}*/
	
	public static Bitmap getBitmap(Context context, int resId, int w, int h){
		if (cache.containsKey(resId)){
			BitmapData bd = cache.get(resId);
			if (bd.size_h == h && bd.size_w == w)
				return bd.bitmap;
			
			cache.remove(resId);
		}

		Resources res = context.getResources();
		Bitmap bmp = BitmapFactory.decodeResource(res, resId);
		bmp = Bitmap.createScaledBitmap(bmp, w, h, true);
		BitmapData newbd = new BitmapData();
		newbd.bitmap = bmp;
		newbd.size_h = h;
		newbd.size_w = w;
		cache.put(resId, newbd);
		
		return bmp;
	}
	
	protected static class BitmapData{
		public Bitmap bitmap;
		public int size_w;
		public int size_h;
	}
}
