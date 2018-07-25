package com.wcl.videoedit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wcl.videoedit.constant.PathConstant;
import com.wcl.videoedit.utils.FileUtils;
import com.wcl.videoedit.utils.ToastUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_setting_clear})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.btn_setting_clear:
                FileUtils.deleteFile(PathConstant.getExVideoDir());
                ToastUtils.show(this, "清空成功！");
                break;
        }
    }
}
