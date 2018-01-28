package com.bignerdranch.android.myapplication.WAPfunctions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.myapplication.WAPfunctions.db.Plan;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.bignerdranch.android.myapplication.R;
import com.bignerdranch.android.myapplication.WAPfunctions.list.CommonUtil;
import com.bignerdranch.android.myapplication.WAPfunctions.list.MyAdapter;
import com.bignerdranch.android.myapplication.WAPfunctions.list.MyBean;
import com.bignerdranch.android.myapplication.WAPfunctions.list.OnItemClickListener;
import com.bignerdranch.android.myapplication.WAPfunctions.widget.CustomDatePicker;

public class PlanEdit extends AppCompatActivity implements OnItemClickListener {
    private TextView title;
    private Button back;
    private EditText bodyEdit;
    private Button storeBtn;
    private String planType;
    private Boolean addOrUpdate;
    private String inputString = "";
    private String inputTime = "";
    private String inputPlaceNumber="";
    private String currentPlace="选择";
    private String inputPlace="";
    private String RemindTime="选择";
    private RelativeLayout selectDate;
    private RelativeLayout selectPlace;
    private TextView selectPlaces;
    private TextView currentTime;
    private CustomDatePicker customDatePicker;
    private Dialog mDialog;
    private MyAdapter mAdapter;
    private List<MyBean> mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_edit);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue_light));
        }
        bodyEdit = (EditText) findViewById(R.id.plan_body);
        storeBtn = (Button) findViewById(R.id.store_button);
        back = (Button)findViewById(R.id.edit_back);
        title = (TextView)findViewById(R.id.edit_title);
        selectDate = (RelativeLayout) findViewById(R.id.select_date);
        selectPlace = (RelativeLayout) findViewById(R.id.select_place);
        currentTime = (TextView) findViewById(R.id.plan_date);
        selectPlaces = (TextView) findViewById(R.id.plan_place);
//        selectPlaces.setText("选择");
        initDatePicker();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        storeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               storeBtnClicked();
            }
        });
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentTime.getText().toString().equals("选择")){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                String now = sdf.format(new Date());
                customDatePicker.show(now);}
                else{
                    customDatePicker.show(currentTime.getText().toString());
                }
            }
        });
        selectPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> permissionList = new ArrayList<>();
                if (ContextCompat.checkSelfPermission(PlanEdit.this, android.Manifest.permission.ACCESS_FINE_LOCATION)!=
                        PackageManager.PERMISSION_GRANTED){
                    permissionList.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
                }
                if (ContextCompat.checkSelfPermission(PlanEdit.this, android.Manifest.permission.READ_PHONE_STATE)!=
                        PackageManager.PERMISSION_GRANTED){
                    permissionList.add(android.Manifest.permission.READ_PHONE_STATE);
                }
                if (ContextCompat.checkSelfPermission(PlanEdit.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED){
                    permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if(!permissionList.isEmpty()){
                    String [] permissions = permissionList.toArray(new String[permissionList.size()]);
                    ActivityCompat.requestPermissions(PlanEdit.this, permissions, 1);
                }else {
                    showDialog();
                }
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 ){
                    for (int result :grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this, "必须同意所有权限才能使用地点提醒", Toast.LENGTH_SHORT).show();
//                            finish();
                            return;
                        }
                    }
                }else{
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void showDialog() {
        mDialog = new Dialog(this, R.style.position_dialog_theme);
        View view = LayoutInflater.from(this).inflate(R.layout.planedit_dialog_list, null);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new MyAdapter(PlanEdit.this,PlanEdit.this);
        mRecyclerView.setAdapter(mAdapter);
        mData = new ArrayList<>();
        mData.add(new MyBean("快递街"));
        mData.add(new MyBean("商业街"));
        mData.add(new MyBean("青春广场"));
        mData.add(new MyBean("艺术学院"));
        mData.add(new MyBean("邮政"));
        mData.add(new MyBean("一基楼"));
        mData.add(new MyBean("综合楼"));
        mData.add(new MyBean("图书馆"));
        mData.add(new MyBean("体育馆"));
        mData.add(new MyBean("水上报告厅"));
        mData.add(new MyBean("一教"));
        mData.add(new MyBean("文化与新闻学院"));
        mData.add(new MyBean("历史文化学院"));
        mData.add(new MyBean("法学院"));
        mData.add(new MyBean("二基楼计算机学院"));
        mData.add(new MyBean("体育学院"));
        mData.add(new MyBean("建筑与环境学院"));
        mData.add(new MyBean("我"));


        mAdapter.refreshDatas(mData);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
        mDialog.setContentView(view);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.width = CommonUtil.getScreenWidth(this);
        //lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.height = CommonUtil.getScreenHeight(this) *2/5 ;
        lp.gravity = Gravity.BOTTOM;
        mDialog.getWindow().setAttributes(lp);
    }
    @Override
    public void onItemClick(int position) {
//        ToastUtil.showToast(this, "点击了 " + mData.get(position).getName() + ", 位置 = " + position);
        switch (position){
            case 0:
                selectPlaces.setText("快递街");
                inputPlaceNumber = "30.552543 103.998376";
                currentPlace = "快递街";
                break;
            case 1:
                selectPlaces.setText("商业街");
                inputPlaceNumber = "30.553351 103.993103";
                currentPlace = "商业街";
                break;
            case 2:
                selectPlaces.setText("青春广场");
                inputPlaceNumber = "30.554661 103.994934";
                currentPlace = "青春广场";
                break;
            case 3:
                selectPlaces.setText("艺术学院");
                inputPlaceNumber = "30.556409 103.995262";
                currentPlace = "艺术学院";
                break;
            case 4:
                selectPlaces.setText("邮政");
                inputPlaceNumber = "30.553940 103.994479";
                currentPlace = "邮政";
                break;
            case 5:
                selectPlaces.setText("一基楼");
                inputPlaceNumber = "30.555721 104.004449";
                currentPlace = "一基楼";
                break;
            case 6:
                selectPlaces.setText("综合楼");
                currentPlace = "综合楼";
                inputPlaceNumber = "30.554679 104.002814";
                break;
            case 7:
                selectPlaces.setText("图书馆");
                inputPlaceNumber = "30.556713 104.000180";
                currentPlace = "图书馆";
                break;
            case 8:
                selectPlaces.setText("体育馆");
                inputPlaceNumber = "30.558326 104.005622";
                currentPlace = "体育馆";
                break;
            case 9:
                selectPlaces.setText("水上报告厅");
                inputPlaceNumber = "30.554993 103.999796";
                currentPlace = "水上报告厅";
                break;
            case 10:
                selectPlaces.setText("一教");
                inputPlaceNumber = "30.554713 104.000158";
                currentPlace = "一教";
                break;
            case 11:
                selectPlaces.setText("文化与新闻学院");
                inputPlaceNumber = "30.560595 104.005642";
                currentPlace = "文化与新闻学院";
                break;
            case 12:
                selectPlaces.setText("历史文化学院");
                inputPlaceNumber = "30.560069 104.004913";
                currentPlace = "历史文化学院";
                break;
            case 13:
                selectPlaces.setText("法学院");
                inputPlaceNumber = "30.559312 104.003879";
                currentPlace = "法学院";
                break;
            case 14:
                selectPlaces.setText("二基楼计算机学院");
                inputPlaceNumber = "30.558454 104.002408";
                currentPlace = "二基楼计算机学院";
                break;
            case 15:
                selectPlaces.setText("体育学院");
                inputPlaceNumber = "30.553931 103.996430";
                currentPlace = "体育学院";
                break;
            case 16:
                selectPlaces.setText("建筑与环境学院");
                inputPlaceNumber = "30.559004 103.999967";
                currentPlace = "建筑与环境学院";
                break;
            case 17:
                selectPlaces.setText("我");
                inputPlaceNumber = "30.552459 103.992154";
                currentPlace = "我";
                break;
        }
        mDialog.dismiss();
    }
    private void storeBtnClicked() {
        boolean result = true;
        String body = bodyEdit.getText().toString();
        if(!body.equals("")) {
            List<Plan> plans = DataSupport.findAll(Plan.class);
            for (Plan item : plans) {
                if(item.getBody().equals(body)&&item.getType().equals(planType)&&
                        item.getRemindTime().equals(RemindTime)&&
                        item.getRemindPlace().equals(currentPlace)){
                    result =false;
                }
            }
            if(result){
                if(addOrUpdate){
                Plan plan = new Plan();
                plan.setBody(body);
                plan.setType(planType);
                plan.setRemind("关");
                plan.setRemindTime(RemindTime);
                    plan.setRemindPlace(currentPlace);
                    plan.setRemindPlaceNumber(inputPlaceNumber);
//                    plan.setPlaceReminded("未提醒");
                plan.save();
                }else{
//                    plan.updateAll("body","11");
//                    plan.save();
                    Plan a = new Plan();
                    a.setBody(body);
                    a.setRemindTime(RemindTime);
                    a.setRemindPlace(currentPlace);
                    a.setRemindPlaceNumber(inputPlaceNumber);
//                    a.setBody("1");
                    a.updateAll("body = ? and type = ?", inputString, planType);
                }
                List<Plan> saf = DataSupport.findAll(Plan.class);
                finish();
            }
            else{
                Toast.makeText(PlanEdit.this, "该计划存在哦", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(PlanEdit.this, "您还没写计划呢(^o^)", Toast.LENGTH_SHORT).show();
        }
    }

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());

        Intent intent = getIntent();
        addOrUpdate=true;
        if(intent.hasExtra("id")) {
            int id;
            id= intent.getIntExtra("id",100);
            Plan plan = DataSupport.find(Plan.class, id);
            inputString =plan.getBody();
            inputTime = plan.getRemindTime();
            inputPlaceNumber = plan.getRemindPlaceNumber();
            inputPlace = plan.getRemindPlace();
            addOrUpdate=false;
            RemindTime = inputTime;
            currentPlace=inputPlace;
            bodyEdit.setText(inputString);
            bodyEdit.setSelection(inputString.length());
        }
        else{
//            selectPlaces.setText("选择");
//            RemindTime = now;
        }
        planType = intent.getStringExtra("type");
        title.setText("添加"+ planType);
        currentTime.setText(RemindTime);
        selectPlaces.setText(currentPlace);
        if(planType.equals("日计划")) {

            customDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
                @Override
                public void handle(String time) { // 回调接口，获得选中的时间
                    RemindTime = time;
                    currentTime.setText(RemindTime);
                }
            }, now,"2021-12-31 23:59"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
            customDatePicker.showSpecificTime(true); // 显示时和分
            customDatePicker.setIsLoop(true); // 允许循环滚动
        }else {
            RemindTime =RemindTime.split(" ")[0];
            customDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
                @Override
                public void handle(String time) { // 回调接口，获得选中的时间
                    RemindTime = time.split(" ")[0];
                    currentTime.setText(RemindTime);
                }
            }, now,"2021-12-31 23:59"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
            customDatePicker.showSpecificTime(false); // 不显示时和分
            customDatePicker.setIsLoop(false); // 不允许循环滚动
        }
    }
    @Override
    public void onBackPressed() {
        if((inputTime.equals(currentTime.getText().toString())&&
                inputString.equals(bodyEdit.getText().toString())&&
                  inputPlace.equals(selectPlaces.getText()))||inputString.equals("")) {
            finish();
        }
        else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(PlanEdit.this);
            dialog.setMessage("您还未保存您的计划");
            dialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    storeBtnClicked();
                    finish();
                }
            });
            dialog.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.show();
        }
    }
}
