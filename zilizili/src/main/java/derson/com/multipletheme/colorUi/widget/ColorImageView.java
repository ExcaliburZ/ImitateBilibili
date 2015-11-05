package derson.com.multipletheme.colorUi.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;


import derson.com.multipletheme.colorUi.ColorUiInterface;
import derson.com.multipletheme.colorUi.util.ViewAttributeUtil;

/**
 * Created by chengli on 15/6/8.
 */
public class ColorImageView extends ImageView implements ColorUiInterface {

    private int attr_img = -1;

    public ColorImageView(Context context) {
        super(context);
    }

    public ColorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attr_img = ViewAttributeUtil.getSrcAttribute(attrs);
        //TODO 获取<style name="Widget.App.ImageView" parent="">
//        <item name="android:background">?selectableItemBackgroundBorderless</item>
//        <item name="android:clickable">true</item>
//        <item name="android:scaleType">centerInside</item>
//        <item name="color_disable">@color/gray_dark</item>
//        <item name="colorControlNormal">@color/pink</item>
//        <item name="colorControlActivated">@color/pink</item>
//        <item name="colorControlHighlight">@color/pink_dark</item>
//        </style>
//        中的color_disable属性,设置ImageView颜色
    }

    public ColorImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attr_img = ViewAttributeUtil.getSrcAttribute(attrs);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setTheme(Resources.Theme themeId) {
        if (attr_img != -1) {
            ViewAttributeUtil.applyImageDrawable(this, themeId, attr_img);
        }
    }
}
