package com.lirui.learn.face.net;

import com.lirui.learn.bean.InTheaters;
import com.lirui.learn.face.FaceBean;
import com.lirui.lib_common.net.HttpHelper;
import com.lirui.lib_common.net.converterFactory.FileUploadTransformer;
import com.lirui.lib_common.net.converterFactory.Transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;

/**
 * 作者：李蕊(1735613836@qq.com)
 * 2016/10/18 0018
 * 功能：网络请求
 */

public class ApiService {

    private volatile static ApiInterface appInterface;
    private volatile static ApiService appService;

    public static ApiService getAppService() {
        if (appService == null) {
            synchronized (ApiService.class) {
                if (appService == null) {
                    appService = new ApiService();
                    appInterface = HttpHelper.getInstance().getApi(ApiInterface.class);
                }
            }
        }
        return appService;
    }

    /**
     * 图片上传
     *
     * @param filePath 图片路径
     */
    public Observable<FaceBean> uploadImage(String filePath) {
        ArrayList<String> paths = new ArrayList<>();
        paths.add(filePath);
        List<MultipartBody.Part> parts = FileUploadTransformer.files2Parts(null,"files", paths, null);
        return appInterface.updateFiles(parts)
                .compose(Transformer.<FaceBean>switchSchedulers());
    }

    public Observable<FaceBean> testWeb() {
        HashMap<String, Object> param = new HashMap<>();
        param.put("key", "id");
        return appInterface.testweb(param).compose(Transformer.<FaceBean>switchSchedulers());
    }
}
