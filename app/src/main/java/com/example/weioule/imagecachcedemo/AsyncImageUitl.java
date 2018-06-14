package com.example.weioule.imagecachcedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author weioule
 * Create on 2018/6/13.
 */
public class AsyncImageUitl {

    private LruCache<String, Bitmap> cache;//lru算法集合，string是图片的url，bitmap为图片的值类型
    private ExecutorService executorService;//维护线性池
    public static final int SUCCSEE = 0;
    public static final int FAIL = 1;
    private Context context;
    private Handler handler;

    public AsyncImageUitl(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;

        //维护几个网络线程下载图片
        executorService = Executors.newFixedThreadPool(5);
        int maxSize = (int) (Runtime.getRuntime().maxMemory() / 8);//获得运行环境的内存大小的1/8
        cache = new LruCache<String, Bitmap>(maxSize) {
            // TODO 每存储一张图片的大小（作用于内存溢出丢图片）
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //返回当前一行所占的字节数*高度，就是图片的大小
                return value.getRowBytes() * value.getHeight();
            }
        };//当前图片缓存总数的大小
    }

    /**
     * @param url 下载图片的连接
     * @return
     */
    public Bitmap getBitmapFromUrl(String url) {
        //1内存中获取图片LRU算法
        Bitmap bitmap = cache.get(url);
        //内存（缓存）中有指定图片
        if (bitmap != null) {
            Log.i("从内存中获得图片", url);
            return bitmap;
        }
        //2文件中（外部存储）获取图片
        bitmap = getBitmapFromFile(url);
        if (bitmap != null) {
            Log.i("从文件中获得图片", url);
            return bitmap;
        }
        //3开启网络下载
        Log.i("从网络中获得图片", url);
        getBitmapFromNet(url);
        return null;

    }

    /**
     * 网络获取图片
     *
     * @param url 图片的；链接地址
     */
    private void getBitmapFromNet(String url) {
        //开启任务
        executorService.execute(new RunnableTask(url));
    }

    //任务接口
    class RunnableTask implements Runnable {
        String imageUrl;
        private HttpURLConnection httpURLConnection;

        public RunnableTask(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @Override
        public void run() {
            //子线程的操作，开启网络下载图片
            try {
                URL url = new URL(imageUrl);
                //请求对象
                httpURLConnection = (HttpURLConnection) url.openConnection();
                //网络请求的方式
                httpURLConnection.setRequestMethod("GET");
                //超时的时间，
                httpURLConnection.setConnectTimeout(5000);
                //读取超时的时间
                httpURLConnection.setReadTimeout(5000);
                //httpURLConnection.getResponseCode()拿到最新数据
                if (httpURLConnection.getResponseCode() == 200) {
                    //success get data from net;get tape
                    InputStream inputStream = httpURLConnection.getInputStream();
                    //将流转化成bitmap图片
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    //利用handler机制放入主线程中显示
                    Message msg = new Message();
                    //需要在主线程中显示的图片msg.obj
                    msg.obj = bitmap;
                    //为msg设置标记
                    msg.what = SUCCSEE;
                    handler.sendMessage(msg);
                    //一，将下载完后的图片保存到内存中
                    cache.put(imageUrl, bitmap);
                    //二，将下载完后的图片保存到文件中
                    writeToLoce(imageUrl, bitmap);
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            //关闭请求
            finally {
                //断开服务器
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            //发送一个空消息
            handler.obtainMessage(FAIL).sendToTarget();
        }
    }

    /**
     * 图片写入cache文件夹下面的操作
     *
     * @param imageUrl
     * @param bitmap
     */
    private void writeToLoce(String imageUrl, Bitmap bitmap) {
        try {
            String bitmapefilename = md5Encode(imageUrl).substring(10);
            File file = new File(getDiskCacheDir(), bitmapefilename);

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            //写入文件的操作(1图片类型 2图片质量当为100时表示不压缩 3文件流)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件中图片的操作
     *
     * @param url 图片的连接地址
     * @return
     */
    private Bitmap getBitmapFromFile(String url) {
        try {
            //使用Md5工具加密截取前10个字符串
            String bitmapefilename = md5Encode(url).substring(10);
            File file = new File(getDiskCacheDir(), bitmapefilename);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            //2读取之后放入内存,提高效率
            cache.put(url, bitmap);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 获取缓存地址:
     * 当SD卡存在或者SD卡不可被移除的时候，就调用getExternalCacheDir()方法来获取缓存路径，否则就调用getCacheDir()方法来获取缓存路径
     */
    public String getDiskCacheDir() {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    //MD5 加密
    public String md5Encode(String string) throws Exception {
        byte[] hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}
