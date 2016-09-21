package com.ibuildapp.romanblack.MapPlugin.model;


import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerWrapper {
    private MarkerOptions options;
    private MapItem item;

    public MarkerWrapper(MarkerOptions options, MapItem item) {
        this.options = options;
        this.item = item;
    }

    public MarkerOptions getOptions() {
        return options;
    }

    public void setOptions(MarkerOptions options) {
        this.options = options;
    }

    public MapItem getItem() {
        return item;
    }

    public void setItem(MapItem item) {
        this.item = item;
    }
}
