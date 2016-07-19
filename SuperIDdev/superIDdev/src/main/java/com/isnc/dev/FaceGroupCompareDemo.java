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
    String groupId, appId;
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
        HashMap<String, String> infos = SuperIDUtils.getappinfo(this, "SUPERID_APPKEY", "SUPERID_SECRET");
        appId = infos.get("SUPERID_APPKEY");
        if (getResources().getConfiguration().orientation!=Configuration.ORIENTATION_LANDSCAPE) {
            initFacesFeature(size);
            isPortrait=true;
        }else {
            isPortrait=false;
            initFacesFeature(sizeLand);
            mFacesGroupCompareView.setPortrait(false, false);
        }
//        mFacesGroupCompareView.setPortrait(false, false);//横屏锁定时需要调用
        //设置摄像头 前1 后0
        setCameraType(1);
        //开始执行人脸检测
//        facesDetect();
        facesDetect(appId,groupId);
    }

    @Override
    protected void initData() {
        groupId=getIntent().getStringExtra("groupId");
    }

    @Override
    protected void initActions() {
        mContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //切换摄像头d
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
        tv.setText("获取失败,errorCode:"+errorCode);
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
        DebugMode.debug(">>>>>>>>"+isPortrait);
        initFacesFeature(isPortrait ? null : sizeLand);
        tv.setVisibility(View.GONE);
        facesDetect(appId, groupId);
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
                mFacesGroupCompareView.setPortrait(false, true);
                facesDetect(appId, groupId);
//                initFacesFeature(sizeLand);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                isPortrait = true;
                initFacesFeature(size);
                mFacesGroupCompareView.setPortrait(true, true);
                facesDetect(appId, groupId);
                break;
        }
    }
}
