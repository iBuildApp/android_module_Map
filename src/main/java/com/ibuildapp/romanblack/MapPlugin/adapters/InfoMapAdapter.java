package com.ibuildapp.romanblack.MapPlugin.adapters;


import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.ibuildapp.romanblack.MapPlugin.R;
import com.ibuildapp.romanblack.MapPlugin.model.MapItem;

import java.util.HashMap;

public class InfoMapAdapter implements GoogleMap.InfoWindowAdapter {
    private Activity context;
    private HashMap<Marker, MapItem> items;

    public InfoMapAdapter(HashMap<Marker, MapItem> items, Activity context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = context.getLayoutInflater().inflate(R.layout.map_dialog_item, null);
        TextView title = (TextView) v.findViewById(R.id.map_dialog_title);
        TextView description = (TextView) v.findViewById(R.id.map_dialog_description);
        MapItem item = items.get(marker);
        title.setText(item.getTitle());
        description.setText(item.getSubtitle());
        return v;
    }
}
