package com.exer.widgets;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.exer.videocollection.R;

import java.lang.reflect.Field;

/**
 * Created by HeHang on 2018/1/14.
 */

public class MediaController extends android.widget.MediaController {
    private Activity mActivity;
    private View mView;
    private String VideoTitle;
    public MediaController(Activity context, String VideoTitle) {
        super(context);
        this.mActivity = context;
        this.VideoTitle = VideoTitle;
    }
    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);
        mView = LayoutInflater.from(getContext()).inflate(
                R.layout.media_controller_title, null);
        ImageButton quitBtn = mView.findViewById(R.id.mediacontroller_top_back);
        quitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActivity != null)
                    mActivity.finish();
            }
        });
        TextView title = mView.findViewById(R.id.mediacontroller_file_name);
        title.setText(this.VideoTitle);
        try {
//            SeekBar sb = (SeekBar) LayoutInflater.from(getContext()).inflate(
//                    R.layout.video_seekbar, null);
            Field mRoot = android.widget.MediaController.class
                    .getDeclaredField("mRoot");
            mRoot.setAccessible(true);
            ViewGroup mRootVg = (ViewGroup) mRoot.get(this);
            ViewGroup vg = findSeekBarParent(mRootVg);
//            int index = 1;
//            for (int i = 0; i < vg.getChildCount(); i++) {
//                if (vg.getChildAt(i) instanceof SeekBar) {
//                    index = i;
//                    break;
//                }
//            }
//            vg.removeViewAt(index);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//            params.weight = 1;
//            vg.addView(sb, index, params);
            mRootVg.addView(mView,0);
//            Field mProgress = android.widget.MediaController.class
//                    .getDeclaredField("mProgress");
//            mProgress.setAccessible(true);
//            mProgress.set(this, sb);
//            Field mSeekListener = android.widget.MediaController.class
//                    .getDeclaredField("mSeekListener");
//            mSeekListener.setAccessible(true);
//            sb.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) mSeekListener
//                    .get(this));
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ViewGroup findSeekBarParent(ViewGroup vg) {
        ViewGroup viewGroup = null;
        for (int i = 0; i < vg.getChildCount(); i++) {
            View view = vg.getChildAt(i);
            if (view instanceof SeekBar) {
                viewGroup = (ViewGroup) view.getParent();
                break;
            } else if (view instanceof ViewGroup) {
                viewGroup = findSeekBarParent((ViewGroup) view);
            } else {
                continue;
            }
        }
        return viewGroup;
    }
}
