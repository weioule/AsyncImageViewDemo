package com.example.weioule.imagecachcedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * author weioule
 * Create on 2018/6/13.
 */
public class AsyncImageView extends ImageView {

    private AsyncImageUitl mAsyncImageUitl;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case AsyncImageUitl.SUCCSEE:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    if (null != bitmap) {
                        setImageBitmap(bitmap);
                    }
                    break;
                case AsyncImageUitl.FAIL:
                    Toast.makeText(getContext().getApplicationContext(), "Download error", Toast.LENGTH_SHORT).show();
                    Log.i("图片加载错误", "Download error");
                default:
                    break;
            }
        }
    };

    public AsyncImageView(Context context) {
        this(context, null);
    }

    public AsyncImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AsyncImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mAsyncImageUitl = new AsyncImageUitl(getContext().getApplicationContext(), handler);
    }

    public void downloadCache2memory(String url) {
        Bitmap bitmap = mAsyncImageUitl.getBitmapFromUrl(url);
        if (null != bitmap) {
            setImageBitmap(bitmap);
        }
    }

}
