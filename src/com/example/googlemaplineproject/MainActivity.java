package com.example.googlemaplineproject;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;



public class MainActivity extends MapActivity {
    MapView myMapView = null;
    MapController myMC = null;
    GeoPoint geoPoint = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);

     setContentView(R.layout.main);
     myMapView = (MapView) findViewById(R.id.mapview);

     geoPoint = null;
     myMapView.setSatellite(false);
     double fromLat = 12.303534;
     double fromLong = 76.64611;
     double toLat = 12.9715987;
     double toLong = 77.5945627;

     String sourceLat = Double.toString(fromLat);
     String sourceLong = Double.toString(fromLong);
     String destinationLat = Double.toString(toLat);
     String destinationLong = Double.toString(toLong);

     String pairs[] = getDirectionData(sourceLat,sourceLong, destinationLat, destinationLong );
     String[] lngLat = pairs[0].split(",");

     // STARTING POINT
     GeoPoint startGP = new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6), (int) (Double.parseDouble(lngLat[0]) * 1E6));

     myMC = myMapView.getController();
     geoPoint = startGP;
     myMC.setCenter(geoPoint);
     myMC.setZoom(10);
     myMapView.getOverlays().add(new DirectionPathOverlay(startGP, startGP));

     // NAVIGATE THE PATH

     GeoPoint gp1;
     GeoPoint gp2 = startGP;

     for (int i = 1; i < pairs.length; i++) {
      lngLat = pairs[i].split(",");
      gp1 = gp2;
      // watch out! For GeoPoint, first:latitude, second:longitude

      gp2 = new GeoPoint((int) (Double.parseDouble(lngLat[1]) * 1E6),(int) (Double.parseDouble(lngLat[0]) * 1E6));
      myMapView.getOverlays().add(new DirectionPathOverlay(gp1, gp2));
      Log.d("xxx", "pair:" + pairs[i]);
     }

     // END POINT
     myMapView.getOverlays().add(new DirectionPathOverlay(gp2, gp2));

     myMapView.getController().animateTo(startGP);
     myMapView.setBuiltInZoomControls(true);
     myMapView.displayZoomControls(true);

    }

    @Override
    protected boolean isRouteDisplayed() {
     // TODO Auto-generated method stub
     return false;
    }

    private String[] getDirectionData(String sourceLat, String sourceLong, String destinationLat, String destinationLong) {


     String urlString = "http://maps.google.com/maps?f=d&hl=en&" +"saddr="+sourceLat+","+sourceLong+"&daddr="+destinationLat+","+destinationLong + "&ie=UTF8&0&om=0&output=kml";
     Log.d("URL", urlString);
     Document doc = null;
     HttpURLConnection urlConnection = null;
     URL url = null;
     String pathConent = "";

     try {

      url = new URL(urlString.toString());
      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.setDoOutput(true);
      urlConnection.setDoInput(true);
      urlConnection.connect();
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      doc = db.parse(urlConnection.getInputStream());

     } catch (Exception e) {
     }

     NodeList nl = doc.getElementsByTagName("LineString");
     for (int s = 0; s < nl.getLength(); s++) {
      Node rootNode = nl.item(s);
      NodeList configItems = rootNode.getChildNodes();
      for (int x = 0; x < configItems.getLength(); x++) {
       Node lineStringNode = configItems.item(x);
       NodeList path = lineStringNode.getChildNodes();
       pathConent = path.item(0).getNodeValue();
      }
     }
     String[] tempContent = pathConent.split(" ");
     return tempContent;
    }

   }


   //*****************************************************************************



    class DirectionPathOverlay extends Overlay {

       private GeoPoint gp1;
       private GeoPoint gp2;

       public DirectionPathOverlay(GeoPoint gp1, GeoPoint gp2) {
           this.gp1 = gp1;
           this.gp2 = gp2;
       }

       @Override
       public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
               long when) {
           // TODO Auto-generated method stub
           Projection projection = mapView.getProjection();
           if (shadow == false) {

               Paint paint = new Paint();
               paint.setAntiAlias(true);
               Point point = new Point();
               projection.toPixels(gp1, point);
               paint.setColor(Color.BLUE);
               Point point2 = new Point();
               projection.toPixels(gp2, point2);
               paint.setStrokeWidth(2);
               canvas.drawLine((float) point.x, (float) point.y, (float) point2.x,(float) point2.y, paint);
           }
           return super.draw(canvas, mapView, shadow, when);
       }

       @Override
       public void draw(Canvas canvas, MapView mapView, boolean shadow) {
           // TODO Auto-generated method stub

           super.draw(canvas, mapView, shadow);
       }

   }
