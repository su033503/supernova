package com.example.administrator.audiorecorder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class SubActivityAutoOption extends Fragment {
    Switch autoRunButton;
    Switch emotionButton;
    Switch autoSaveButton;
    EnvironmentSet changeEnvironmentSet = MainActivity.environmentSet;
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.sub_autooption, container, false);
        autoRunButton = (Switch)view.findViewById(R.id.autoRunButton);
        emotionButton = (Switch)view.findViewById(R.id.emotionButton);
        autoSaveButton = (Switch)view.findViewById(R.id.autoSaveButton);
        final ImageView settedBackgroundImage = (ImageView)view.findViewById(R.id.settedBackgroundImg);
        switch (changeEnvironmentSet.getBgNo()) {
            case 1: settedBackgroundImage.setImageResource(R.drawable.background8); break;
            case 2: settedBackgroundImage.setImageResource(R.drawable.background3); break;
            case 3: settedBackgroundImage.setImageResource(R.drawable.background0); break;
            case 4: settedBackgroundImage.setImageResource(R.drawable.background11); break;
        }
        System.out.println("autoRun값 여기"+IncomingCallBroadcastReceiver.autoRun);
        autoRunButton.setChecked(changeEnvironmentSet.getAutoPlay());
        autoRunButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
//                    IncomingCallBroadcastReceiver.autoRun = false;
                    changeEnvironmentSet.setAutoPlay(false);
                    Toast.makeText(getActivity(), "통화 시 자동 실행 기능 Off", Toast.LENGTH_SHORT).show();
                } else {
//                    IncomingCallBroadcastReceiver.autoRun = true;
                    changeEnvironmentSet.setAutoPlay(true);
                    Toast.makeText(getActivity(), "통화 시 자동 실행 기능 On", Toast.LENGTH_SHORT).show();
                }
                saveEnvironmentLog();
            }
        });
        emotionButton.setChecked(changeEnvironmentSet.getEmotionPlay());
        emotionButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
//                    IncomingCallBroadcastReceiver.autoRun = false;
                    changeEnvironmentSet.setEmotionPlay(false);
                    Toast.makeText(getActivity(), "감정분석 기능 Off", Toast.LENGTH_SHORT).show();
                } else {
//                    IncomingCallBroadcastReceiver.autoRun = true;
                    changeEnvironmentSet.setEmotionPlay(true);
                    Toast.makeText(getActivity(), "감정분석 기능 On", Toast.LENGTH_SHORT).show();
                }
                saveEnvironmentLog();
            }
        });
        autoSaveButton.setChecked(changeEnvironmentSet.getSaveLog());
        autoSaveButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
//                    IncomingCallBroadcastReceiver.autoRun = false;
                    changeEnvironmentSet.setSaveLog(false);
                    Toast.makeText(getActivity(), "통화내용 저장기능 Off", Toast.LENGTH_SHORT).show();
                } else {
//                    IncomingCallBroadcastReceiver.autoRun = true;
                    changeEnvironmentSet.setSaveLog(true);
                    Toast.makeText(getActivity(), "통화내용 저장기능 On", Toast.LENGTH_SHORT).show();
                }
                saveEnvironmentLog();
            }
        });
        final LinearLayout setbg = (LinearLayout) view.findViewById(R.id.setBackground);
        setbg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setbg.setBackgroundColor(Color.rgb(221, 221, 221));
                View setbgDialog = inflater.inflate(R.layout.setbackground_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("배경테마 선택");
                builder.setView(setbgDialog);

                final CheckBox cb1 = (CheckBox) setbgDialog.findViewById(R.id.cb1);
                final CheckBox cb2 = (CheckBox) setbgDialog.findViewById(R.id.cb2);
                final CheckBox cb3 = (CheckBox) setbgDialog.findViewById(R.id.cb3);
                final CheckBox cb4 = (CheckBox) setbgDialog.findViewById(R.id.cb4);
                final ImageView iv1 = (ImageView) setbgDialog.findViewById(R.id.iv1);
                final ImageView iv2 = (ImageView) setbgDialog.findViewById(R.id.iv2);
                final ImageView iv3 = (ImageView) setbgDialog.findViewById(R.id.iv3);
                final ImageView iv4 = (ImageView) setbgDialog.findViewById(R.id.iv4);
                iv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cb1.toggle();
                    }
                });
                iv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cb2.toggle();
                    }
                });
                iv3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cb3.toggle();
                    }
                });
                iv4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cb4.toggle();
                    }
                });
                final Boolean[] checkedOne = {true};
                Drawable setted = settedBackgroundImage.getDrawable();
                Drawable i1 = iv1.getDrawable();
                Drawable i2 = iv2.getDrawable();
                Drawable i3 = iv3.getDrawable();
                Drawable i4 = iv4.getDrawable();
                Bitmap settedb = ((BitmapDrawable) setted).getBitmap();
                Bitmap i1b = ((BitmapDrawable) i1).getBitmap();
                Bitmap i2b = ((BitmapDrawable) i2).getBitmap();
                Bitmap i3b = ((BitmapDrawable) i3).getBitmap();
                Bitmap i4b = ((BitmapDrawable) i4).getBitmap();
                if (settedb.equals(i1b))
                    cb1.setChecked(true);
                else if (settedb.equals(i2b))
                    cb2.setChecked(true);
                else if (settedb.equals(i3b))
                    cb3.setChecked(true);
                else if (settedb.equals(i4b))
                    cb4.setChecked(true);

                cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked == true) {
                            checkedOne[0] = false;
                            cb2.setChecked(false);
                            cb3.setChecked(false);
                            cb4.setChecked(false);
                            checkedOne[0] = true;
                        } else if (isChecked == false) {
                            if (checkedOne[0] == true) {
                                checkedOne[0] = true;
                                cb1.setChecked(true);
                            } else if (checkedOne[0] == false) {
                                cb1.setChecked(false);
                            }
                        }
                    }
                });
                cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked == true) {
                            checkedOne[0] = false;
                            cb1.setChecked(false);
                            cb3.setChecked(false);
                            cb4.setChecked(false);
                            checkedOne[0] = true;
                        } else if (isChecked == false) {
                            if (checkedOne[0] == true) {
                                checkedOne[0] = true;
                                cb2.setChecked(true);
                            } else if (checkedOne[0] == false) {
                                cb2.setChecked(false);
                            }
                        }
                    }
                });
                cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked == true) {
                            checkedOne[0] = false;
                            cb1.setChecked(false);
                            cb2.setChecked(false);
                            cb4.setChecked(false);
                            checkedOne[0] = true;
                        } else if (isChecked == false) {
                            if (checkedOne[0] == true) {
                                checkedOne[0] = true;
                                cb3.setChecked(true);
                            } else if (checkedOne[0] == false) {
                                cb3.setChecked(false);
                            }
                        }
                    }
                });
                cb4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked == true) {
                            checkedOne[0] = false;
                            cb1.setChecked(false);
                            cb2.setChecked(false);
                            cb3.setChecked(false);
                            checkedOne[0] = true;
                        } else if (isChecked == false) {
                            if (checkedOne[0] == true) {
                                checkedOne[0] = true;
                                cb4.setChecked(true);
                            } else if (checkedOne[0] == false) {
                                cb4.setChecked(false);
                            }
                        }
                    }
                });
                builder.setPositiveButton("선택완료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setbg.setBackgroundColor(Color.WHITE);
                        setbg.setBackgroundResource(R.drawable.border);
                        Toast.makeText(getContext(), "테마변경을 취소합니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkedOne[0] == false) {
                            Toast.makeText(getContext(), "하나의 테마를 선택해야 합니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            setbg.setBackgroundColor(Color.WHITE);
                            setbg.setBackgroundResource(R.drawable.border);
                            if (cb1.isChecked()) {
                                settedBackgroundImage.setImageDrawable(iv1.getDrawable());
                                changeEnvironmentSet.setBgNo(1);
                            } else if (cb2.isChecked()) {
                                settedBackgroundImage.setImageDrawable(iv2.getDrawable());
                                changeEnvironmentSet.setBgNo(2);
                            } else if (cb3.isChecked()) {
                                settedBackgroundImage.setImageDrawable(iv3.getDrawable());
                                changeEnvironmentSet.setBgNo(3);
                            } else if (cb4.isChecked()) {
                                settedBackgroundImage.setImageDrawable(iv4.getDrawable());
                                changeEnvironmentSet.setBgNo(4);

                            }
                            saveEnvironmentLog();
                            dialog.dismiss();
                        }
                    }
                });

            }
        });
        final LinearLayout license = (LinearLayout) view.findViewById(R.id.openSource);
        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                license.setBackgroundColor(Color.rgb(221,221,221));
                View licenseView = inflater.inflate(R.layout.license_dialog,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("오픈소스 라이센스");
                builder.setView(licenseView);

                builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        license.setBackgroundColor(Color.WHITE);
                        license.setBackgroundResource(R.drawable.border);
                    }
                });
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    public boolean onKey(DialogInterface dialog,
                                         int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
                            license.setBackgroundColor(Color.WHITE);
                            license.setBackgroundResource(R.drawable.border);
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
        final LinearLayout teamintro = (LinearLayout) view.findViewById(R.id.develop);
        teamintro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamintro.setBackgroundColor(Color.rgb(221,221,221));
                View licenseView = inflater.inflate(R.layout.teamintroduce_dialog,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("개발자 정보");
                builder.setView(licenseView);

                builder.setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        teamintro.setBackgroundColor(Color.WHITE);
                        teamintro.setBackgroundResource(R.drawable.border);
                    }
                });
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    public boolean onKey(DialogInterface dialog,
                                         int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
                            teamintro.setBackgroundColor(Color.WHITE);
                            teamintro.setBackgroundResource(R.drawable.border);
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
    public void saveEnvironmentLog() {
        final String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File dir = new File(sdPath+"/myvoice");
        if(!dir.exists())
            dir.mkdir();
        String filePath = dir.getAbsolutePath()+"/"+"Environment.dat";
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(filePath));

            oos.writeObject(changeEnvironmentSet);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("환경설정파일 저장");
    }
}
