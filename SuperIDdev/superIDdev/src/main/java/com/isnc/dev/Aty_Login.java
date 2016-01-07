package com.isnc.dev;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.isnc.facesdk.SuperID;
import com.isnc.facesdk.common.Cache;
import com.isnc.facesdk.common.SDKConfig;
import com.isnc.facesdk.soloader.SoDownloadManager;

import java.io.File;

public class Aty_Login extends Activity {
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aty_login);
		context = this;
	}

	// 一般登录
	public void btn_login(View v) {
		startActivity(new Intent(this, Aty_UserCenter.class));
		finish();

	}

	// 人脸登录
	public void btn_superidlogin(View v) {

        //sdk 已集成so库加 调用示例
//        SuperID.faceLogin(this);


        //sdk无加载so库 调用示例
		SuperID.faceLogin(this, new SuperID.SoLoaderCallback() {
			@Override
			public void soLoader() {
				doDownload();
			}
		});
	}

	private void doDownload() {
		final ProgressDialog mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("刷脸升级包下载");
		mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
		SoDownloadManager.checkSoAndDownload(this, new SoDownloadManager.ProgressCallback() {
			@Override
			public void getProgress(int progress) {
				//progress为下载进度0~100
				mProgressDialog.setProgress(progress);
				if (progress == 100) {
					//加载成功
					mProgressDialog.dismiss();
                    SuperID.faceLogin(Aty_Login.this);
				} else if (progress == -100) {
					//加载失败
					mProgressDialog.dismiss();
				}
			}
		});
	}

	// 清除缓存
	public void btn_clear(View v) {
		Cache.clearCached(context);
		delete(new File(SDKConfig.TEMP_PATH));
		Intent intent = new Intent(this, Aty_Welcome.class);
		startActivity(intent);
		finish();
	}

	public static void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}
	}

	// 接口返回
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (resultCode) {
		// 授权成功
		case SDKConfig.AUTH_SUCCESS:
			Intent intent = new Intent(this, Aty_UserCenter.class);
			startActivity(intent);
			finish();
			break;
		// 取消授权
		case SDKConfig.AUTH_BACK:

			break;
		// 找不到该用户
		case SDKConfig.USER_NOTFOUND:

			break;
		// 登录成功
		case SDKConfig.LOGINSUCCESS:
			System.out.println(Cache.getCached(context, SDKConfig.KEY_APPINFO));
			Intent i = new Intent(this, Aty_UserCenter.class);
			startActivity(i);
			finish();
			break;
		// 登录失败
		case SDKConfig.LOGINFAIL:
			break;
		// 网络有误
		case SDKConfig.NETWORKFAIL:
			break;
		// 一登SDK版本过低
		case SDKConfig.SDKVERSIONEXPIRED:
			break;
		default:
			break;
		}

	}
}
