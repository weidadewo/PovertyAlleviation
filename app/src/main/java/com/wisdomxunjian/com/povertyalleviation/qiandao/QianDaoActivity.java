package com.wisdomxunjian.com.povertyalleviation.qiandao;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.platform.comapi.map.B;
import com.wisdomxunjian.com.povertyalleviation.BeseActivity;
import com.wisdomxunjian.com.povertyalleviation.R;
import com.wisdomxunjian.com.povertyalleviation.utils.ImageUtils;
import com.wisdomxunjian.com.povertyalleviation.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.format;
import static com.baidu.location.h.g.t;

public class QianDaoActivity extends BeseActivity implements View.OnClickListener{
    private String FileName = null;
  private ImageView iv,iv_paizhao,iv_qiandaotupian1,iv_qiandaotupian2,iv_qiandaotupian3;
    private Uri origUri;
    private String theLarge;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer = null;
    private Button ly,bf;
    public LocationClient mLocationClient = null;
    private TextView tv_qiandaoshijian,getTv_qiandaodidian;
    private int p=0;
    private Handler handler=new Handler(){
        @Override
        public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
            return super.sendMessageAtTime(msg, uptimeMillis);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qian_dao);
        setText(View.VISIBLE,View.VISIBLE,View.INVISIBLE,"签到");
        tv_qiandaoshijian= (TextView) findViewById(R.id.tv_qiandaoshijian);
        getTv_qiandaodidian= (TextView) findViewById(R.id.tv_qiandaodidian);

        iv= (ImageView) findViewById(R.id.iv_qiandao);
        iv.setOnClickListener(this);
        iv_qiandaotupian1= (ImageView) findViewById(R.id.iv_qiandaotu1);
        iv_qiandaotupian2= (ImageView) findViewById(R.id.iv_qiandaotu2);
        iv_qiandaotupian3= (ImageView) findViewById(R.id.iv_qiandaotu3);
        iv_paizhao= (ImageView) findViewById(R.id.iv_paizhao);
        iv_paizhao.setOnClickListener(this);
        ly= (Button) findViewById(R.id.bt_luyin);
        bf= (Button) findViewById(R.id.bt_bofang);
        ly.setOnClickListener(this);
        bf.setOnClickListener(this);
        initLocation();
        //设置sdcard的路径
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    0);//自定义的code
        }
        FileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        FileName += "/audiorecordtest.mp3";
    }
    private void qiandao(){
//        startActivity(new Intent(QianDaoActivity.this,DialogActivity.class));
    }

    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case R.id.bt_luyin:
                 if(isHasSdcard())
                 luyin();
                 break;
             case R.id.bt_bofang:
                 if(isHasSdcard())
                 bofang();
                 break;
             case R.id.iv_paizhao:
                 paizhao();
                 break;

         }
    }
    private boolean isHasSdcard(){
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            Toast.makeText(QianDaoActivity.this,"没有插入SD卡",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void paizhao(){
        if(p==3){
            Toast.makeText(this,"最多上传三张图片",Toast.LENGTH_LONG).show();
        }

        CharSequence[] items = {
                getString(R.string.img_from_album),
                getString(R.string.img_from_camera)
        };
        imageChooseItem(items);
    }
    private void bofang(){
        // TODO Auto-generated method stub
        mPlayer = new MediaPlayer();
        try{
            mPlayer.setDataSource(FileName);
            mPlayer.prepare();
            mPlayer.start();
        }catch(IOException e){
            Log.e("TAG","播放失败");
        }
    }
    private void luyin(){
        // TODO Auto-generated method stub
        if(mRecorder!=null){
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            Toast.makeText(QianDaoActivity.this,"结束录音",Toast.LENGTH_LONG).show();
            ly.setText("开始录音");
            return;
        }
        Toast.makeText(QianDaoActivity.this,"开始录音",Toast.LENGTH_LONG).show();
         mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(FileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("TAG", "prepare() failed");
        }
        ly.setText("停止录音");
        mRecorder.start();
    }
    private void initLocation(){
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
//                tv_qiandaoshijian.setText(bdLocation.getTime());
//                getTv_qiandaodidian.setText(bdLocation.getLocationDescribe());
                Log.i("TAG",bdLocation.getTime()+bdLocation.getLocationDescribe());
            }

            @Override
            public void onConnectHotSpotMessage(String s, int i) {

            }
        });
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
        mLocationClient.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode){
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
                //Log.e("图片路径",data.toString());
//			if(data.toString().contains("file:///")){
//				jqstr = data.toString().substring(7);
//				pv.pick(data);
//			}else{
                try{
//                    pv.pick(data);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap bm = BitmapFactory.decodeFile(theLarge, options);
                    iv_qiandaotupian2.setImageBitmap(bm);
                    //}
                    iv_qiandaotupian2.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "图片添加完成", Toast.LENGTH_LONG).show();
                }catch(RuntimeException e){
                    Toast.makeText(this, "图片已经选择，请勿重复添加。",  Toast.LENGTH_LONG).show();
//                    imgTex.setTextColor(Color.BLUE);
                }

                break;

            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
//			if(data.toString().contains("file:///")){
//				jqstr = data.toString().substring(7);
//				pv.pick(data);
//			}else{
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bm = null;
                try {
                    bm = BitmapFactory.decodeStream( this.getContentResolver().openInputStream(data.getData()));
                    iv_qiandaotupian2.setImageBitmap(bm);
                    iv_qiandaotupian2.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    Toast.makeText(this,"未找到图片",Toast.LENGTH_LONG).show();
                }


                //}
                if (!TextUtils.isEmpty(String.valueOf(data.getData()))) {
                    Toast.makeText(this, "图片添加完成",  Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "图片添加失败",  Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    private void imageChooseItem(CharSequence[] items )
    {
        AlertDialog imageDialog = new AlertDialog.Builder(this).setTitle("上传文件").setIcon(android.R.drawable.btn_star).setItems(items,
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int item)
                    {
                        //判断是否挂载了SD卡
//					String storageState = Environment.getExternalStorageState();
//					if(storageState.equals(Environment.MEDIA_MOUNTED)){
//						File savedir = new File(FILE_SAVEPATH);
//						if (!savedir.exists()) {
//							savedir.mkdirs();
//						}
//					}
//					else{
//						UIHelper.ToastMessage(context, "无法保存上传的头像，请检查SD卡是否挂载");
//						return;
//					}

                        //输出裁剪的临时文件
//					String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        //照片命名
//					String fileName = "ntgis_" + timeStamp + ".jpg";
//					file = new File(FILE_SAVEPATH, fileName);

//					origUri = Uri.fromFile(file);

                        //相册选图
                        if(item == 0) {
//						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//						intent.addCategory(Intent.CATEGORY_OPENABLE);
//						intent.setType("image/*");
//						activity.startActivityForResult(Intent.createChooser(intent, "选择图片"),ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);

                            Intent intent;
                            if (Build.VERSION.SDK_INT < 19) {
                                intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "选择图片"),
                                        ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
                            } else {
                                intent = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "选择图片"),
                                        ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
                            }
                        }
                        //手机拍照
                        else if(item == 1){
//						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//						intent.putExtra(MediaStore.EXTRA_OUTPUT, origUri);
//						activity.startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
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
                                Toast.makeText(QianDaoActivity.this, "无法保存照片，请检查SD卡是否挂载",
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

                            theLarge = savePath + fileName;// 该照片的绝对路径

                            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            startActivityForResult(intent,
                                    ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
                        }
                    }}).create();

        imageDialog.show();
    }
}
