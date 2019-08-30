# AShadow
Android阴影控件库

## 使用
```
implementation 'com.mosect.AShadow:1.0.8'
```

## 更新记录
### 1.0.8
* 修复ShadowLinearLayout和ShadowRelativeLayout渲染位置不对的问题
* 修复ShadowKey比对方法
### 1.0.5
* 增加ShadowLinearLayout和ShadowRelativeLayout
### 1.0.4
* 增加FastShadow，用于快速渲染阴影（渲染速度极大提升）
* ShadowLayout改用FastShadow
* 启用clipShadow属性，不再支持，在2.0版本将会移除
* 不推荐使用RectChildShadow和AbsChildShadow，在2.0版本将会移除
### 1.0.3
* 增加spaceShadow属性，控制阴影是否影响视图位置
### 1.0.2
* 增加clipShadow属性，控制是否填充视图部分
* 增加solidColor属性，填充的颜色
* 增加圆角属性：roundRadius、roundLT、roundRT、roundRB、roundLB
### 1.0.1
**此版本有问题，请勿使用**
### 1.0.0
* 阴影布局ShadowLayout，其子视图可以设置一个阴影，支持的属性：shadowColor、shadowRadius、shadowX、shadowY

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
裁剪阴影（不再使用） | ShadowLayout.LayoutParams.clipShadow | app:layout_clipShadow | boolean
填充颜色 | ShadowLayout.LayoutParams.shadowInfo.setSolidColor | app:layout_solidColor | color
阴影占用空间 | ShadowLayout.LayoutParams.spaceShadow | app:layout_spaceShadow | boolean

## 说明：
放到ShadowLayout中的视图都可以设置阴影，默认阴影半径和偏移量会影响其位置。可以通过设置spaceShadow（**ShadowLinearLayout和ShadowRelativeLayout不支持**）控制阴影是否影响视图位置。

增加FastShadow，用于快速渲染子视图，**不再需要关闭硬件加速**。复写ViewGroup的drawChild方法，并调用FastShadow的drawChild方法：
```
    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        priChildShadow.drawChild(canvas, this, child);
        return super.drawChild(canvas, child, drawingTime);
    }
```
FastShadow是一个抽象类，需要实现部分方法才能使用，例如，ShadowLayout的实现：
```
    priChildShadow = new FastShadow() {
        @Nullable
        @Override
        protected ShadowInfo getShadowInfo(@NonNull ViewGroup parent, @NonNull View child) {
            // 此方法返回子视图的阴影信息
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (null != lp) {
                return lp.shadowInfo;
            }
            return null;
        }

        @Nullable
        @Override
        protected Object getChildShadowKey(@NonNull ViewGroup parent, @NonNull View child) {
            // 此方法返回子视图的阴影对应的key，不同的阴影拥有不同的key，推荐使用ShadowKey类
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (null != lp) {
                return lp.shadowKey;
            }
            return null;
        }

        @Override
        public float[] getChildRounds(@NonNull ViewGroup parent, @NonNull View child) {
            // 此方法返回子视图的圆角
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (null != lp) {
                return lp.roundRadius;
            }
            return null;
        }

        @NonNull
        @Override
        protected Object copyKey(@NonNull Object key) {
            // 最好复写这个方法，用于复制阴影的key对象，默认直接返回原对象
            return ((ShadowKey) key).clone();
        }
    };
```
其中，copyKey方法不是必须，但最好去复写它，然后返回一个key的副本，如果采用ShadowKey，可以直接调用clone方法即可。

**已取消clipShadow属性，此属性不再生效**

# 联系信息
```
QQ：905340954
邮箱：zhouliuyang1995@163.com
网站：http://www.mosect.com （建设中）
```
