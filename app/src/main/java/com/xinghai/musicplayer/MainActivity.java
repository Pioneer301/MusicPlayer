package com.xinghai.musicplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xinghai.musicplayer.Service.MusicService;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    int flag = 1;//设置一个标志：供点击“开始/暂停”按钮使用
    private Button btnStart, btnStop, btnNext, btnLast;
    private TextView txtInfo;
    private ListView listView;
    private SeekBar seekBar;
    private MusicService musicService = null;
    private Handler handler;//处理改变进度条时间
    int UPDATE = 0x101;
    private boolean autoChange, manulChange;//判断进度条是自动改变还是手动改变
    private boolean isPause;//判断是从暂停中恢复还是重新播放

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //动态获取权限--当手机系统大于23时，才有必要动态获取权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissionDynamic();
        }
        musicService = new MusicService();
        setContentView(R.layout.activity_main);
        setListViewAdapter();
    }

    private void requestPermissionDynamic() {
        int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission3 = ContextCompat.checkSelfPermission(this, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
        if (permission1 != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED ||
                permission3 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:

                break;
        }
    }

    //向列表中添加mp3名字
    private void setListViewAdapter() {
        String[] str = new String[musicService.musicList.size()];
        int i = 0;
        for (String path : musicService.musicList) {
            File file = new File(path);
            str[i++] = file.getName();
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, str);
        listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }
}
