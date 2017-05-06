package com.wisdomxunjian.com.povertyalleviation.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.Calendar;
import java.util.List;

public class GPSService extends Service {
    public LocationClient mLocationClient = null;
    public GPSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null ;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        initLocation();
        Log.i("TAG","服务开启");

        //位置提醒相关代码
        NotifyLister mNotifyer = new NotifyLister();
        mNotifyer.SetNotifyLocation(32.677655,112.124139,10000,"gps");
//4个参数代表要位置提醒的点的坐标，具体含义依次为：纬度，经度，距离范围，坐标系类型(gcj02,gps,bd09,bd09ll)

        mLocationClient.registerNotify(mNotifyer);
//注册位置提醒监听事件后，可以通过SetNotifyLocation 来修改位置提醒设置，修改后立刻生效。
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                //获取定位结果
                StringBuffer sb = new StringBuffer(256);

                sb.append("time : ");
                sb.append(location.getTime());    //获取定位时间

                sb.append("\nerror code : ");
                sb.append(location.getLocType());    //获取类型类型

                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());    //获取纬度信息

                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());    //获取经度信息

                sb.append("\nradius : ");
                sb.append(location.getRadius());    //获取定位精准度

                if (location.getLocType() == BDLocation.TypeGpsLocation){

                    // GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());    // 单位：公里每小时

                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());    //获取卫星数

                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());    //获取海拔高度信息，单位米

                    sb.append("\ndirection : ");
                    sb.append(location.getDirection());    //获取方向信息，单位度

                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());    //获取地址信息

                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");

                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
                    Log.i("TAG","kaiqi");
                    // 网络定位结果
                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());    //获取地址信息

                    sb.append("\noperationers : ");
                    sb.append(location.getOperators());    //获取运营商信息

                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");

                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

                    // 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");

                } else if (location.getLocType() == BDLocation.TypeServerError) {

                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");

                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");

                }

                sb.append("\nlocationdescribe : ");
                sb.append(location.getLocationDescribe());    //位置语义化信息

                List<Poi> list = location.getPoiList();    // POI数据
                if (list != null) {
                    sb.append("\npoilist size = : ");
                    sb.append(list.size());
                    for (Poi p : list) {
                        sb.append("\npoi= : ");
                        sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                    }
                }

                Log.i("BaiduLocationApiDem", sb.toString());

                Calendar cal = Calendar.getInstance();// 当前日期
                int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
                int minute = cal.get(Calendar.MINUTE);// 获取分钟
                int minuteOfDay = hour * 60 + minute;// 从0:00分开是到目前为止的分钟数
                final int start = 8 * 60 + 30;// 起始时间 17:20的分钟数
                final int end = 18 * 60;// 结束时间 19:00的分钟数
                if (minuteOfDay >= start && minuteOfDay <= end) {

                    System.out.println("在外围内");
//                    double distance = DistanceUtil.getDistance(new LatLng(location.getLongitude(), location.getLatitude()), new LatLng(112.124139, 32.677655));
                    double distance =  getDistance(location,4.9E-324, 4.9E-324);
                    if(distance<1000){
                        System.out.println("自动打卡成功");
                    }
                } else {
                    System.out.println("在外围外");
                }
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {
            }

        });
        mLocationClient.start();
    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("gps");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span=60000;
        option.setScanSpan(0);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        mLocationClient.setLocOption(option);
    }
    public class NotifyLister extends BDNotifyListener{

        public void onNotify(BDLocation mlocation, float distance){
            Log.i("TAG","到达位置");
        }
    }
    public double getDistance(BDLocation start,double la2,double lo2) {
        double lat1 = (Math.PI / 180) * start.getLatitude();
        double lat2 = (Math.PI / 180) *la2;

        double lon1 = (Math.PI / 180) * start.getLongitude();
        double lon2 = (Math.PI / 180) *lo2;

        // 地球半径
        double R = 6371;

        // 两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1)
                * Math.cos(lat2) * Math.cos(lon2 - lon1))
                * R;

        return d * 1000;
    }
}
