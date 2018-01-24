package com.exer.widgets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.exer.videocollection.R;
import com.exer.videocollection.VideoPlayActivity;

import java.io.File;
import java.util.Date;

import io.vov.vitamio.widget.MediaController;

public class MyMediaController extends MediaController {
    private Context mContext;
    private View mVolumeBrightnessLayout;
    private ImageView mOperationBg;
    private ImageView mOperationPercent;
    private AudioManager mAudioManager;
    private int mMaxVolume;
    private int mVolume = -1;
    private float mBrightness = -1f;
    private GestureDetector mGestureDetector;

    VideoPlayActivity activity;
    private ImageView mediacontroller_screen_fit;
    /**
     * public static final int VIDEO_LAYOUT_ORIGIN
     缩放参数，原始画面大小。
     常量值：0

     public static final int VIDEO_LAYOUT_SCALE
     缩放参数，画面全屏。
     常量值：1

     public static final int VIDEO_LAYOUT_STRETCH
     缩放参数，画面拉伸。
     常量值：2

     public static final int VIDEO_LAYOUT_ZOOM
     缩放参数，画面裁剪。
     常量值：3
     */
    private String[] strDialogs=new String[]{"100%","全屏","拉伸","裁剪"};
    private int[] imgs=new int[]{R.drawable.mediacontroller_sreen_size_100,R.drawable.mediacontroller_screen_fit,R.drawable.mediacontroller_screen_size,R.drawable.mediacontroller_sreen_size_crop};
    private int mCurrentPageSize=2;

    @SuppressLint("HandlerLeak")
    private Handler mDismissHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                mVolumeBrightnessLayout.setVisibility(View.GONE);
            }
        }
    };

    public MyMediaController(Context context) {
        super(context);
        this.mContext = context;
        activity=(VideoPlayActivity)context;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        assert mAudioManager != null;
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mGestureDetector = new GestureDetector(mContext, new VolumeBrightnesGestureListener());
    }

    @Override
    protected View makeControllerView() {
        return ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.media_controller, this);//(getResources().getIdentifier("mediacontroller", "layout", mContext.getPackageName()), this);
    }

    @Override
    protected void initOtherView() {
        ImageView mediacontroller_snapshot = mRoot.findViewById(R.id.mediacontroller_snapshot);
        ImageView mediacontroller_previous = mRoot.findViewById(R.id.mediacontroller_previous);
        ImageView mediacontroller_next = mRoot.findViewById(R.id.mediacontroller_next);
        mediacontroller_screen_fit= mRoot.findViewById(R.id.mediacontroller_screen_fit);
        mediacontroller_snapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap currentFrame = activity.getCurrentFrame();
                //保存到本地
                String picturnPath= activity.getExternalCacheDir()+ File.separator+new Date().getTime()+".jpg";
                boolean saveSuccess = Tools.saveBitmap(currentFrame, new File(picturnPath));
                if(saveSuccess){
                    Toast.makeText(activity, "截屏保存到"+picturnPath,
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(activity, "截屏失败",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        mediacontroller_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.reverseVideo();
            }
        });
        mediacontroller_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.speedVideo();
            }
        });
        mediacontroller_screen_fit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentPageSize++;
                if(mCurrentPageSize>3){
                    mCurrentPageSize=0;
                }
                Toast.makeText(activity, strDialogs[mCurrentPageSize],
                        Toast.LENGTH_SHORT).show();
                mediacontroller_screen_fit.setBackground(getResources().getDrawable(imgs[mCurrentPageSize]));
                activity.setVideoPageSize(mCurrentPageSize);
            }
        });

        mVolumeBrightnessLayout = mRoot.findViewById(R.id.operation_volume_brightness);
        mOperationBg = mRoot.findViewById(R.id.operation_bg);
        mOperationPercent = mRoot.findViewById(R.id.operation_percent);
        mRoot.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //LogUtils.i(LogUtils.LOG_TAG, "onTouchEvent");
                if (mGestureDetector.onTouchEvent(event)) {
                    return true;
                }
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        endGesture();
                        break;
                }
                return false;
            }
        });
    }

    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;
        // 隐藏
        mDismissHandler.removeMessages(0);
        mDismissHandler.sendEmptyMessageDelayed(0, 500);
    }

    private class VolumeBrightnesGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            int y = (int) e2.getRawY();
            int x = (int) e2.getRawX();
            Display disp = ((Activity) mContext).getWindowManager().getDefaultDisplay();
            int windowWidth = disp.getWidth();
            int windowHeight = disp.getHeight();
            if (Math.abs(x - mOldX) < Math.abs(mOldY - y)){
                if (mOldX > windowWidth * 0.45) {
                    onVolumeSlide((mOldY - y) / windowHeight);
                    return true;
                } else if (mOldX < windowWidth * 0.45) {
                    onBrightnessSlide((mOldY - y) / windowHeight);
                    return true;
                }
            }else{
                if (mOldX - x > windowWidth * 0.2){
                    activity.reverseVideo();
                    return true;
                }else if(x -  mOldX> windowWidth * 0.2){
                    activity.speedVideo();
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e){
            Display disp = ((Activity) mContext).getWindowManager().getDefaultDisplay();
            //int windowWidth = disp.getWidth();
            int windowHeight = disp.getHeight();
            int y = (int) e.getY();
            if (y > 30 && y < windowHeight -100){
                hide();
                return true;
            }
            return false;
        }
    }

    /**
     * 声音高低
     */
    private void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;

            mOperationBg.setImageResource(R.drawable.video_volumn_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }

        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = (findViewById(R.id.operation_full)).getWidth() * index / mMaxVolume;
        mOperationPercent.setLayoutParams(lp);
    }

    /**
     * 处理屏幕亮暗
     */
    private void onBrightnessSlide(float percent) {
        if (mBrightness < 0) {
            mBrightness = ((Activity) mContext).getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;

            mOperationBg.setImageResource(R.drawable.video_brightness_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }
        WindowManager.LayoutParams lpa = ((Activity) getContext()).getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        ((Activity) mContext).getWindow().setAttributes(lpa);

        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = (int) ((findViewById(R.id.operation_full)).getWidth() * lpa.screenBrightness);
        mOperationPercent.setLayoutParams(lp);
    }
}
