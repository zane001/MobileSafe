package com.zane001.mobilesafe.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by zane001 on 2014/7/6.
 */
public class GPSInfoProvider {
    private static GPSInfoProvider mGPSInfoProvider;
    private static LocationManager lm;
    private static MyListener listener;
    private static SharedPreferences sp;

    public synchronized static GPSInfoProvider getInstance(Context context) {
        if(mGPSInfoProvider == null) {
            mGPSInfoProvider = new GPSInfoProvider();
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE); //最精准的精确度
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_HIGH);
            criteria.setAltitudeRequired(true); //获取海拔信息
            criteria.setSpeedRequired(true);
            String provider = lm.getBestProvider(criteria, true); //获取到当前手机最好用的位置提供者
            listener = new GPSInfoProvider().new MyListener();
            lm.requestLocationUpdates(provider, 60000, 100, listener);  //更新位置
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return mGPSInfoProvider;
    }

    public void stopListen() {
        lm.removeUpdates(listener);
        listener = null;
    }

    protected class MyListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            String latitude = "latitude: " + location.getLatitude();    //纬度
            String longitude = "longitude: " + location.getLongitude(); //经度
            String meter = "accuracy: " + location.getAccuracy(); //精确度
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("last_location", latitude + "-" + longitude + "-" + meter);
            editor.commit();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    /**
     * 获取手机位置
     * @return
     */
    public String getLocation() {
        return sp.getString("last_location", "");
    }
}
