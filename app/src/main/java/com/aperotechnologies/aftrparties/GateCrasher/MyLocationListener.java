package com.aperotechnologies.aftrparties.GateCrasher;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by mpatil on 17/06/16.
 */
public class MyLocationListener implements LocationListener
{
    @Override
    public void onLocationChanged(Location loc)
    {
//        editLocation.setText("");
//        pb.setVisibility(View.INVISIBLE);
//        Toast.makeText(
//                getBaseContext(),
//                "Location changed: Lat: " + loc.getLatitude() + " Lng: "
//                        + loc.getLongitude(), Toast.LENGTH_SHORT).show();
        String longitude = "Longitude: " + loc.getLongitude();
        Log.e("11", longitude);
        String latitude = "Latitude: " + loc.getLatitude();
        Log.e("22", latitude);

        /*------- To get city name from coordinates -------- */
//        String cityName = null;
//        Geocoder gcd = new Geocoder(m_, Locale.getDefault());
//        List<Address> addresses;
//        try {
//            addresses = gcd.getFromLocation(loc.getLatitude(),
//                    loc.getLongitude(), 1);
//            if (addresses.size() > 0) {
//                System.out.println(addresses.get(0).getLocality());
//                cityName = addresses.get(0).getLocality();
//            }
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//        String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
//                + cityName;

        Log.e("33", latitude + "    " + longitude);

        // editLocation.setText(s);
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
