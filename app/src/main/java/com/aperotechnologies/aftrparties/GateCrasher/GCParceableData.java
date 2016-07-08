package com.aperotechnologies.aftrparties.GateCrasher;

import java.io.Serializable;

/**
 * Created by hasai on 04/07/16.
 */
public class GCParceableData implements Serializable
{
    private static final long serialVersionUID = -7060210544600464481L;
    private String latitude;
    private String longitude;
    private String distance;
    private String atdatetime;
    private String byob;
    private String preference;



    public String getlatitude() { return latitude; }
    public void setlatitude(String latitude) { this.latitude = latitude; }

    public String getlongitude() { return longitude; }
    public void setlongitude(String longitude) { this.longitude = longitude; }

    public String getdistance() { return distance; }
    public void setdistance(String distance) { this.distance = distance; }

    public String getatdatetime() { return atdatetime; }
    public void setatdatetime(String atdatetime) { this.atdatetime = atdatetime; }

    public String getbyob() { return byob; }
    public void setbyob(String byob) { this.byob = byob; }

    public String getgenderpreference() { return preference;}
    public void setgenderpreference(String preference){ this.preference = preference; }
}