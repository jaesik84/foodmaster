package com.foodmaster.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.foodmaster.R;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import map.MapApiConst;

public class MainActivity extends FragmentActivity implements MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener {

    private static final String LOG_TAG = "MainActivity";
    private final int MENU_LOCATION = Menu.FIRST;
    private final int MENU_REVERSE_GEO = Menu.FIRST + 1;

    private MapView mMapView;
    private MapReverseGeoCoder mReverseGeoCoder = null;
    private boolean isUsingCustomLocationMarker = false;

    private static final int REQUEST_CODE_LOCATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        startActivity(new Intent(this, SplashActivity.class));

        // 안드로이드 6버전 이상시 퍼미션 적용
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Display UI and wait for user interaction
            } else {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_LOCATION);
            }
        }

        mMapView = new MapView(this);
        mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        // 현위치를 표시하는 아이콘(마커)를 화면에 표시할지 여부를 설정
        mMapView.setShowCurrentLocationMarker(true);
        // 다운로드한 지도 이미지 데이터를 단말의 영구(persistent) 캐쉬 영역에 저장
        mMapView.setMapTilePersistentCacheEnabled(true);

        RelativeLayout container = (RelativeLayout) findViewById(R.id.map_view);
        container.addView(mMapView);

        btnEvent();

    }

    private void btnEvent() {
        new ExampleAsyncTask().execute("1", "2", "3", "4", "5");
    }

    class ExampleAsyncTask extends AsyncTask<String, Integer, Long> {

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Long result) {
            Toast.makeText(MainActivity.this, "Thread END", Toast.LENGTH_SHORT).show();
            // 줌 레벨 변경
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
            mMapView.setZoomLevel(-5, true);
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(MainActivity.this, "Thread START", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected Long doInBackground(String... params) {
            long result = 0;
            int numberOfParams = params.length;
            for (int i = 0; i < numberOfParams; i++) {
                SystemClock.sleep(1000);

            }
            return result;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // success!
            } else {
                // Permission was denied or request was cancelled
            }
        }
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_LOCATION, Menu.NONE, "Location");
        menu.add(0, MENU_REVERSE_GEO, Menu.NONE, "Reverse Geo-Coding");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final int itemId = item.getItemId();

        switch (itemId) {
            case MENU_LOCATION: {
                String[] mapMoveMenuItems = {
                        "User Location On",
                        "User Location On, MapMoving Off",
                        "User Location+Heading On",
                        "User Location+Heading On, MapMoving Off",
                        "Off",
                        (isUsingCustomLocationMarker ? "Default" : "Custom") + " Location Marker",
                        "Show Location Marker",
                        "Hide Location Marker"
                };
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Location");
                dialog.setItems(mapMoveMenuItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // User Location On
                            {
                                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                            }
                            break;
                            case 1: // User Location On, MapMoving Off
                            {
                                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
                            }
                            break;
                            case 2: // User Location+Heading On
                            {
                                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                            }
                            break;
                            case 3: // User Location+Heading On, MapMoving Off
                            {
                                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeadingWithoutMapMoving);
                            }
                            break;
                            case 4: // Off
                            {
                                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                            }
                            break;
                            case 5: // Default/Custom Location Marker
                            {
                                if (isUsingCustomLocationMarker) {
                                    mMapView.setCurrentLocationRadius(0);
                                    mMapView.setDefaultCurrentLocationMarker();
                                } else {
                                    mMapView.setCurrentLocationRadius(100); // meter
                                    mMapView.setCurrentLocationRadiusFillColor(Color.argb(77, 255, 255, 0));
                                    mMapView.setCurrentLocationRadiusStrokeColor(Color.argb(77, 255, 165, 0));

                                    MapPOIItem.ImageOffset trackingImageAnchorPointOffset = new MapPOIItem.ImageOffset(16, 16); // 좌하단(0,0) 기준 앵커포인트 오프셋
                                    MapPOIItem.ImageOffset directionImageAnchorPointOffset = new MapPOIItem.ImageOffset(65, 65);
                                    MapPOIItem.ImageOffset offImageAnchorPointOffset = new MapPOIItem.ImageOffset(15, 15);
                                    mMapView.setCustomCurrentLocationMarkerTrackingImage(R.drawable.custom_map_present_tracking, trackingImageAnchorPointOffset);
                                    mMapView.setCustomCurrentLocationMarkerDirectionImage(R.drawable.custom_map_present_direction, directionImageAnchorPointOffset);
                                    mMapView.setCustomCurrentLocationMarkerImage(R.drawable.custom_map_present, offImageAnchorPointOffset);
                                }
                                isUsingCustomLocationMarker = !isUsingCustomLocationMarker;
                            }
                            break;
                            case 6: // Show Location Marker
                            {
                                mMapView.setShowCurrentLocationMarker(true);
                            }
                            break;
                            case 7: // Hide Location Marker
                            {
                                if (mMapView.isShowingCurrentLocationMarker()) {
                                    mMapView.setShowCurrentLocationMarker(false);
                                }
                            }
                            break;
                        }
                    }

                });
                dialog.show();
                return true;
            }

            case MENU_REVERSE_GEO: {
                mReverseGeoCoder = new MapReverseGeoCoder(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY, mMapView.getMapCenterPoint(), MainActivity.this, MainActivity.this);
                mReverseGeoCoder.startFindingAddress();
                return true;
            }
        }

        return onOptionsItemSelected(item);

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {
        Toast.makeText(MainActivity.this, "onCurrentLocationDeviceHeadingUpdate", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {
        Toast.makeText(MainActivity.this, "onCurrentLocationUpdateFailed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
        Toast.makeText(MainActivity.this, "onCurrentLocationUpdateCancelled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        Toast.makeText(MainActivity.this, "onReverseGeoCoderFoundAddress", Toast.LENGTH_SHORT).show();
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        Toast.makeText(MainActivity.this, "onReverseGeoCoderFailedToFindAddress", Toast.LENGTH_SHORT).show();
        onFinishReverseGeoCoding("Fail");
    }

    private void onFinishReverseGeoCoding(String result) {
        Toast.makeText(MainActivity.this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
    }
}
