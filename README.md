# AShadow
Android阴影控件库

## 使用
**gihub代码未更新（没有同步），请使用jcenter开源库**
```
implementation 'com.mosect.AShadow:1.0.2'
```

## XML示例
```
<?xml version="1.0" encoding="utf-8"?>
<com.mosect.ashadow.ShadowLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#cccccc"
    android:orientation="vertical">

    <View
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:layout_gravity="center"
        app:layout_clipShadow="false"
        app:layout_roundRadius="10dp"
        app:layout_shadowColor="#000000"
        app:layout_shadowRadius="10dp"
        app:layout_solidColor="#ffffff" />
</com.mosect.ashadow.ShadowLayout>
```
视图属性：

属性名 | JAVA | XML | 取值
----- | ---- | --- | ------
阴影半径 | ShadowLayout.LayoutParams.shadowInfo.setShadowRadius | app:layout_shadowRadius | dimen
阴影X偏移量 | ShadowLayout.LayoutParams.shadowInfo.setShadowX | app:layout_shadowX | dimen
阴影Y偏移量 | ShadowLayout.LayoutParams.shadowInfo.setShadowY | app:layout_shadowY | dimen
阴影颜色 | ShadowLayout.LayoutParams.shadowInfo.setShadowColor | app:layout_shadowColor | color
圆角半径 | ShadowLayout.LayoutParams.shadowInfo.setRoundRadius | app:layout_roundRadius | dimen
左上角圆角半径 | ShadowLayout.LayoutParams.shadowInfo.setRoundRadius | app:layout_roundLT | dimen
右上角圆角半径 | ShadowLayout.LayoutParams.shadowInfo.setRoundRadius | app:layout_roundRT | dimen
右下角圆角半径 | ShadowLayout.LayoutParams.shadowInfo.setRoundRadius | app:layout_roundRB | dimen
左下角圆角半径 | ShadowLayout.LayoutParams.shadowInfo.setRoundRadius | app:layout_roundLB | dimen
裁剪阴影 | ShadowLayout.LayoutParams.clipShadow | app:layout_clipShadow | boolean
填充颜色 | ShadowLayout.LayoutParams.shadowInfo.setSolidColor | app:layout_solidColor | color

## 说明：
放到ShadowLayout中的视图都可以设置阴影，阴影半径和偏移量会影响其位置。

**如果开启了clipShadow（默认），内容视图部分不会填充颜色，只会填充其阴影。因为使用了clip，因此在有圆角的阴影中，四个角会出现黑边，可以将solidColor属性设置成和背景一样的颜色，可以解决此问题。**

**如果关闭clipShadow属性，内容视图部分会被填充solidColor，solidColor不能含有透明度，否则会出现阴影不显示问题**

# 联系信息
```
QQ：905340954
邮箱：zhouliuyang1995@163.com
网站：http://www.mosect.com （建设中）
```
