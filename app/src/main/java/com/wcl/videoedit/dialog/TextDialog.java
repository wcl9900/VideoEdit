package com.wcl.videoedit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.wcl.videoedit.R;
import com.wcl.videoedit.bean.TextEntity;
import com.wcl.videoedit.constant.EpMapColor;

import VideoHandle.EpText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wangchunlong on 2018/7/23.
 */

public class TextDialog extends Dialog {
    @BindView(R.id.edt_text_number)
    EditText edtTextNumber;
    @BindView(R.id.edt_text_color)
    Spinner spinnerColor;
    @BindView(R.id.edt_text)
    EditText edtText;

    private TextView showTextView;
    private TextEntity textEntity;

    public TextDialog(@NonNull Context context, TextEntity textEntity, TextView showTextView) {
        super(context);
        this.textEntity = textEntity;
        this.showTextView = showTextView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_text_setting);
        ButterKnife.bind(this);
        edtText.setText(textEntity.getText());
        edtTextNumber.setText(textEntity.getSize() + "");
        spinnerColor.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, EpText.Color.values()));
        int selectPos = 0;
        for (int index = 0; index < spinnerColor.getAdapter().getCount(); index++) {
            if (TextUtils.equals(((EpText.Color) spinnerColor.getAdapter().getItem(index)).getColor(), textEntity.getColor())) {
                selectPos = index;
                break;
            }
        }
        spinnerColor.setSelection(selectPos);
        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setText(((EpText.Color) parent.getAdapter().getItem(position)).getColor());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @OnClick({R.id.btn_cancel, R.id.btn_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                break;
            case R.id.btn_sure:
                textEntity.setText(edtText.getText().toString());
                textEntity.setSize(Float.parseFloat(edtTextNumber.getText().toString()));
                textEntity.setColor(((EpText.Color) spinnerColor.getSelectedItem()).getColor());

                showTextView.setText(textEntity.getText());
                showTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textEntity.getSize());
                showTextView.setTextColor(EpMapColor.valueOf(textEntity.getColor()).getColor());
                break;
        }
        dismiss();
    }
}
