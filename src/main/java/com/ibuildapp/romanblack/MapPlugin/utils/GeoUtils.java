package com.ibuildapp.romanblack.MapPlugin.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ibuildapp.romanblack.MapPlugin.R;
import com.ibuildapp.romanblack.MapPlugin.model.MapItem;
import com.ibuildapp.romanblack.MapPlugin.model.MarkerWrapper;
import com.restfb.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class GeoUtils {

    private static final String LOG_NAME = "GEO";
    private static Map<String, Integer> icons = new HashMap<String, Integer>(){{
            put("cars.png", R.drawable.map_cars);
            put("company.png", R.drawable.map_company);
            put("default.png", R.drawable.map_default);
            put("education.png", R.drawable.map_education);
            put("food.png", R.drawable.map_food);
            put("info.png", R.drawable.map_info);
            put("it.png", R.drawable.map_it);
            put("medical_case.png", R.drawable.map_medical_case);
            put("shopping.png", R.drawable.map_shopping);
            put("sports.png", R.drawable.map_sports);
            put("travel.png", R.drawable.map_travel);
        }
    };

    public static Observable<MarkerWrapper> createOptions(final Context context, final MapItem item){
        return Observable.create(new Observable.OnSubscribe<MarkerWrapper>() {
            @Override
            public void call(Subscriber<? super MarkerWrapper> subscriber) {
                Log.e(LOG_NAME, Thread.currentThread().getName() + " " + Thread.currentThread().getId());
                MarkerOptions builder = new MarkerOptions();
                builder.position(new LatLng(item.getLatitude(), item.getLongitude()));

                boolean containsInRes = false;
                Integer iconRes = -1;
                for (Map.Entry<String, Integer> entry : icons.entrySet())
                    if (!StringUtils.isBlank(item.getIconUrl())
                            && item.getIconUrl().contains(entry.getKey())){
                        containsInRes = true;
                        iconRes = entry.getValue();
                    }

                try {
                    if (containsInRes)
                        builder.icon(BitmapDescriptorFactory.fromResource(iconRes));
                    else{
                        Bitmap icon = Glide.with(context).load(item.getIconUrl()).asBitmap().into(-1, -1).get();
                        builder.icon(BitmapDescriptorFactory.fromBitmap(icon));
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                subscriber.onNext(new MarkerWrapper(builder, item));
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public static class CenterCalculator{
        private LatLng center;
        private int initZoom;

        public void init(Set<Marker> markers){
            double maxLat = -90;
            double maxLng = -180;
            double minLat = 90;
            double minLng = 180;

            for (Marker marker : markers){
                if (marker.getPosition().latitude > maxLat)
                    maxLat = marker.getPosition().latitude;

                if (marker.getPosition().longitude > maxLng)
                    maxLng = marker.getPosition().longitude;

                if (marker.getPosition().latitude < minLat)
                    minLat = marker.getPosition().latitude;

                if (marker.getPosition().longitude < minLng)
                    minLng = marker.getPosition().longitude;
            }
            center =  new LatLng((maxLat + minLat) / 2, (maxLng + minLng) / 2);

            double gr = Math.abs(maxLng - minLng);
            int z = 1;
            if (gr > (120)) {
                z = 1;
            } else if (gr > (60)) {
                z = 2;
            } else if (gr > (30)) {
                z = 3;
            } else if (gr > (15)) {
                z = 4;
            } else if (gr > (8)) {
                z = 5;
            } else if (gr > (4)) {
                z = 6;
            } else if (gr > (2)) {
                z = 7;
            } else if (gr > (1)) {
                z = 8;
            } else if (gr > (0.5)) {
                z = 9;
            } else if(gr > (0.3)){
                z = 10;
            }else if(gr > (0.1)){
                z = 11;
            }else z = 12;

            initZoom = z;
        }

        public LatLng getCenter() {
            return center;
        }

        public int getInitZoom() {
            return initZoom;
        }
    }
}