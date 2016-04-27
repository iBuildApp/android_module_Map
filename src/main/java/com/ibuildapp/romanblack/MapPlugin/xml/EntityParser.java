/****************************************************************************
*                                                                           *
*  Copyright (C) 2014-2015 iBuildApp, Inc. ( http://ibuildapp.com )         *
*                                                                           *
*  This file is part of iBuildApp.                                          *
*                                                                           *
*  This Source Code Form is subject to the terms of the iBuildApp License.  *
*  You can obtain one at http://ibuildapp.com/license/                      *
*                                                                           *
****************************************************************************/
package com.ibuildapp.romanblack.MapPlugin.xml;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.xml.sax.Attributes;

import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Log;
import android.util.Xml;

import com.ibuildapp.romanblack.MapPlugin.model.MapItem;

/**
 * This class using for module xml data parsing.
 */
public class EntityParser {

    private ArrayList<MapItem> items = new ArrayList<MapItem>();
    private String xml = "";
    private String title = "";
    public boolean showCurrentLocation = true;
    private MapItem item = null;
    public int zoom = -1;

    /**
     * Constructs new EntityParser instance.
     * @param xml - module xml data to parse
     */
    public EntityParser(String xml) {
        this.xml = xml;
    }

    /**
     * Returns map title.
     * @return 
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns map location item.
     * @return 
     */
    public ArrayList<MapItem> getItems() {
        return items;
    }

    /**
     * Parses module data that was set in constructor.
     */
    public void parse() {
        RootElement root = new RootElement("data");

        android.sax.Element title = root.getChild("title");
        android.sax.Element object = root.getChild("object");
        android.sax.Element showlocation = root.getChild("showCurrentUserLocation");

        showlocation.setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                if (body.contains("0")) {
                    EntityParser.this.showCurrentLocation = false;
                }
            }
        });

        root.getChild("initialZoom").setEndTextElementListener(new EndTextElementListener() {
            public void end(String arg0) {
                zoom = Integer.parseInt(arg0);
            }
        });

        root.setEndElementListener(new EndElementListener() {
            @Override
            public void end() {
            }
        });

        title.setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                EntityParser.this.title = body;
            }
        });

        object.setStartElementListener(new StartElementListener() {
            @Override
            public void start(Attributes attributes) {
                item = new MapItem();
            }
        });
        object.setEndElementListener(new EndElementListener() {
            @Override
            public void end() {
                items.add(item);
                item = null;
            }
        });
        object.getChild("title").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                if (item != null) {
                    item.setTitle(body);
                }
            }
        });
        object.getChild("subtitle").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                if (item != null) {
                    item.setSubtitle(body);
                }
            }
        });
        object.getChild("description").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                if (item != null) {
                    item.setDescription(body);
                }
            }
        });
        object.getChild("longitude").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                if (item != null) {
                    try {
                        item.setLongitude(Double.parseDouble(body));
                    } catch (Exception e) {
                    }
                }
            }
        });
        object.getChild("latitude").setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                if (item != null) {
                    try {
                        item.setLatitude(Double.parseDouble(body));
                    } catch (Exception e) {
                    }

                }
            }
        });

        object.getChild("pinurl").setEndTextElementListener(new EndTextElementListener() {
            public void end(String arg0) {
                item.setIconUrl(arg0.trim());
            }
        });

        try {
            Xml.parse(new ByteArrayInputStream(xml.getBytes()), Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (Exception e) {
            Log.d("", "");
        }
    }
}