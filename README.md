# AShadow
Android阴影控件库

## 使用
```
implementation 'com.mosect:ViewUtils:1.0.6'
主项目，jcenter审核中，目前请先下载源码导入
```

## XML示例
```
<com.mosect.ashadow.ShadowLayout
  android:layout_width="wrap_content"
  android:layout_height="wrap_content">
  
  <View
    android:layout_width="100dp"
    android:layout_height="100dp"
    app:layout_shadowRadius="10dp"
    app:layout_shadowColor="#ff0000"
    app:layout_shadowX="2dp"
    app:layout_shadowY="4dp"/>
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

## 说明：
放到ShadowLayout中的视图都可以设置阴影，阴影半径和偏移量会影响其位置。

# 联系信息
```
QQ：905340954
邮箱：zhouliuyang1995@163.com
网站：http://www.mosect.com （建设中）
```
