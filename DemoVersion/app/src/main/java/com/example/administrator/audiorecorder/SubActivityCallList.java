package com.example.administrator.audiorecorder;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016-05-10.
 */
public class SubActivityCallList extends Fragment {
    ListView callList;
    String[] Names;
    Drawable[] Photos;
    String[] Times;
    ArrayList<CallLog> callLogs = new ArrayList<CallLog>();
    ListViewAdapter myAdapter;
    Button delete;
    int logSize;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sub_calllist, container, false);
        callLog();
        callList = (ListView)view.findViewById(R.id.callList);
        delete = (Button)view.findViewById(R.id.delete);
        myAdapter = new ListViewAdapter();
        if(!callLogs.isEmpty()) {
            logSize = callLogs.size();
            Names = new String[logSize];
            Photos = new Drawable[logSize];
            Times = new String[logSize];
            for (int i = 0; i < logSize; i++) {
                Names[i] = callLogs.get(i).getName();
                Photos[i] =  new BitmapDrawable(getResources(), getPhoneBookPhoto(callLogs.get(i).getPhoneNumber()));
                Times[i] = callLogs.get(i).getTime();
            }
            myAdapter.addItem(Photos, Names, Times);
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,Names);
            callList.setAdapter(myAdapter);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext()).setTitle("통화기록 초기화").setMessage("기록을 전부 삭제하시겠습니까?").setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        callList.setAdapter(new ListViewAdapter());
                        myAdapter.notifyDataSetChanged();
                        deleteLogFile();
                    }
                })
                        .setNegativeButton("아니오", null).show();
                return;
            }
        });
        callList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), CallLogDialog.class);
                System.out.println("포지션 번호" + position);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        }
        return view;
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
    public void deleteLogFile() {
        final String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File dir = new File(sdPath+"/myvoice");
        if(!dir.exists())
            dir.mkdir();
        String filePath = dir.getAbsolutePath()+"/"+"Log.dat";
        File log = new File(filePath);
        log.delete();
        MainActivity.callLogs.clear();
    }
    public Bitmap getPhoneBookPhoto(String phoneNumber){
        Uri phoneUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Uri photoUri = null;
        ContentResolver cr = getContext().getContentResolver();
        Cursor contact = cr.query(phoneUri,
                new String[]{ContactsContract.Contacts._ID}, null, null, null);
        if (contact.moveToFirst()) {
            long userId = contact.getLong(contact.getColumnIndex(ContactsContract.Contacts._ID));
            photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, userId);
        }
        else {
            Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.unknown);
            return defaultPhoto;
        }
        if (photoUri != null) {
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(
                    cr, photoUri);
            if (input != null) {
                return BitmapFactory.decodeStream(input);
            }
        } else {
            Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.unknown);
            return defaultPhoto;
        }
        Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.unknown);
        return defaultPhoto;
    }
}
