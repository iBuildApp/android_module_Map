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

import java.io.Serializable;

/**
 * Entity class that represents map location.
 */
public class MapItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private String title = "";
    private String subtitle = "";
    private String description = "";
    private String iconUrl = "";
    private double longitude = 0;
    private double latitude = 0;

    /**
     * Constructs new map location.
     */
    MapItem() {
    }

    /**
     * Sets the location title.
     * @param value the location title
     */
    public void setTitle(String value) {
        title = value;
    }

    /**
     * Returns the location title.
     * @return the location title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the location subtitle.
     * @param value 
     */
    public void setSubtitle(String value) {
        subtitle = value;
    }

    /**
     * Returns the location subtitle.
     * @return the location subtitle
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * Sets the location description.
     * @param value the location description
     */
    public void setDescription(String value) {
        description = value;
    }

    /**
     * Returns the location description.
     * @return the location description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the location longitude.
     * @param value the location longitude to set
     */
    public void setLongitude(double value) {
        longitude = value;
    }

    /**
     * Returns the location longitude.
     * @return the location longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the location latitude.
     * @param value the location latitude to set
     */
    public void setLatitude(double value) {
        latitude = value;
    }

    /**
     * Returns the location latitude
     * @return the location latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns the location pin icon URL.
     * @return the location pin icon URL
     */
    public String getIconUrl() {
        return iconUrl;
    }

    /**
     * Sets the location pin icon URL.
     * @param iconUrl the location pin icon URL
     */
    public void setIconUrl(String iconUrl) {
        if (iconUrl == null) {
            iconUrl = "";
        }

        this.iconUrl = iconUrl;
    }
}
