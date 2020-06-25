package com.xuanyuetech.tocoach.util;

import android.content.Context;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_LOW_MEMORY;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_MULTI_CODEC_WRONG;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_MUXER_START_FAILED;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_NO_VIDEO_TRACK;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_SETUP_AUDIO_ENCODER_FAILED;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_SETUP_CAMERA_FAILED;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_SETUP_MICROPHONE_FAILED;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_SETUP_VIDEO_DECODER_FAILED;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_SETUP_VIDEO_ENCODER_FAILED;
import static com.qiniu.pili.droid.shortvideo.PLErrorCode.ERROR_SRC_DST_SAME_FILE_PATH;

public class ToastUtils {
    private static void s(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void l(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
    //自定义toast显示时长 in Ms
    public void showMyToast(final Toast toast, final int cnt) {
        final Timer timer =new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        },0,3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt );
    }

    public static void toastErrorCode(Context context, int errorCode) {
        switch (errorCode) {
            case ERROR_SETUP_CAMERA_FAILED:
                ToastUtils.s(context, "摄像头配置错误");
                break;
            case ERROR_SETUP_MICROPHONE_FAILED:
                ToastUtils.s(context, "麦克风配置错误");
                break;
            case ERROR_NO_VIDEO_TRACK:
                ToastUtils.s(context, "该文件没有视频信息！");
                break;
            case ERROR_SRC_DST_SAME_FILE_PATH:
                ToastUtils.s(context, "源文件路径和目标路径不能相同！");
                break;
            case ERROR_MULTI_CODEC_WRONG:
                ToastUtils.s(context, "当前机型暂不支持该功能");
                break;
            case ERROR_SETUP_VIDEO_ENCODER_FAILED:
                ToastUtils.s(context, "视频编码器启动失败");
                break;
            case ERROR_SETUP_VIDEO_DECODER_FAILED:
                ToastUtils.s(context, "视频解码器启动失败");
                break;
            case ERROR_SETUP_AUDIO_ENCODER_FAILED:
                ToastUtils.s(context, "音频编码器启动失败");
                break;
            case ERROR_LOW_MEMORY:
                ToastUtils.s(context, "手机内存不足，无法对该视频进行时光倒流！");
                break;
            case ERROR_MUXER_START_FAILED:
                ToastUtils.s(context, "MUXER 启动失败, 请检查视频格式");
                break;
            default:
                ToastUtils.s(context, "错误码：" + errorCode + " 请检查您的网络");
        }
    }
}
