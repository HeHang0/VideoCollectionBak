package com.exer.widgets;

import android.widget.ImageView;

/**
 * Created by HeHang on 2018/1/28.
 */

public interface OnShowThumbnailListener {
    /**回传封面的view，让用户自主设置*/
    void onShowThumbnail(ImageView ivThumbnail);
}
