package com.xinghai.musicplayer.Service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {

    private static final File PATH = Environment.getExternalStorageDirectory();//获取SD卡根目录
    public List<String> musicList;//存放找到的所有mp3的绝对路径
    private MediaPlayer player;//定义多媒体对象
    private int songNum;//当前播放的歌曲在list中的下标，flag为标志
    private String songName;//当前播放的歌曲名

    public MusicService() {
        super();
        player = new MediaPlayer();//实例化一个多媒体对象
        musicList = new ArrayList<>();//实例化一个List链表数组
        try {
            File MUSIC_PATH = new File(PATH, "music");//获取根目录的二级目录Music
            if (MUSIC_PATH.listFiles(new MusicFilter()).length > 0) {
                for (File file : MUSIC_PATH.listFiles(new MusicFilter())) {
                    musicList.add(file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            Log.i("MusicService", "读取文件异常");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 播放
     */
    public void play() {
        player.reset();//重置多媒体
        String dataSource = musicList.get(songNum);//得到当前播放音乐的路径
        setPlayName(dataSource);//截取歌名
        //制定参数为音频文件
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(dataSource);//为多媒体对象设置播放路径
            player.prepare();//准备播放
            player.start();//开始播放
            //setOnCompletionListener 当当前多媒体对象播放完成时发生的事件
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    next();//如果当前歌曲播放完毕,自动播放下一首
                }
            });
        } catch (IOException e) {
            Log.v("MusicService", e.getMessage());
        }
    }

    /**
     * 继续播放
     */
    public void goPlay() {
        int position = getCurrentProgress();
        player.seekTo(position);//设置当前MediaPlayer的播放位置，单位是毫秒
        try {
            player.prepare();//同步的方式装载流媒体文件
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.start();
    }

    public int getCurrentProgress() {
        if (player != null && player.isPlaying()) {
            return player.getCurrentPosition();
        } else if (player != null && (!player.isPlaying())) {
            return player.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 下一首
     */
    private void next() {
        songNum = songNum == musicList.size() - 1 ? 0 : songNum + 1;
        play();
    }

    /**
     * 上一首
     */
    private void last() {
        songNum = songNum == 0 ? musicList.size() - 1 : songNum - 1;
        play();
    }

    /**
     * 暂停播放
     */
    private void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    /**
     * 停止播放
     */
    private void stop() {
        if (player != null && player.isPlaying()) {
            player.stop();
            player.reset();
        }
    }

    /**
     * 修改获取的mp3文件的名字：供TextView显示
     *
     * @param dataSource
     */
    public void setPlayName(String dataSource) {
        File file = new File(dataSource);//假设为D:\\dd.mp3
        String name = file.getName();//name=dd.mp3
        int index = name.lastIndexOf(".");
        songName = name.substring(0, index);
    }


    /**
     * 内部类：供加载MP3文件时调用
     */
    class MusicFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3"));//返回当前目录所有以.mp3结尾的文件
        }
    }
}
