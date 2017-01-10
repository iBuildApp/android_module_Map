package com.ibuildapp.romanblack.MapPlugin;


import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.appbuilder.sdk.android.AppBuilderModuleMainAppCompat;
import com.appbuilder.sdk.android.Utils;
import com.appbuilder.sdk.android.Widget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.ibuildapp.romanblack.MapPlugin.adapters.InfoMapAdapter;
import com.ibuildapp.romanblack.MapPlugin.dialog.RouteSelectDialog;
import com.ibuildapp.romanblack.MapPlugin.model.MapItem;
import com.ibuildapp.romanblack.MapPlugin.model.MarkerWrapper;
import com.ibuildapp.romanblack.MapPlugin.utils.GeoUtils;
import com.ibuildapp.romanblack.MapPlugin.utils.MapConstants;
import com.ibuildapp.romanblack.MapPlugin.utils.rx.SimpleSubscriber;
import com.ibuildapp.romanblack.MapPlugin.xml.EntityParser;
import com.restfb.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;

public class MapPlugin extends AppBuilderModuleMainAppCompat implements OnMapReadyCallback {

    private String title;
    private String xml;

    private View backToMyLocation;
    private View userDirection;

    private boolean showLocationButton;
    private ArrayList<MapItem> items;

    private GoogleMap mapObject;
    private GeoUtils.CenterCalculator calculator;

    @Override
    public void create() {
        initContent();
        setContentView(R.layout.map_main);
        backToMyLocation = findViewById(R.id.map_main_back_to_my_location);
        userDirection = findViewById(R.id.map_main_user_direction);

        setTopBarBackgroundColor(getResources().getColor(R.color.map_title_color));

        if (StringUtils.isBlank(title))
            setTopBarTitle(title);
        else setTopBarTitle(getResources().getString(R.string.map_widget_name));

        setTopBarLeftButtonTextAndColor(getResources().getString(R.string.map_home), getResources().getColor(android.R.color.black),
                true, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
        setTopBarTitleColor(getResources().getColor(android.R.color.black));

        Schedulers.computation().createWorker().schedule(new Action0() {
            @Override
            public void call() {
                EntityParser parser = new EntityParser(xml);
                parser.parse();
                items = parser.getItems();
                showLocationButton = parser.showCurrentLocation;
                AndroidSchedulers.mainThread().createWorker().schedule(new Action0() {
                    @Override
                    public void call() {
                        postUIInit();
                    }
                });
            }
        });
    }

    private void postUIInit() {
        if (!showLocationButton)
            backToMyLocation.setVisibility(View.GONE);
        else backToMyLocation.setVisibility(View.VISIBLE);

        backToMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapObject != null) {
                    Location myLocation = mapObject.getMyLocation();
                    if (myLocation != null)
                        mapObject.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(myLocation.getLatitude(), myLocation.getLongitude()),
                                calculator.getInitZoom()));
                }
            }
        });

        userDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseRouteFinal();
            }
        });

        SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentManager manager = getSupportFragmentManager();

        manager.beginTransaction()
                    .replace(R.id.map_main_container, mapFragment)
                    .commit();

        mapFragment.getMapAsync(this);
    }

    private void initContent() {
        Widget widget = (Widget) getIntent().getSerializableExtra(MapConstants.EXTRA_WIDGET);
        title = widget.getTitle();
        xml = widget.getPluginXmlData().length() == 0
                ? Utils.readXmlFromFile(widget.getPathToXmlFile())
                : widget.getPluginXmlData();

    }

    private void chooseRouteFinal() {
        RouteSelectDialog dialog = new RouteSelectDialog(this, items, new RouteSelectDialog.RouteDialogListener() {
            @Override
            public void itemClick(int position) {
                try {
                    MapItem item = items.get(position);
                    String url = "http://maps.google.com/maps?daddr=" + item.getLatitude() + "," + item.getLongitude();
                    Uri gmmIntentUri = Uri.parse(url);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        dialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapObject = googleMap;
        mapObject.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapObject.getUiSettings().setMapToolbarEnabled(false);
        mapObject.setMyLocationEnabled(true);

        List<Observable<MarkerWrapper>> markerTasks = new ArrayList<>();

        for (MapItem item : items)
            markerTasks.add(GeoUtils.createOptions(this, item));

        Observable.combineLatest(markerTasks, new FuncN<HashMap<Marker, MapItem>>() {
            @Override
            public HashMap<Marker, MapItem> call(Object... args) {
                HashMap<Marker, MapItem> map = new HashMap<>();

                for (Object object  : args){
                    if (!(object instanceof MarkerWrapper))
                        continue;

                    MarkerWrapper wrapper = (MarkerWrapper) object;
                    map.put(mapObject.addMarker(wrapper.getOptions()), wrapper.getItem());
                }
                return map;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleSubscriber<HashMap<Marker, MapItem>>(){
                    @Override
                    public void onNext(HashMap<Marker, MapItem> markerMapItemHashMap) {
                        mapObject.setInfoWindowAdapter(new InfoMapAdapter(markerMapItemHashMap, MapPlugin.this));

                        calculator = new GeoUtils.CenterCalculator();
                        calculator.init(markerMapItemHashMap.keySet());
                        mapObject.animateCamera(CameraUpdateFactory.newLatLngZoom(calculator.getCenter(),
                                calculator.getInitZoom()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    public void showInfoActivity(String description) {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra(MapConstants.URL, description);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
    }
}
