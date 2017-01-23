package com.isnc.dev;

import android.content.res.Configuration;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.isnc.facesdk.aty.Aty_BaseGroupCompare;
import com.isnc.facesdk.common.DebugMode;
import com.isnc.facesdk.common.SuperIDUtils;

import org.json.JSONObject;

import java.util.HashMap;


public class FaceGroupCompareDemo extends Aty_BaseGroupCompare {

    TextView tv;
    FrameLayout mContainer;
    String gidString, sidString;

    @Override
    protected int getContentLayoutId() {
        return R.layout.aty_facesdemo;
    }

    @Override
    protected void initView() {
        mContainer = (FrameLayout) findViewById(R.id.container);
        tv = (TextView) findViewById(R.id.textView1);
        //初始化多人脸,size为数组，{width，height}设置surfaceview宽高
        //若为null，则为全屛
//        int[] size = {480, 640};
        //initFacesFeature(size);
        //横竖屏切换
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            initFacesFeature(size);
            isPortrait = true;
        } else {
            isPortrait = false;
            initFacesFeature(sizeLand);
            mFacesGroupCompareView.setLand(true);
        }
        //设置摄像头 前1 后0
//        setCameraType(0);
//        //开始执行人脸检测
//        facesDetect(sidString, gidString);
    }


    @Override
    protected void initData() {
        gidString = getIntent().getStringExtra("groupId");
        HashMap<String, String> infos = SuperIDUtils.getappinfo(this, "SUPERID_APPKEY", "SUPERID_SECRET");
        sidString = infos.get("SUPERID_APPKEY");
    }

    @Override
    protected void initActions() {
        mContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //切换摄像头
                changeCamera();
            }
        });
    }


    //抓取人脸post服务器 返回数据操作
    @Override
    protected void doFacesCallBack(JSONObject result) {
        tv.setVisibility(View.VISIBLE);
        tv.setText(result.toString());
        super.doFacesCallBack(result);
    }

    //抓去人脸返回失败处理
    @Override
    protected void doFacesCallBackFail(int errorCode) {
        tv.setText("获取失败,errorCode:" + errorCode);
        Toast.makeText(this, "获取失败", Toast.LENGTH_SHORT).show();
        super.doFacesCallBackFail(errorCode);
    }

    @Override
    protected void requestFacesData() {

        tv.setVisibility(View.VISIBLE);
        tv.setText("请求数据中");
        super.requestFacesData();
    }

    //重试
    public void btnRetry(View v) {
        initFacesFeature(isPortrait ? null : sizeLand);
        tv.setVisibility(View.GONE);
        facesDetect(sidString, gidString);
    }

    int[] sizeLand = {640, 480};//横屏时宽高比
    int[] size = {480, 640};//竖屏是宽高
    boolean isPortrait;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                isPortrait = false;
                initFacesFeature(sizeLand);
                mFacesGroupCompareView.setLand(true);
                facesDetect(sidString, gidString);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                isPortrait = true;
                initFacesFeature(size);
                mFacesGroupCompareView.setLand(false);
                facesDetect(sidString, gidString);
                break;
        }
    }

    @Override
    public void onPause() {
        DebugMode.debug(">>>>onPause>>>>");
        super.onPause();
    }
}
