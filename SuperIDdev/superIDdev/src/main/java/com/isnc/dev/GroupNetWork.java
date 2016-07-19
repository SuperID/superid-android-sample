package com.isnc.dev;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.isnc.facesdk.SuperID;
import com.isnc.facesdk.common.Cache;
import com.isnc.facesdk.common.DebugMode;
import com.isnc.facesdk.common.SDKConfig;
import com.isnc.facesdk.common.SuperIDUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liangjie on 16/7/5.
 * Description:TODO
 */
public class GroupNetWork {

    private Context mContext;
    String BASE_URL = "https://api.superid.me/v1";
    static String URL_CREAT_GROUP = "/group/create";
    static String URL_GET_GROUP_INFO = "/group/:id/info";
    static String URL_GET_GROUP_USERS = "/group/:id/users";
    static String URL_GET_GROUP_LIST = "/group/list";
    static String URL_GROUP_INFO = "/group/:id";
    static String URL_GROUP_USER = "/group/:id/users";
    String TOKEN = "NgR5MqEelrEWxhPgkSeHX1PH9bofpL";
    String noncestr = "123456789";
    String appId;
    String groupUId = "";
    ExecutorService fix = Executors.newFixedThreadPool(4);

    public GroupNetWork(Context context) {
        mContext = context;
        HashMap<String, String> infos = SuperIDUtils.getappinfo(context, "SUPERID_APPKEY", "SUPERID_SECRET");
        appId = infos.get("SUPERID_APPKEY");
        String appinfo = Cache.getCached(context, SDKConfig.KEY_APPINFO);
        try {
            JSONObject object = new JSONObject(appinfo);
            groupUId = object.getString("group_uid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SuperID.setDebugMode(true);
    }

    /****
     * 新建一个group
     *
     * @param name 名称，必传
     * @param tag  描述，可不传
     * @param info 扩展信息，可不传
     */
    public void createGroup(String name, String tag, HashMap<String, String> info, final OnNetworkCallBack callBack) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("app_id", appId);
        params.put("timestamp", System.currentTimeMillis() + "");
        params.put("noncestr", noncestr);
        params.put("name", name);
        params.put("source_app_id", appId);
        if (!TextUtils.isEmpty(tag)) {
            params.put("tag", tag);
        }
        if (info != null) {
            //info必须传json，此处用gson将HashMap转为json字符串，可自行选择转换方法
            params.put("info", new Gson().toJson(info));
        }
        try {
            String signature = getSignature(params, TOKEN);
            params.put("signature", signature);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String url = BASE_URL + URL_CREAT_GROUP;
        fix.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    sendPost(url, params, callBack);
                } catch (Exception e) {
                    DebugMode.debug(">>>>error>" + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /***
     * 删除群组
     *
     * @param groupId 组id
     */
    public void deleteGroup(String groupId, final OnNetworkCallBack callBack) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("app_id", appId);
        params.put("timestamp", System.currentTimeMillis() + "");
        params.put("noncestr", noncestr);
        try {
            String signature = getSignature(params, TOKEN);
            params.put("signature", signature);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String url = BASE_URL + URL_GROUP_INFO.replace(":id", groupId);
        fix.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    requetDelete(url, params, callBack);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static String getParamsString(HashMap<String, String> params) {
        String p = "?";
        for (String key : params.keySet()) {
            String value = params.get(key);
            try {
                value = URLEncoder.encode(params.get(key), "UTF-8");//将参数URLEncoder
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            p = p + key + "=" + value + "&";
        }
        return p.substring(0, p.length() - 1);
    }

    /***
     * 将用户加入群组
     *
     * @param openId  用户id，多个id用,隔开
     * @param groupId group的id
     */
    public void addUserToGroup(String openId, String groupId, final OnNetworkCallBack callBack) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("app_id", appId);
        params.put("timestamp", System.currentTimeMillis() + "");
        params.put("noncestr", noncestr);
        if (!TextUtils.isEmpty(groupUId)) {
            params.put("group_uid", groupUId);
        }
        params.put("open_id", openId);
        try {
            String signature = getSignature(params, TOKEN);
            params.put("signature", signature);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String url = BASE_URL + URL_GET_GROUP_USERS.replace(":id", groupId);
        DebugMode.debug(">>>>=>" + url);
        fix.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    sendPost(url, params, callBack);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /***
     * @param openId  用户id，多个id用,隔开
     * @param groupId group的id
     */
    public void deleteUserFromGroup(String openId, String groupId, final OnNetworkCallBack callBack) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("app_id", appId);
        params.put("timestamp", System.currentTimeMillis() + "");
        params.put("noncestr", noncestr);
        params.put("open_id", openId);
        try {
            String signature = getSignature(params, TOKEN);
            params.put("signature", signature);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String url = BASE_URL + URL_GET_GROUP_USERS.replace(":id", groupId);
        //TODO 此处网络请求可改为应用相应的网络请求
        fix.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    requetDelete(url, params, callBack);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getGroupList(String tag, final OnNetworkCallBack networkCallBack) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("app_id", appId);
        params.put("timestamp", System.currentTimeMillis() + "");
        params.put("noncestr", noncestr);
        if (!TextUtils.isEmpty(tag)) {
            params.put("tag", tag);
        }
        try {
            String signature = getSignature(params, TOKEN);
            params.put("signature", signature);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String url = BASE_URL + URL_GET_GROUP_LIST;
        fix.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    requetGet(url, params, networkCallBack);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /***
     * 签名生成算法
     *
     * @param params 请求参数集，所有参数必须已转换为字符串类型，值已进行encode
     * @param secret 签名密钥
     * @return 签名
     * @throws IOException
     */
    private String getSignature(HashMap<String, String> params, String secret) throws Exception {
        // 先将参数以其参数名的字典序升序进行排序
        Map<String, String> sortedParams = new TreeMap<String, String>(params);
        Set<Map.Entry<String, String>> entrys = sortedParams.entrySet();

        // 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
        StringBuilder basestring = new StringBuilder();
        for (Map.Entry<String, String> param : entrys) {
            basestring.append(param.getKey()).append("=").append(URLEncoder.encode(param.getValue(), "UTF-8")).append("&");
        }
        basestring.delete(basestring.length() - 1, basestring.length()).append(":").append(secret);
        // System.out.println(basestring);
        // 使用MD5对待签名串求签
        byte[] bytes;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            bytes = md5.digest(basestring.toString().getBytes("UTF-8"));
        } catch (GeneralSecurityException ex) {
            throw new Exception(ex);
        }

        // 将MD5输出的二进制结果转换为小写的十六进制
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex);
        }
        return sign.toString().toUpperCase();
    }


    public interface OnNetworkCallBack {
        void onSuccessCallBack(Object data);

    }

    public void sendPost(String url, HashMap<String, String> params, OnNetworkCallBack callBack) throws Exception {

        URL obj = new URL(url);

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        con.setDoOutput(true);
        con.getOutputStream().write(postDataBytes);

        int responseCode = con.getResponseCode();
        String msg = con.getResponseMessage();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        System.out.println("Response : " + msg);

        BufferedReader in;
        if (200 <= responseCode && responseCode <= 299) {
            in = new BufferedReader(new InputStreamReader((con.getInputStream())));
        } else {
            in = new BufferedReader(new InputStreamReader((con.getErrorStream())));
        }
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        callBack.onSuccessCallBack(response.toString());
        //print result
        System.out.println(response.toString());

    }

    public void requetGet(String url, HashMap<String, String> params, OnNetworkCallBack callBack) throws Exception {
        url = url + getParamsString(params);

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in;
        if (200 <= responseCode && responseCode <= 299) {
            in = new BufferedReader(new InputStreamReader((con.getInputStream())));
        } else {
            in = new BufferedReader(new InputStreamReader((con.getErrorStream())));
        }
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        callBack.onSuccessCallBack(response);
        //print result
        System.out.println(response.toString());
    }

    public void requetDelete(String url, HashMap<String, String> params, OnNetworkCallBack callBack) throws Exception {
        url = url + getParamsString(params);

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("DELETE");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'DELETE' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in;
        if (200 <= responseCode && responseCode <= 299) {
            in = new BufferedReader(new InputStreamReader((con.getInputStream())));
        } else {
            in = new BufferedReader(new InputStreamReader((con.getErrorStream())));
        }
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        callBack.onSuccessCallBack(response.toString());
        //print result
        System.out.println(response.toString());
    }
}
