package com.example.administrator.audiorecorder;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import javaFlacEncoder.FLAC_FileEncoder;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements MaterialTabListener {

    MaterialTabHost tabHost;
    ViewPager pager;
    ViewPagerAdapter adapter;
    private Resources res;
    static ArrayList<CallLog> callLogs = new ArrayList<CallLog>();
    static ArrayList<LinearLayout> talkLayouts  = new ArrayList<LinearLayout>();
    public static ArrayList<String> layoutsNames = new ArrayList<String>();
    static EnvironmentSet environmentSet = new EnvironmentSet(true,true,true,1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(Window.FEATURE_NO_TITLE, Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        res = this.getResources();//
        callLog();
        callEnvironmentLog();
        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        pager = (ViewPager) this.findViewById(R.id.pager);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabHost.setSelectedNavigationItem(position);
            }
        });


        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            // .setText(adapter.getPageTitle(i))
                            //.setTabListener(this)
                            .setIcon(getIcon(i))
                            .setTabListener(this)


            );
        }

    }

    public void callLog() {
        final String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File dir = new File(sdPath+"/myvoice");
        if(!dir.exists())
            dir.mkdir();
        String filePath = dir.getAbsolutePath()+"/"+"Log.dat";
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream( new FileInputStream(filePath));
            callLogs = (ArrayList<CallLog>)ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("로그파일이 없습니다.");
        }
    }
    public void callEnvironmentLog() {
        final String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File dir = new File(sdPath+"/myvoice");
        if(!dir.exists())
            dir.mkdir();
        String filePath = dir.getAbsolutePath()+"/"+"Environment.dat";
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream( new FileInputStream(filePath));
            environmentSet = (EnvironmentSet)ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("환경설정 파일이 없습니다.");
        }
    }

    @Override
    public void onTabSelected(MaterialTab tab){
        pager.setCurrentItem(tab.getPosition());

    }
    @Override
    public void onTabReselected(MaterialTab tab){

    }
    @Override
    public void onTabUnselected(MaterialTab tab){

    }
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        public Fragment getItem(int num) {

            Fragment newFragment = null;
            switch (num) {
                case 0:
                    newFragment = new SubActivityAutoOption();
                    break;
                case 1:
                    newFragment = new SubActivityCallList();
                    break;
                case 2:
                    newFragment = new SubActivityHelp();
                    break;

                default:
                    break;
            }

            return newFragment;
        }
        @Override
        public int getCount() {
            return 3;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0: return "tab 1";
                case 1: return "tab 2";
                case 2: return "tab 3";
                default: return null;
            }
        }
    }
    /*
    * It doesn't matter the color of the icons, but they must have solid colors
    */
    private Drawable getIcon(int position)
    {          switch(position) {
        case 0:
            return res.getDrawable(R.drawable.setting);
        case 1:
            return res.getDrawable(R.drawable.list);
        case 2:
            return res.getDrawable(R.drawable.archive);
    }
        return null;

    }
}
