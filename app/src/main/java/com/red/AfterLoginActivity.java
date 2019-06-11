package com.red;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

public class AfterLoginActivity extends AppCompatActivity {

    private MapView mMapView = null;
    private ImageButton btLocationCfg,btSet;
    private BaiduMap mBaiduMap = null;
    private LocationClient mLocationClient =null;
    private String TAG = "AfterLoginActivity";
    private RoutePlanSearch mSearch;
    private LatLng mpoint,point;//设置我的位置与唯一设备位置
    private Boolean firstMap = true; //是否第一次看
    private Boolean flagStartCarDrive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login);

        mMapView = findViewById(R.id.bmapView);
        btLocationCfg = findViewById(R.id.Image01);
        btSet = findViewById(R.id.Image02);

        mBaiduMap = mMapView.getMap();

        /* ---------------------地图上显示定位为信息------------------------------------------------*/
        mBaiduMap.setMyLocationEnabled(true);// 使能定位图层

        //定位初始化
        mLocationClient = new LocationClient(this);

        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
        option.setIsNeedAddress(true);//是否需要地址信息
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(2000);//每一秒发起一次定位请求间隔

        //设置locationClientOption
        mLocationClient.setLocOption(option);

        //注册LocationListener监听器
        mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                // map view 销毁后不在处理新接收的位置
                if (bdLocation == null || mMapView == null)
                    return;

                //获取经纬度
                double latitude = bdLocation.getLatitude();    //获取纬度信息
                double longitude = bdLocation.getLongitude();    //获取经度信息
                Log.i(TAG,"经度:" + longitude + "," + "维度" + latitude);
                mpoint = new LatLng(latitude,longitude);
                //Toast.makeText(AfterLoginActivity.this,"经度:" + longitude + " 纬度: "
                //        + latitude,Toast.LENGTH_SHORT).show();

                // 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(bdLocation.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100)
                        .latitude(latitude)
                        .longitude(longitude)
                        .build();

                //第一次打开Activity移动地图到定位的位置
                if(firstMap) {
                    firstMap = false;
                    LatLng xy = new LatLng(latitude,
                            longitude);
                    MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(xy);
                    mBaiduMap.animateMapStatus(status);
                }
                // 设置定位数据
                mBaiduMap.setMyLocationData(locData);
            }
        });

        //开启地图定位图层
        mLocationClient.start();
        /* ---------------------地图上显示定位为信息------------------------------------------------*/

        /* ---------------------地图上显示设备位置信息----------------------------------------------*/
        point = new LatLng(37.964482,112.553259); //定义设备位置信息
        // 定义设备位置图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.carstop);
        // 构建适用于百度地图的地图位置设置参数(MarkerOption)
        OverlayOptions markOptions = new MarkerOptions()
                .alpha(0.5f)
                .position(point)
                .icon(bitmap)
                .draggable(false)
                .flat(true);
        // 在百度地图上构建设施图标
        mBaiduMap.addOverlay(markOptions);
        // Marker点击触发事件
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Intent intent = new Intent(AfterLoginActivity.this,
                        ShowInfoActivity.class);
                startActivityForResult(intent,1);
                return true;
            }
        });
        /* ---------------------地图上显示设备位置信息----------------------------------------------*/

        /*----------------------设置定位模式-------------------------------------------------------*/
        //设置ImageBtton监听器
        btLocationCfg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //定位模式配置
                MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.FOLLOWING,/*设置为定位跟随模式*/
                        true,/*显示方向信息*/
                        null/*不设置自定义图标*/);
                //设置定位模式:自动跟随，有方向
                mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);
                //提示模式更改
                Toast.makeText(AfterLoginActivity.this,"打开自动跟随模式",
                        Toast.LENGTH_SHORT).show();

                //驾车路线规划
                //carDrvieDemo();
            }
        });
        /*----------------------设置定位模式-------------------------------------------------------*/

        /*if(flagStartCarDrive) {
            flagStartCarDrive = false;
            carDrvieDemo();
        }*/

        /*----------------------打开ip设置界面-----------------------------------------------------*/
        btSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AfterLoginActivity.this,
                        SettingActivity.class);
                startActivity(intent);
            }
        });
        /*----------------------------------------------------------------------------------------*/
    }

    protected void carDrvieDemo() {
        //创建驾车路线规划检索实例
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
                //创建DrivingRouteOverlay实例
                DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
                try{
                    if (drivingRouteResult.getRouteLines().size() > 0) {
                        //获取路径规划数据,(以返回的第一条路线为例）
                        //为DrivingRouteOverlay实例设置数据
                        overlay.setData(drivingRouteResult.getRouteLines().get(0));
                        //在地图上绘制DrivingRouteOverlay
                        overlay.addToMap();
                    }
                } catch (NullPointerException e) {
                    throw e;
                }
            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        });
        //初始位置坐标
        PlanNode stNode = PlanNode.withLocation(mpoint);
        PlanNode enNode = PlanNode.withLocation(point);
        //发起检索
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode)
                .to(enNode));
        //释放检索实例
        mSearch.destroy();
    }

    @Override
    protected void onResume() {
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK) {
                    flagStartCarDrive = data.getBooleanExtra("carDriveDemo",false);
                    if(flagStartCarDrive) {
                        flagStartCarDrive = false;
                        carDrvieDemo();
                    }
                }
                break;
            default:
                break;
        }
    }
}
