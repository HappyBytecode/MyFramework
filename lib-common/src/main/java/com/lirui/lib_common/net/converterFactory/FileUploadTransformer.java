package com.lirui.lib_common.net.converterFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Retrofit2文件上传的封装
 */
public class FileUploadTransformer {

//    @Multipart
//    @POST(RequestApiPath.UPLOAD_WORK)
//    Observable<String> fileupload(@PartMap Map<String, RequestBody> parts);
    public static Map<String, RequestBody> files2RequestBody(HashMap<String, String> parameters, String key, List<String> filePaths, MediaType imageType) {

        Map<String, RequestBody> files = new HashMap<>();

        if (parameters != null) {
            Set<String> keys = parameters.keySet();
            for (String paraKey : keys) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), parameters.get(paraKey));
                files.put(paraKey, requestBody);
            }
        }

        String tmpKey = key;
        if (filePaths.size() > 1) {
            tmpKey = key + "0";
        }
        for (int i = 0; i < filePaths.size(); i++) {
            String path = filePaths.get(i);
            File file = new File(path);
            if (imageType == null) {
                imageType = MediaType.parse("multipart/form-data");
            }
            RequestBody body = RequestBody.create(imageType, file);

            files.put(tmpKey + "\";filename=\"" + file.getName() + "\"", body);
            tmpKey = key + (i + 1);
        }
        return files;
    }

//    @Multipart
//    @POST(RequestApiPath.UPLOAD_WORK)
//    Observable<BaseResp> requestUploadWork(@PartMap Map<String, RequestBody> params,
//                                           @Part List<MultipartBody.Part> parts);
    public static List<MultipartBody.Part> files2Parts(HashMap<String, String> parameters, String key,
                                                       List<String> filePaths, MediaType imageType) {
        List<MultipartBody.Part> parts = new ArrayList<>();

        if (parameters != null) {
            Set<String> keys = parameters.keySet();
            for (String paraKey : keys) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), parameters.get(paraKey));
                MultipartBody.Part part = MultipartBody.Part.createFormData(paraKey, null, requestBody);
                parts.add(part);
            }
        }

        String tmpKey = key;
        if (filePaths.size() > 1) {
            tmpKey = key + "0";
        }
        for (int i = 0; i < filePaths.size(); i++) {
            File file = new File(filePaths.get(i));
            if (imageType == null) {
                imageType = MediaType.parse("multipart/form-data");
            }
            // 根据类型及File对象创建RequestBody（okhttp的类）
            RequestBody requestBody = RequestBody.create(imageType, file);
            // 将RequestBody封装成MultipartBody.Part类型（同样是okhttp的）
            MultipartBody.Part part = MultipartBody.Part.
                    createFormData(tmpKey, file.getName(), requestBody);
            // 添加进集合
            parts.add(part);
            tmpKey = key + (i + 1);
        }
        return parts;
    }


//    @POST(RequestApiPath.UPLOAD_WORK)
//    Observable<BaseResp> requestUploadWork(@Body MultipartBody body);
    public static MultipartBody filesToMultipartBody(HashMap<String, String> parameters, String key,
                                                     List<String> filePaths,
                                                     MediaType imageType) {
        MultipartBody.Builder builder = new MultipartBody.Builder();

        if (parameters != null) {
            Set<String> keys = parameters.keySet();
            for (String paraKey : keys) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), parameters.get(paraKey));
                // MultipartBody.Builder的addFormDataPart()有一个直接添加key value的重载，但坑的是这个方法
                // 不会设置编码类型，会出乱码，所以可以使用3个参数的，将中间的filename置为null就可以了
                // builder.addFormDataPart(key, value);
                // 还有一个坑就是，后台取数据的时候有可能是有顺序的，比如必须先取文本后取文件，
                // 否则就取不到（真弱啊...），所以还要注意add的顺序
                builder.addFormDataPart(paraKey, null, requestBody);
            }
        }

        String tmpKey = key;
        if (filePaths.size() > 1) {
            tmpKey = key + "0";
        }
        for (int i = 0; i < filePaths.size(); i++) {
            File file = new File(filePaths.get(i));
            if (imageType == null) {
                imageType = MediaType.parse("multipart/form-data");
            }
            RequestBody requestBody = RequestBody.create(imageType, file);
            builder.addFormDataPart(tmpKey, file.getName(), requestBody);
            tmpKey = key + (i + 1);
        }
        builder.setType(MultipartBody.FORM);

        return builder.build();
    }
}
