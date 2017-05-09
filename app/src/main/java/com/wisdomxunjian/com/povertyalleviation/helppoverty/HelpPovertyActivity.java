package com.wisdomxunjian.com.povertyalleviation.helppoverty;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.wisdomxunjian.com.povertyalleviation.R;
import com.wisdomxunjian.com.povertyalleviation.adapter.MyAdapter;
import com.wisdomxunjian.com.povertyalleviation.bean.EventContent;
import com.wisdomxunjian.com.povertyalleviation.qiandao.QianDaoActivity;
import com.wisdomxunjian.com.povertyalleviation.utils.DataString;
import com.wisdomxunjian.com.povertyalleviation.utils.FileUtils;
import com.wisdomxunjian.com.povertyalleviation.utils.ImageUtils;
import com.wisdomxunjian.com.povertyalleviation.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/5/6.
 */

public class HelpPovertyActivity extends Activity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{

    private Button btSeek;
    private Button btToday;
    private Button btBygone;
    private ImageView btPhont;
    private Button btSend;
    private EditText etName;
    private EditText etNameHelp;
    private EditText etTime;
    private EditText etSeek;
    private EditText etWay;
    private EditText etLocation;
    private LinearLayout llToday;
    private LinearLayout llAccomp;
    private RecyclerView rvList;
    private SwipeRefreshLayout mSwipe;
    private ImageView mPhoto1;
    private ImageView mPhoto2;
    private ImageView mPhoto3;
    private Uri origUri;
    private int p=0;
    private String theLarge;
    private static final String TAG = "HelpPovertyActivity";
    private final static String FILE_SAVEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Ntgis/Portrait/";
    private Uri cropUri;
    private File protraitFile;
    private File file;
    private BitmapFactory.Options options;
    private LocationClient mLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_poverty);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        btToday = (Button) findViewById(R.id.bt_today);//头部按钮
        btBygone = (Button) findViewById(R.id.bt_bygone);//头部按钮
        btPhont = (ImageView) findViewById(R.id.bt_photograph);//选择照片
        btSend = (Button) findViewById(R.id.bt_send);//发送请求
        btSeek = (Button) findViewById(R.id.bt_seek);//收索
        etName = (EditText) findViewById(R.id.et_name);//贫困人
        etNameHelp = (EditText) findViewById(R.id.et_name_help);//帮贫人
        etTime = (EditText) findViewById(R.id.et_time);//时间
        etSeek = (EditText) findViewById(R.id.et_seek);//收索
        etWay = (EditText) findViewById(R.id.et_way);//帮扶的内容
        etLocation = (EditText) findViewById(R.id.et_location);//地址
        llAccomp = (LinearLayout) findViewById(R.id.ll_accomplish);
        llToday = (LinearLayout) findViewById(R.id.ll_today_help);
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout);
        rvList = (RecyclerView) findViewById(R.id.rv_list);
        mPhoto1 = (ImageView) findViewById(R.id.iv_photo1);
        mPhoto2 = (ImageView) findViewById(R.id.iv_photo2);
        mPhoto3 = (ImageView) findViewById(R.id.iv_photo3);
        etTime.setText(DataString.getTime());
        etLocation.setTextColor(Color.BLACK);
        initLocation();

    }

    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());

        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
//                tv_qiandaoshijian.setText(bdLocation.getTime());
//                getTv_qiandaodidian.setText(bdLocation.getLocationDescribe());
                Log.i("TAG",location.getTime()+location.getLocationDescribe()+mLocationClient.isStarted()+location.getLocType());
                if (location == null)
                    return;

                etLocation.setText(location.getAddrStr());

                etLocation.setSelection(etLocation.getText().toString().length());
                Log.i(TAG, "onReceiveLocation: "+etLocation.getText().toString());
                Log.i(TAG, "onReceiveLocation: +"+333);
                int errorcode = location.getLocType();
                if (errorcode == 61 || errorcode == 65 || errorcode == 161) {
                    double mLatitude = location.getLatitude() * 1E6;
                    double mLongitude = location.getLongitude() * 1E6;
                    String name = location.getAddrStr();
                    GeoPoint locPoint = new GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));


                    mLocationClient.stop();

                } else {
                    Toast.makeText(HelpPovertyActivity.this, "定位失败", Toast.LENGTH_SHORT).show();
                }
            }



            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        });
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span=60000;
        option.setScanSpan(0);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要


        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps
        option.setCoorType("bd09ll"); // 设置坐标类型为bd09ll
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
        mLocationClient.start();
    }

    private void initData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        //设置布局管理器
        rvList.setLayoutManager(layoutManager);
         //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper. VERTICAL);
        List<EventContent> evlist = new ArrayList<EventContent>();
       //设置Adapter
        MyAdapter mAdapter = new MyAdapter(getApplicationContext(),evlist);
        rvList.setAdapter(mAdapter);
    }

    private void initListener() {
        btToday.setOnClickListener(this);
        btBygone.setOnClickListener(this);
        btPhont.setOnClickListener(this);
        btSeek.setOnClickListener(this);
        btSend.setOnClickListener(this);
        mSwipe.setOnRefreshListener(this);
        mSwipe.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_today:
                if (llAccomp.getVisibility() == View.GONE){
                    llAccomp.setVisibility(View.GONE);
                    llToday.setVisibility(View.VISIBLE);
                }
                llToday.setVisibility(View.VISIBLE);
                break;
            case R.id.bt_bygone:
                if (llAccomp.getVisibility() == View.GONE){
                    llAccomp.setVisibility(View.VISIBLE);
                    llToday.setVisibility(View.GONE);
                }
                llAccomp.setVisibility(View.VISIBLE);
                break;
            case R.id.bt_photograph:

                paizhao();
                break;
            case R.id.bt_send:
                break;
            case R.id.bt_seek:
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        etLocation.setSaveEnabled(false);
    }

    private void paizhao() {
        if(p==3){
            Toast.makeText(this,"最多上传三张图片",Toast.LENGTH_LONG).show();
        }
        CharSequence[] items = {
                getString(R.string.img_from_album),
                getString(R.string.img_from_camera)
        };
        imageChooseItem(items);
    }

    private void imageChooseItem(final CharSequence[] items) {
        AlertDialog imageDialog = new AlertDialog.Builder(this).setTitle("添加图片")
                .setIcon(android.R.drawable.btn_star)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        if (item == 0){
                            Intent intent;
                            if (Build.VERSION.SDK_INT < 19) {
                                intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "选择图片"),
                                        ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
                            } else {
                                intent = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "选择图片"),
                                        ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
                            }
                        }else if (item == 1){
                            Intent intent;
                            // 判断是否挂载了SD卡
                            String savePath = "";
                            String storageState = Environment.getExternalStorageState();
                            if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                                savePath = Environment.getExternalStorageDirectory()
                                        .getAbsolutePath() + "/oschina/Camera/";
                                File savedir = new File(savePath);
                                if (!savedir.exists()) {
                                    savedir.mkdirs();
                                }
                            }

                            // 没有挂载SD卡，无法保存文件
                            if (StringUtils.isEmpty(savePath)) {
                                Toast.makeText(HelpPovertyActivity.this, "无法保存照片，请检查SD卡是否挂载",
                                        Toast.LENGTH_SHORT).show();
//				            AppContext.showToastShort("无法保存照片，请检查SD卡是否挂载");
                                return;
                            }

                            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                                    .format(new Date());
                            String fileName = "osc_" + timeStamp + ".jpg";// 照片命名
                            File out = new File(savePath, fileName);
                            Uri uri = Uri.fromFile(out);
                            origUri = uri;

                            // 该照片的绝对路径
                            theLarge = savePath + fileName;

                            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            startActivityForResult(intent,
                                    ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
                        }
                    }
                }).create();
        imageDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: "+22222+requestCode);

        if (resultCode != RESULT_OK)
            return;
        switch (requestCode){
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD://相册

                try{
//                    pv.pick(data);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap bm = null;
                    try {
                        bm = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(data.getData()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (mPhoto1.getVisibility() == View.INVISIBLE){

                        mPhoto1.setImageBitmap(bm);
                        Log.i(TAG, "onActivityResult: "+22222+data.getData().getPath());
                        //}
                        mPhoto1.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "图片添加完成", Toast.LENGTH_SHORT).show();
                    }else if (mPhoto1.getVisibility() == View.VISIBLE && mPhoto2.getVisibility()==View.INVISIBLE){

                        mPhoto2.setImageBitmap(bm);
                        //}
                        mPhoto2.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "图片添加完成", Toast.LENGTH_SHORT).show();
                    }else if (mPhoto1.getVisibility() == View.VISIBLE && mPhoto2.getVisibility()==View.VISIBLE
                            &&mPhoto3.getVisibility() == View.INVISIBLE){

                        mPhoto3.setImageBitmap(bm);
                        //}
                        mPhoto3.setVisibility(View.VISIBLE);
                        p=3;
                        Toast.makeText(this, "图片添加完成", Toast.LENGTH_SHORT).show();

                    }

                }catch(RuntimeException e){
                    Toast.makeText(this, "图片已经选择，请勿重复添加。",  Toast.LENGTH_LONG).show();
//                    imgTex.setTextColor(Color.BLUE);
                }
                break;
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA://相机
                Log.i(TAG, "onActivityResult: +"+ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
                if(data == null){
                    File file = new File(FileUtils.compressImage(theLarge, 720, 1080, 1024));
                    options = new BitmapFactory.Options();
                    Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                    mPhoto1.setImageBitmap(bm);
                    mPhoto1.setVisibility(View.VISIBLE);
                }else {
                    getUploadTempFile(data.getData());
                    Bitmap bm = BitmapFactory.decodeFile(cropUri.getPath(), options);
                    mPhoto1.setImageBitmap(bm);
                    mPhoto1.setVisibility(View.VISIBLE);
                }

                break;
        }
    }

    private Uri getUploadTempFile(Uri uri) {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(FILE_SAVEPATH);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            Toast.makeText(this, "无法保存照片，请检查SD卡是否挂载",
                    Toast.LENGTH_SHORT).show();
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);

        // 如果是标准Uri
        if (StringUtils.isEmpty(thePath)) {
            thePath = ImageUtils.getAbsoluteImagePath(this, uri);
            if(thePath.contains("file:///")){
                String str = thePath.substring(7);
                thePath = str;
            }
        }
        String ext = FileUtils.getFileFormat(thePath);
//        ext = StringUtils.isEmpty(ext) ? "jpg" : ext;
//        // 照片命名
//        String cropFileName = "osc_crop_" + timeStamp + "." + ext;
//        // 裁剪头像的绝对路径
//        protraitPath = FILE_SAVEPATH + cropFileName;

        protraitFile = new File(thePath);

        cropUri = Uri.fromFile(protraitFile);
        file = new File(FileUtils.compressImage(thePath, 720, 1080, 1024));
        return this.cropUri;
    }




    @Override
    public void onRefresh() {

    }
}
