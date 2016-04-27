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
package com.ibuildapp.romanblack.MapPlugin;

import android.util.Log;

import com.ibuildapp.romanblack.MapPlugin.model.MapItem;

import java.util.ArrayList;

/**
 * This class using to prepare Google Map HTML to load in WebView.
 */
public class MapWebPageCreator {

    /**
     * Creates map HTML page with given parameters.
     * @param sourcePage map HTML page template
     * @param items map location items
     * @param zoom map zoom
     * @param srcLatitude 
     * @param srcLongitude
     * @return prepared map HTML page
     */
    static String createMapPage(String sourcePage, ArrayList<MapItem> items, int zoom,
                                float srcLatitude, float srcLongitude) {
        String mapPage = "";

        // create map points and past it to source html page
        mapPage = sourcePage.replace("__RePlAcE-Points__", createPoints(items));

        // proccess zoom amount
        mapPage = replaceCenterCoordinates(mapPage, items, zoom, srcLatitude, srcLongitude);

        return mapPage;
    }

    /**
     * Creates map points HTML fragment
     * @param items map location items
     * @return map points HTML fragment
     */
    private static String createPoints(ArrayList<MapItem> items) {
        String res = "";

        for (int i = 0; i < (items.size()); i++) {
            try {
                StringBuilder sb = new StringBuilder();

                sb.append("myMap.points.push({\n");

                // point
                sb.append("point: \'");
                sb.append(items.get(i).getTitle().
                        replaceAll("\n", " ").replaceAll("\'", "\\\\'") + "\',\n");

                // latitude
                sb.append("latitude:");
                sb.append(items.get(i).getLatitude());
                sb.append(",\n");

                //longitude
                sb.append("longitude:");
                sb.append(items.get(i).getLongitude());
                sb.append(",\n");

                //details
                sb.append("details: \'");
                sb.append(items.get(i).getSubtitle().
                        replaceAll("\n", " ").replaceAll("\'", "\\\\'"));
                sb.append("\',\n");

                //url
                sb.append("url: \'");
                sb.append(items.get(i).getDescription().
                        replaceAll("\n", " ").replaceAll("\'", "\\\\'"));
                sb.append("\',\n");

                //icon url
                if (items.get(i).getIconUrl().length() > 0) {
                    sb.append("icon: \'");
                    sb.append(items.get(i).getIconUrl().trim());
                    sb.append("\',\n");
                }

                //end
                sb.append("})\n\n");

                String temp = sb.toString();

                res = res + temp;

            } catch (Exception e) {
                Log.e("", "");
            }
        }

        return res;
    }

    /**
     * Replaces map center coordinates.
     * @param sourcePage map HTML page template
     * @param items map location items
     * @param zoom map zoom level
     * @param srcLatitude
     * @param srcLongitude
     * @return 
     */
    private static String replaceCenterCoordinates(String sourcePage, ArrayList<MapItem> items,
            int zoom, float srcLatitude, float srcLongitude) {
        String res = "";

        float maxLat = -90;
        float maxLng = -180;
        float minLat = 90;
        float minLng = 180;

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getLatitude() > maxLat) {
                maxLat = (float) items.get(i).getLatitude();
            }

            if (items.get(i).getLongitude() > maxLng) {
                maxLng = (float) items.get(i).getLongitude();
            }

            if (items.get(i).getLatitude() < minLat) {
                minLat = (float) items.get(i).getLatitude();
            }

            if (items.get(i).getLongitude() < minLng) {
                minLng = (float) items.get(i).getLongitude();
            }
        }

        float cenLat = (maxLat + minLat) / 2;
        float cenLng = (maxLng + minLng) / 2;

        res = sourcePage.replace("__RePlAcE-Lat__", new Float(cenLat).toString());
        res = res.replace("__RePlAcE-Lng__", new Float(cenLng).toString());

        if (zoom != -1) {
            res = res.replace("__RePlAcE-Zoom__",
                    Integer.toString(zoom));
        } else {
            res = res.replace("__RePlAcE-Zoom__",
                    new Integer(processZoom(minLng, maxLng)).toString());
        }

        return res;
    }

    /**
     * Processes map zoom level depending on map locations coordinates.
     * @param minLongitude minimal map location longitude
     * @param maxLongitude maximal map location longitude
     * @return 
     */
    private static int processZoom(float minLongitude, float maxLongitude) {
        int z = 1;
        float gr = Math.abs(maxLongitude - minLongitude);

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
        } else {
            z = 10;
        }

        return z;
    }
}
