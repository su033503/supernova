package com.example.administrator.audiorecorder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2016-05-29.
 */
public class SubActivityHelp extends Fragment{
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sub_help, container, false);
        final LinearLayout optionHelp = (LinearLayout)view.findViewById(R.id.optionHelp);
        final LinearLayout settingHelp = (LinearLayout)view.findViewById(R.id.settingHelp);
        optionHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionHelp.setBackgroundColor(Color.rgb(221,221,221));
                View licenseView = inflater.inflate(R.layout.optionhelp_dialog,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("설정 도움말");
                builder.setView(licenseView);

                builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        optionHelp.setBackgroundColor(Color.WHITE);
                        optionHelp.setBackgroundResource(R.drawable.border);
                    }
                });
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    public boolean onKey(DialogInterface dialog,
                                         int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
                            optionHelp.setBackgroundColor(Color.WHITE);
                            optionHelp.setBackgroundResource(R.drawable.border);
                            return true;
                        }
                        return false;
                    }
                });
                final AlertDialog dialog=builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
        settingHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingHelp.setBackgroundColor(Color.rgb(221,221,221));
                View licenseView = inflater.inflate(R.layout.helplist_dialog,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("통화기록 도움말");
                builder.setView(licenseView);

                builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        settingHelp.setBackgroundColor(Color.WHITE);
                        settingHelp.setBackgroundResource(R.drawable.border);
                    }
                });
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    public boolean onKey(DialogInterface dialog,
                                         int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
                            settingHelp.setBackgroundColor(Color.WHITE);
                            settingHelp.setBackgroundResource(R.drawable.border);
                            return true;
                        }
                        return false;
                    }
                });
                final AlertDialog dialog=builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
        return view;
    }

}
