package kr.popcorn.sharoom.activity.View.Host;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import kr.popcorn.sharoom.R;
import kr.popcorn.sharoom.activity.TabView.Activity_server_roading;
import kr.popcorn.sharoom.helper.GlobalApplication;
import kr.popcorn.sharoom.helper.Helper_room;
import kr.popcorn.sharoom.helper.Helper_roomData;
import kr.popcorn.sharoom.helper.Helper_server;
import kr.popcorn.sharoom.helper.Helper_userData;
import me.yokeyword.imagepicker.ImagePicker;
import me.yokeyword.imagepicker.callback.CallbackForCamera;
import me.yokeyword.imagepicker.callback.CallbackForImagePicker;

/**
 * Created by user on 16. 3. 12.
 */


//방을 수정하기 위한 액티비티
public class Activity_host_editRoom extends Activity  implements View.OnClickListener{
    public final int MAX_SIZE=7;
    public final int PICK_THE_ALBUM=1;

    private ArrayList<String> list;
    private ImagePicker mImagePicker;

    private Dialog dialog;

    private ImageButton picButton;
    private ImageButton dialogCam;
    private ImageButton dialogGal;

    private EditText et_title;
    private EditText et_address;
    private EditText et_price;
    private EditText et_roomInfo;
    private EditText et_facilities;
    private CheckBox roomtype1, roomtype2, roomtype3, roomtype4;
    private String mRoomKind[] = { "원룸", "하숙", "자취", "고시원" };
    private String _roomKind;
    private double lat, lng;

    public TextView tv_register, tv_delete;
    private int mYear, mMonth, mDay;
    private TextView startDate, endDate;
    private String start, end;
    private String today;
    private TextView toptext;


    private int idx;
    int position;
    int imgLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editroom);


        //사진 클릭시 카메라와 갤러리에서 고를수있게 지원해주는 다이얼로그
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_editroom_dialog);

        picButton = (ImageButton) findViewById(R.id.picture);

        toptext = (TextView) findViewById(R.id.toptext);

        //다이얼로그의 카메라와 갤러리 버튼
        dialogCam = (ImageButton) dialog.findViewById(R.id.camera);
        dialogGal = (ImageButton) dialog.findViewById(R.id.gallery);


        picButton.setOnClickListener(this);
        dialogCam.setOnClickListener(this);
        dialogGal.setOnClickListener(this);

        toptext.setText("방 수정하기");

        idx = getIntent().getIntExtra("roomNumber",0);  //방리스트 인덱스
        position=1; //현재 사진의 인덱스
        Helper_roomData roomData = Helper_room.getInstance().list.get(idx);
        imgLength = Helper_room.getInstance().list.get(idx).image.size();


        mImagePicker = new ImagePicker(this);
        loadData();
        et_title = (EditText)findViewById(R.id.et_title);
        et_address = (EditText)findViewById(R.id.et_address);
        et_price = (EditText)findViewById(R.id.et_price);
        //et_roomKind = (EditText)findViewById(R.id.et_roomKind);
        et_roomInfo = (EditText) findViewById(R.id.et_roominfo);
        et_facilities = (EditText)findViewById(R.id.et_facilities);

        roomtype1 = (CheckBox) findViewById(R.id.ck_roomtype1);
        roomtype2 = (CheckBox) findViewById(R.id.ck_roomtype2);
        roomtype3 = (CheckBox) findViewById(R.id.ck_roomtype3);
        roomtype4 = (CheckBox) findViewById(R.id.ck_roomtype4);

        et_title.setText(roomData.getTitle());
        et_address.setText(roomData.getAddress());
        et_price.setText(roomData.getPrice());
        et_roomInfo.setText(roomData.getRoomInfo());
        et_facilities.setText(roomData.fac);

        et_title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                              @Override
                                              public void onFocusChange(View v, boolean hasFocus) {
                                                  if (hasFocus) {
                                                      et_title.setHint("");
                                                      InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                                                      imm.showSoftInput(et_title, InputMethodManager.SHOW_IMPLICIT);
                                                  } else{
                                                      et_title.setHint("주말 빌려드립니다! 연락주세요!");
                                                  }
                                              }
                                          }
        );

        et_address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_address.setHint("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_address, InputMethodManager.SHOW_IMPLICIT);

                } else{
                    et_address.setHint("서울시 성북구 정릉동 11-12");
                }
            }
        });


        et_price.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_price.setHint("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_price, InputMethodManager.SHOW_IMPLICIT);

                } else{
                    et_price.setHint("300,000");
                }
            }
        });
        /*et_roomKind.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_roomKind.setHint("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_roomKind, InputMethodManager.SHOW_IMPLICIT);

                } else{
                    et_roomKind.setHint("원룸, 자취방, 하숙집...etc");
                }
            }
        });
        */

        et_facilities.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_facilities.setHint("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_facilities, InputMethodManager.SHOW_IMPLICIT);

                } else{
                    et_address.setHint("침대 1인용, 컴퓨터 책상, 옷걸이...etc");
                }
            }
        });
        et_roomInfo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_roomInfo.setHint("");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_roomInfo, InputMethodManager.SHOW_IMPLICIT);

                } else{
                    et_roomInfo.setHint("국민대학교 정문에서 걸어서 10분정도 걸리는 거리에 위치한 원룸입니다. 연락주세요.");
                }
            }
        });

        et_title.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_title.getWindowToken(), 0);//hide keyboard
                    return true;
                }
                return false;
            }
        });
        et_address.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_address.getWindowToken(), 0);    //hide keyboard
                    return true;
                }
                return false;
            }
        });
        et_price.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_price.getWindowToken(), 0);    //hide keyboard
                    return true;
                }
                return false;
            }
        });

        /*et_roomKind.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_roomKind.getWindowToken(), 0);    //hide keyboard
                    return true;
                }
                return false;
            }
        });*/
        et_facilities.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_facilities.getWindowToken(), 0);    //hide keyboard
                    return true;
                }
                return false;
            }
        });
        et_roomInfo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_roomInfo.getWindowToken(), 0);    //hide keyboard
                    return true;
                }
                return false;
            }
        });
        et_facilities.setHorizontallyScrolling(false);
        et_roomInfo.setHorizontallyScrolling(false);
        startDate = (TextView) findViewById(R.id.startDate);
        endDate = (TextView) findViewById(R.id.endDate);

        Calendar cal = new GregorianCalendar();
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);
        //날짜 초기값 현재날짜로 세팅
        today = String.format("%d/%d/%d", mYear, mMonth+1, mDay);
        startDate.setText(String.format("%d/%d/%d", mYear, mMonth+1, mDay));
        endDate.setText(String.format("%d/%d/%d", mYear, mMonth + 1, mDay));

        //달력 입력을 받기 위한 다이얼로그
        startDate.setOnClickListener(new TextView.OnClickListener() { //시작날짜
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.startDate:
                        new DatePickerDialog(Activity_host_editRoom.this, mDateSetListener1, mYear, mMonth, mDay).show();
                        break;

                }
            }
        });

        endDate.setOnClickListener(new TextView.OnClickListener() { //끝 날짜
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.endDate:
                        new DatePickerDialog(Activity_host_editRoom.this, mDateSetListener2, mYear, mMonth, mDay).show();
                        break;

                }
            }
        });

        String location = et_address.getText().toString();
        GlobalApplication myApp = (GlobalApplication) getApplication();
        myApp.setGlobalString(location);

        Geocoder geocoder = new Geocoder(this);
        Address addr;

        try {
            List<Address> listAddress = geocoder.getFromLocationName(location, 1);
            if (listAddress.size() > 0) { // 주소값이 존재 하면
                addr = listAddress.get(0); // Address형태로
                //lat = (int) (addr.getLatitude() * 1E6);
                //lng = (int) (addr.getLongitude() * 1E6);
                lat = addr.getLatitude();
                lng = addr.getLongitude();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        tv_delete = (TextView) findViewById(R.id.tv_delete);
        tv_delete.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

        tv_register = (TextView) findViewById(R.id.bottomtext);
        tv_register.setText("수정 하기");
        tv_register.setOnClickListener(new TextView.OnClickListener(){
            public void onClick(View v) {

                startActivity(new Intent(getApplication(), Activity_server_roading.class)); // 서버 정보 받을 동안 보여줄 activity

                Log.d("buttonClick", "okokokokok");
                for(int i=0; i<list.size(); i++){
                    Log.d("buttonList", list.get(i));
                }


                if(roomtype1.isChecked())
                {
                    _roomKind = mRoomKind[0];
                }
                else if(roomtype2.isChecked())
                {
                    _roomKind = mRoomKind[1];
                }
                else if(roomtype3.isChecked())
                {
                    _roomKind = mRoomKind[2];
                }
                else if(roomtype4.isChecked())
                {
                    _roomKind = mRoomKind[3];
                }
                else{
                    Toast.makeText(Activity_host_editRoom.this, "방유형을 선택해주세요.", Toast.LENGTH_LONG).show();
                }
                final String title = et_title.getText().toString();
                final String address = et_address.getText().toString();
                final String price = et_price.getText().toString();
                final String roomKind = _roomKind;
                Log.d("roomKindnString", roomKind);

                final String roomInfo = et_roomInfo.getText().toString();
                final String fac = et_facilities.getText().toString();
                String sDate = startDate.getText().toString();
                String eDate = endDate.getText().toString();
                final double mLat = lat;
                final double mLng = lng;

                System.out.println(sDate);

                System.out.println(eDate);
                // Log.i("jihyun1", end);)

                if(sDate == null) {
                    startDate.setText(today);
                    Toast.makeText(Activity_host_editRoom.this, "시작 날짜를 입력해주세요.", Toast.LENGTH_LONG).show();
                }
                else if(eDate == null)
                {
                    endDate.setText(today);
                    Toast.makeText(Activity_host_editRoom.this, "종료 날짜를 입력해주세요.", Toast.LENGTH_LONG).show();
                }
                else if(sDate.compareTo(eDate)>0){
                    Toast.makeText(Activity_host_editRoom.this, "입력 날짜를 확인해주세요.", Toast.LENGTH_LONG).show();
                }
                else{
                    postImage(list, title, address, price, roomKind, fac, roomInfo, sDate, eDate, mLat, mLng);

                    SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = mPref.edit();
                    editor.clear();
                    editor.commit();
                }
            }
        });

    }

    DatePickerDialog.OnDateSetListener mDateSetListener1 =
            new DatePickerDialog.OnDateSetListener(){

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;

                    startDate.setText(String.format("%d/%d/%d", mYear, mMonth+1, mDay));
                    start = new String(String.valueOf(mYear)+"/"+String.valueOf(mMonth+1)+"/"+String.valueOf(mDay));
                }
            };

    DatePickerDialog.OnDateSetListener mDateSetListener2 =
            new DatePickerDialog.OnDateSetListener(){

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;

                    endDate.setText(String.format("%d/%d/%d", mYear, mMonth+1, mDay));
                    end = new String(String.valueOf(mYear)+"/"+String.valueOf(mMonth+1)+"/"+String.valueOf(mDay));

                }
            };

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            //방 사진을 눌렀을때 처리
            case R.id.picture:
                //방에 사진이 하나도 없을경우 다이얼로그를 띄워서 카메라와 갤러리를 고를수있게해줌
                if(list.size() == 0 ) dialog.show();
                else{   //사진이 하나이상 있을경우 사진편집 액티비티를 띄워서 그곳에서 방사진을 편집하게끔한다.
                    openActivity();
                }
                break;
            case R.id.camera:
                dialog.dismiss();
                mImagePicker.openCamera(new CallbackForCamera() {
                    @Override
                    public void onError(Exception error) {

                    }
                    @Override
                    public void onComplete(String imagePath) {
                        list.add(imagePath);
                        openActivity();
                    }

                    @Override
                    public void onCancel(String imagePath) {
                        Toast.makeText(getApplicationContext(), "실패..", Toast.LENGTH_SHORT).show();

                        File tempFile = new File(imagePath);
                        if (tempFile.exists()) {
                            tempFile.delete();
                        }
                    }
                });
                break;
            case R.id.gallery:
                dialog.dismiss();
                mImagePicker.openImagePiker(true, new CallbackForImagePicker() {
                    @Override
                    public void onError(Exception error) {
                        Log.i("aab", "error");
                    }
                    @Override
                    public void onComplete(List<String> imagePath) {
                        list.addAll(imagePath);
                        for(int i=MAX_SIZE; i<list.size(); i++){
                            list.remove(i);
                        }
                        openActivity();
                    }
                });
                break;
        }
    }
    public void openActivity(){
        Intent it = new Intent(this, Activity_host_registerRoom_roomPic.class);
        it.putExtra("list", list);
        startActivityForResult(it, PICK_THE_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode){
            case PICK_THE_ALBUM:
                list = data.getStringArrayListExtra("list");
                saveData();
                break;
        }

        mImagePicker.delegateActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void onResume(){
        super.onResume();
        loadData();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
    }

    private void saveData(){
        // 특정번호의 공유저장소를 편집가능 상태로 불러온다.
        SharedPreferences.Editor edt = getSharedPreferences("room",0).edit();

        // 저장
        edt.putInt("picCount", list.size());
        for(int i=0; i<list.size(); i++){
            edt.putString("pic" + i, list.get(i));
        }

        // 수행
        edt.commit();
    }

    private void loadData(){
        // 저장소 객체를 생성
        SharedPreferences prefs = getSharedPreferences("room", 0);

        // 로드
        int size = prefs.getInt("picCount",0);
        ArrayList<String> arrayList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            arrayList.add(prefs.getString("pic" + i, null));
        }

        list = arrayList;

        if( list.size() > 0 ) {
            try {
                Bitmap bitmap = decodeUri(getApplicationContext(), Uri.fromFile(new File(list.get(0))), 100);
                picButton.setImageBitmap(bitmap);
            }catch (FileNotFoundException e){
                Log.e("ddddd","fuck");
            }
        }else{
            picButton.setImageResource(R.drawable.roompicture);
        }
    }

    public void postImage(ArrayList<String> list, String title, String address, String price,  String roomKind, String fac, String roomInfo, String sDate, String eDate, double mLat, double mLng){

        //아이디 가져옴.
        int userID = Helper_userData.getInstance().getUserID();
        String storage = getFilesDir().toString();
        RequestParams params = new RequestParams();
        params.put("roomNumber",  Helper_room.getInstance().list.get(idx).roomNumber);
        System.out.println("aaaaaa" + Helper_room.getInstance().list.get(idx).roomNumber);
        params.put("userID",userID);
        params.put("size", list.size()); //이미지 크기.

        for (int i = 0; i < list.size(); i++) {
            System.out.println("sibalbalblabl_imageLink : " + list.get(i));

            String imagePath = storage + i+".jpg";

            try{
                params.put("file" + i, SaveBitmapToFileCache(decodeUri(getApplicationContext(), Uri.fromFile(new File(list.get(i))), 100), imagePath));
                //params.put("path", "aaa");
            }
            catch(FileNotFoundException e){
                System.out.println("sibalbal fileNotFound");
            }
        }
        params.put("title", title);
        params.put("address", address);
        params.put("price", price);
        params.put("roomKind", roomKind);
        params.put("fac", fac);
        params.put("roomInfo", roomInfo);
        params.put("sDate", sDate);
        params.put("eDate", eDate);
        params.put("lat", mLat);
        params.put("lng", mLat);

        Helper_server.post("data/edit_roomdata.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.println("statusCode "+statusCode);//statusCode 200
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("sibalbalblabl_onFailure");
            }
        });
    }


    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth
                , height_tmp = o.outHeight;
        int scale = 1;

        while(true) {
            if(width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }

    private File SaveBitmapToFileCache(Bitmap bitmap, String strFilePath) {

        File fileCacheItem = new File(strFilePath);
        OutputStream out = null;

        try
        {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                out.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return fileCacheItem;
    }
}

