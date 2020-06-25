# 代碼混淆壓縮比，一般在0~7之間，預設為5一般這不做調整。
-optimizationpasses 5

# 混合時不用大小寫混合，混合後的類別名為小寫。
-dontusemixedcaseclassnames

# 指定不去忽略非公共庫的類別。
-dontskipnonpubliclibraryclasses

# 這句話能夠使我們的項目混淆後產生映射文件。
# 包含有類別名->混淆後類別名的映射關係。
-verbose

# 指定不忽略非公共庫的類別。
-dontskipnonpubliclibraryclassmembers
-dontshrink

# 不做預校驗，preverify是proguard的四個步驟之一，Android不需要preverify，去掉這一步能夠加快混淆速度。
-dontpreverify

# 保留Annotation不混淆。
-keepattributes *Annotation*,InnerClasses

# 屏蔽警告
-ignorewarnings

# 避免混淆泛型。
-keepattributes Signature

# 異常時保留顯示行號。
-keepattributes SourceFile,LineNumberTable

# 指定混淆採用的算法，後面的參數是個過濾器。
# 此過濾器事google推薦的算法一般不做更改。
-optimizations !code/simplification/cast,!field/*,!class/merging/*

# 保留我們使用的四大组件，自定義的Application等等這些類別不被混淆。
# 因為这些子類別都有可能被外部調用。
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Appliction
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService

# 保留繼承。
-keep public class * extends androidx.**
# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

# 保留R下面的資源。
-keep class **.R$* {*;}

# 保留在Activity中的方法参数是view的方法，
# 這樣以来我們在layout中寫的onClick就不會被影響。
-keepclassmembers class * extends android.app.Activity{
public void *(android.view.View);
}

# 保留列舉類不被混淆。
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}

# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
*** get*();
void set*(***);
public <init>(android.content.Context);
public <init>(android.content.Context, android.util.AttributeSet);
public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep public class * {
<methods>;
}

# 保留Parcelable序列化類別不被混淆。
-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}

# 保留Serializable序列化的類別不被混淆。
-keepclassmembers class * implements java.io.Serializable {
static final long serialVersionUID;
private static final java.io.ObjectStreamField[] serialPersistentFields;
!static !transient <fields>;
!private <fields>;
!private <methods>;
private void writeObject(java.io.ObjectOutputStream);
private void readObject(java.io.ObjectInputStream);
java.lang.Object writeReplace();
java.lang.Object readResolve();
}

# 對於帶有回調函數的onXXEvent、**On*Listener的，不能被混淆。
-keepclassmembers class * {
void *(**On*Event);
void *(**On*Listener);
}

#
#----------------------------- WebView(项目中没有可以忽略) -----------------------------
#
#webView需要进行特殊处理
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
#在app中与HTML5的JavaScript的交互进行特殊处理
#我们需要确保这些js要调用的原生方法不能够被混淆，于是我们需要做如下处理：
-keepclassmembers class com.ljd.example.JSInterface {
    <methods>;
}

# ----------------------------- 其他的 -----------------------------
#
# 删除代码中Log相关的代码
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**