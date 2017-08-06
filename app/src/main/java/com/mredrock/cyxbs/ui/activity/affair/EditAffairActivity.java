package com.mredrock.cyxbs.ui.activity.affair;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mredrock.cyxbs.R;
import com.mredrock.cyxbs.component.widget.Position;
import com.mredrock.cyxbs.event.TimeChooseEvent;
import com.mredrock.cyxbs.model.Affair;
import com.mredrock.cyxbs.model.AffairApi;
import com.mredrock.cyxbs.ui.activity.BaseActivity;
import com.mredrock.cyxbs.ui.widget.PickerBottomSheetDialog;
import com.mredrock.cyxbs.util.DensityUtils;
import com.mredrock.cyxbs.util.KeyboardUtils;
import com.mredrock.cyxbs.util.database.DBManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class EditAffairActivity extends BaseActivity {

    private static final String TAG = "EditAffairActivity";

    public static final String BUNDLE_KEY = "position";
    public static final String WEEK_NUMBER = "week";
    private static final String COURSE_KEY = "course";
    private final String[] TIMES = new String[]{"不提醒", "提前5分钟", "提前10分钟", "提前20分钟", "提前30分钟", "提前一个小时"};
    private final int[] TIME_MINUTE = new int[]{0, 5, 10, 20, 30, 60};

    private final String[] WEEKS = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private final String[] CLASSES = {"一二节", "三四节", "五六节", "七八节", "九十节", "AB节"};
    private boolean isStartByCourse = false;
    private String uid;

    @Bind(R.id.remind_text)
    TextView mRemindText;
    @Bind(R.id.week_text)
    TextView mWeekText;
    @Bind(R.id.time_text)
    TextView mTimeText;
    @Bind(R.id.content)
    EditText mContentEdit;
    @Bind(R.id.title)
    EditText mTitleEdit;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.toolbar_title)
    TextView mToolbarTitle;

    private List<Integer> weeks = new ArrayList<>();
    private WeekAdapter mWeekAdapter;
    private ArrayList<Position> mPositions = new ArrayList<>();
    private int time = 0;
    private BottomSheetDialog mPickWeekDialog;
    private PickerBottomSheetDialog mPickRemindDialog;
    private BottomSheetDialog mPickTimeDialog;

    @OnClick(R.id.choose_remind)
    public void showChooseRemindDialog(View v) {
        KeyboardUtils.hideInput(v);
        if (mPickRemindDialog == null) {
            initPickRemindDialog();
        }
        mPickRemindDialog.show();
    }

    @OnClick(R.id.choose_week)
    public void showChooseWeekDialog(View v) {
        KeyboardUtils.hideInput(v);
        if (mPickWeekDialog == null) {
            initPickWeekDialog();
        }
        mPickWeekDialog.show();
    }

    @OnClick(R.id.choose_time)
    public void showChooseTimeDialog(View v) {
        // TODO: 2017/8/5 选择时间
        KeyboardUtils.hideInput(v);
        if (mPickTimeDialog == null) {
            initPickTimeDialog();
        }
        mPickTimeDialog.show();
/*
        Intent i = new Intent(this, TimeChooseActivity.class);
        i.putExtra(TimeChooseActivity.BUNDLE_KEY, mPositions);
        startActivity(i);*/
    }

    /*
        @SuppressWarnings("unchecked")
        public void submit(View v) {
            KeyboardUtils.hideInput(v);
            if (v.getId() == R.id.edit_affair_iv_save) {
                String title = mTitleEdit.getText().toString();
                String content = mContentEdit.getText().toString();
                if (title.trim().isEmpty()) {
                    Toast.makeText(APP.getContext(), "标题不能为空哦", Toast.LENGTH_SHORT).show();
                } else if (weeks.size() == 0 || mPositions.size() == 0) {
                    Toast.makeText(APP.getContext(), "时间或周数不能为空哦", Toast.LENGTH_SHORT).show();
                } else {
                    DBManager dbManager = DBManager.INSTANCE;
                    Affair affair = new Affair();
                    AffairApi.AffairItem affairItem = new AffairApi.AffairItem();
                    Gson gson = new Gson();
                    Random ne = new Random();
                    String x;
                    if (uid == null)
                        x = System.currentTimeMillis() + "" + (ne.nextInt(9999 - 1000 + 1) + 1000);//为变量赋随机值10009999
                    else {
                        x = uid;
                    }
                    affairItem.setContent(content);
                    affairItem.setTime(time);
                    affairItem.setId(x);
                    affairItem.setTitle(title);


                    for (Position p : mPositions) {
                        AffairApi.AffairItem.DateBean date = new AffairApi.AffairItem.DateBean();
                        date.setClassX(p.getY());
                        date.setDay(p.getX());
                        date.getWeek().addAll(mWeekAdapter.getWeeks());
                        affairItem.getDate().add(date);
                    }
                    affair.week = affairItem.getDate().get(0).getWeek();
                    if (!isStartByCourse) {
                        RequestManager.getInstance().addAffair(new SimpleSubscriber<Object>(this, true, false, new SubscriberListener<Object>() {
                            @Override
                            public void onCompleted() {
                                super.onCompleted();
                                dbManager.insert(true, x, APP.getUser(EditAffairActivity.this).stuNum, gson.toJson(affairItem))
                                        .subscribeOn(Schedulers.io())
                                        .unsubscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Subscriber() {
                                            @Override
                                            public void onCompleted() {
                                                EventBus.getDefault().post(new AffairAddEvent(affair));
                                                onBackPressed();
                                            }

                                            @Override
                                            public void onError(Throwable e) {

                                            }

                                            @Override
                                            public void onNext(Object o) {

                                            }
                                        });
                            }

                            @Override
                            public boolean onError(Throwable e) {
                                if (e instanceof SocketTimeoutException)
                                    Toast.makeText(EditAffairActivity.this, "连接超时，检查一下网络哦", Toast.LENGTH_SHORT).show();
                                else if (e instanceof RedrockApiException)
                                    Toast.makeText(EditAffairActivity.this, "服务器出了点小毛病，请稍后再试", Toast.LENGTH_SHORT).show();
                                else if (e instanceof MalformedJsonException) {

                                }
                                return true;

                            }

                            @Override
                            public void onNext(Object object) {
                                super.onNext(object);
                                // LOGE("EditAffairActivity",redrockApiWrapper.id);
                            }

                            @Override
                            public void onStart() {
                                super.onStart();
                            }
                        }), APP.getUser(this).stuNum, APP.getUser(this).idNum, x, title, content, gson.toJson(affairItem.getDate()), affairItem.getTime());
                    } else {
                        //  Log.e(TAG, "onSaveClick: isStartByCourse");
                        RequestManager.getInstance().editAffair(new SimpleSubscriber<Object>(this, true, false, new SubscriberListener<Object>() {
                            @Override
                            public void onCompleted() {
                                super.onCompleted();
                                dbManager.insert(true, x, APP.getUser(EditAffairActivity.this).stuNum, gson.toJson(affairItem), true)
                                        .subscribeOn(Schedulers.io())
                                        .unsubscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Subscriber() {
                                            @Override
                                            public void onCompleted() {
                                                EventBus.getDefault().post(new AffairModifyEvent());
                                                onBackPressed();
                                            }

                                            @Override
                                            public void onError(Throwable e) {

                                            }

                                            @Override
                                            public void onNext(Object o) {

                                            }
                                        });
                            }

                            @Override
                            public boolean onError(Throwable e) {
                                if (e instanceof SocketTimeoutException)
                                    Toast.makeText(EditAffairActivity.this, "连接超时，检查一下网络哦", Toast.LENGTH_SHORT).show();
                                else if (e instanceof RedrockApiException)
                                    Toast.makeText(EditAffairActivity.this, "服务器出了点小毛病，请稍后再试", Toast.LENGTH_SHORT).show();
                                else if (e instanceof MalformedJsonException) {
                                    Toast.makeText(EditAffairActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "onError: " + e.getMessage());
                                }
                                return true;
                            }

                            @Override
                            public void onNext(Object object) {
                                super.onNext(object);
                                // LOGE("EditAffairActivity",redrockApiWrapper.id);
                            }

                            @Override
                            public void onStart() {
                                super.onStart();
                            }
                        }), APP.getUser(this).stuNum, APP.getUser(this).idNum, x, title, content, gson.toJson(affairItem.getDate()), affairItem.getTime());
                    }
                }
            } else {
                onBackPressed();
            }
        }
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_affair);
        ButterKnife.bind(this);
        initView();

        /*if (!initData())
            initCourse();*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_affair, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.submit) {
            // TODO: 2017/8/5 提交
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initCourse() {
        Affair course = getIntent().getParcelableExtra(COURSE_KEY);
        if (course == null)
            return;
        uid = course.uid;
        isStartByCourse = true;
        setData(course);

        DBManager.INSTANCE.queryItem(uid).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AffairApi.AffairItem>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(AffairApi.AffairItem affairItem) {
                        if (affairItem != null) {
                            mWeekAdapter.addAllWeekNum(affairItem.getDate().get(0).getWeek());
                            //onWeekChooseOkClick();
                            StringBuilder builder = new StringBuilder();
                            for (AffairApi.AffairItem.DateBean dateBean : affairItem.getDate()) {
                                Position position = new Position(dateBean.getDay(), dateBean.getClassX());
                                mPositions.add(position);
                                for (int i = 0; i < mPositions.size() && i < 3; i++) {
                                    builder.append(WEEKS[mPositions.get(i).getX()] + CLASSES[mPositions.get(i).getY()] + " ");
                                }
                            }
                            mTimeText.setText(builder.toString());
                            mTitleEdit.setText(affairItem.getTitle());
                            mContentEdit.setText(affairItem.getContent());
                            mRemindText.setText(TIMES[transferTimeToText(course.time)]);
                            mRemindText.setText(TIME_MINUTE[transferTimeToText(course.time)]);
                        }

                    }
                });
    }

    private void setData(Affair course) {
        mTitleEdit.setText(course.course);
        mContentEdit.setText(course.teacher);
        mWeekAdapter.addAllWeekNum(course.week);
        //onWeekChooseOkClick();
        Position position = new Position(course.hash_day, course.hash_lesson);
        mPositions.add(position);
        StringBuilder builder = new StringBuilder();
        mRemindText.setText(TIMES[transferTimeToText(course.time)]);
        for (int i = 0; i < mPositions.size() && i < 3; i++) {
            builder.append(WEEKS[mPositions.get(i).getX()] + CLASSES[mPositions.get(i).getY()] + " ");
        }
        mTimeText.setText(builder.toString());

    }

    private boolean initData() {
        Position position = (Position) getIntent().getSerializableExtra(BUNDLE_KEY);
        if (position != null) {
            mPositions.add(position);
            mTimeText.setText(WEEKS[position.getX()] + CLASSES[position.getY()]);
        }
        int currentWeek = getIntent().getIntExtra(WEEK_NUMBER, -1);
        if (currentWeek != -1) {
            mWeekAdapter.addWeekNum(currentWeek);
            //onWeekChooseOkClick();
            return true;
        }
        return false;
    }

    private void initView() {
        initToolbar();
    }

    private void initPickTimeDialog() {
        mPickTimeDialog = new BottomSheetDialog(this);
        View itemView = LayoutInflater.from(this).inflate(R.layout.dialog_pick_time, null, false);
        mPickTimeDialog.setContentView(itemView);

        itemView.findViewById(R.id.divider).setVisibility(View.GONE);
        GridView gridView = (GridView) itemView.findViewById(R.id.gridView);
        int numCol = 7;
        int size = (DensityUtils.getScreenWidth(this) - DensityUtils.dp2px(this, 35 + 6)) / 7;
        ArrayList<Position> positions = new ArrayList<>(mPositions);
        gridView.setNumColumns(numCol);
        gridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 7 * CLASSES.length;
            }

            @Override
            public Object getItem(int position) {
                return positions.contains(new Position(position % numCol, position / numCol));
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                CardView cardView = new CardView(EditAffairActivity.this);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, size);
                cardView.setLayoutParams(layoutParams);
                Position position1 = new Position(position % numCol, position / numCol);
                if (positions.contains(position1)) {
                    cardView.setBackgroundResource(R.drawable.shape_rectangle_blue_fill);
                    cardView.setCardElevation(DensityUtils.dp2px(parent.getContext(), 2));
                } else {
                    cardView.setCardElevation(0);
                    cardView.setBackgroundColor(Color.WHITE);
                }
                return cardView;
            }
        });
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            CardView cardView = (CardView) view;
            Position position1 = new Position(position % numCol, position / numCol);
            if (positions.contains(position1)) {
                cardView.setBackgroundColor(Color.WHITE);
                cardView.setCardElevation(0);
                positions.remove(position1);
            } else {
                cardView.setBackgroundResource(R.drawable.shape_rectangle_blue_fill);
                cardView.setCardElevation(DensityUtils.dp2px(this, 2));
                positions.add(position1);
            }
        });

        itemView.findViewById(R.id.cancel).setOnClickListener(v -> {
            positions.clear();
            positions.addAll(mPositions);
            mPickTimeDialog.dismiss();
        });
        itemView.findViewById(R.id.sure).setOnClickListener(v -> {
            mPositions.clear();
            mPositions.addAll(positions);
            mPickTimeDialog.dismiss();
        });
    }

    private void initPickWeekDialog() {
        mPickWeekDialog = new BottomSheetDialog(this);
        View itemView = LayoutInflater.from(this).inflate(R.layout.dialog_pick_week, null, false);
        GridLayoutManager layoutManager = new GridLayoutManager(this,
                Math.max(1, DensityUtils.getScreenWidth(this) / DensityUtils.dp2px(this, 100)));
        RecyclerView rv = (RecyclerView) itemView.findViewById(R.id.recyclerView);
        rv.setLayoutManager(layoutManager);
        mWeekAdapter = new WeekAdapter();
        rv.setAdapter(mWeekAdapter);
        View cancel = itemView.findViewById(R.id.cancel);
        View sure = itemView.findViewById(R.id.sure);
        cancel.setOnClickListener(v -> mPickWeekDialog.dismiss());
        sure.setOnClickListener(v -> {
            weeks.clear();
            weeks.addAll(mWeekAdapter.getWeeks());
            if (weeks.size() != 0) {
                Collections.sort(weeks);
                String data = weeks.toString();
                data = data.substring(1, data.length() - 1);
                mWeekText.setText("第" + data + "周");
                mWeekText.setTextColor(Color.parseColor("#666666"));
            } else {
                mWeekText.setText("选择周数");
                mWeekText.setTextColor(Color.parseColor("#999999"));
            }
            mPickWeekDialog.dismiss();
        });
        mPickWeekDialog.setContentView(itemView);
    }

    private void initPickRemindDialog() {
        mPickRemindDialog = new PickerBottomSheetDialog(this);
        mPickRemindDialog.setData(TIMES);
        mPickRemindDialog.setOnClickListener(new PickerBottomSheetDialog.OnClickListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onSure(String value, int position) {
                time = position;
                mRemindText.setTextColor(position == 0 ?
                        Color.parseColor("#999999") : Color.parseColor("#666666"));
                mRemindText.setText(value);
            }
        });
    }

    private void initToolbar() {
        if (mToolbar != null) {
            mToolbar.setTitle("");
            mToolbarTitle.setText("编辑备忘");
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_back);
            mToolbar.setNavigationOnClickListener(v -> EditAffairActivity.this.finish());
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        }
    }

    public static void editAffairActivityStart(Context context, int weekNum) {
        Intent starter = new Intent(context, EditAffairActivity.class);
        starter.putExtra(WEEK_NUMBER, weekNum);
        context.startActivity(starter);
    }

    public static void editAffairActivityStart(Context context, Affair affair) {
        Intent starter = new Intent(context, EditAffairActivity.class);
        starter.putExtra(COURSE_KEY, (Parcelable) affair);
        context.startActivity(starter);
    }

    private int transferTimeToText(int time) {
        int index;
        switch (time) {
            case 5:
                index = 1;
                break;
            case 10:
                index = 2;
                break;
            case 20:
                index = 3;
                break;
            case 30:
                index = 4;
                break;
            case 60:
                index = 5;
                break;
            default:
                index = 0;
                break;
        }
        return index;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeChooseEvent(TimeChooseEvent event) {
        mPositions.clear();
        mPositions.addAll(event.getPositions());
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < mPositions.size() && i < 3; i++) {
            stringBuffer.append(WEEKS[mPositions.get(i).getX()] + CLASSES[mPositions.get(i).getY()] + " ");
        }
        mTimeText.setText(stringBuffer.toString());
    }


    class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.WeekViewHolder> {
        private List<String> weeks = new ArrayList<>();
        private Set<Integer> mWeeks = new HashSet<>();


        public WeekAdapter() {
            weeks.addAll(Arrays.asList(EditAffairActivity.this.getResources().getStringArray(R.array.titles_weeks)));
            weeks.remove(0);
        }

        @Override
        public WeekAdapter.WeekViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WeekAdapter.WeekViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_week, parent, false));
        }

        public Set<Integer> getWeeks() {
            return mWeeks;
        }

        public void addWeekNum(int weekNum) {
            mWeeks.add(weekNum);
        }

        public void addAllWeekNum(List<Integer> weekNums) {
            mWeeks.addAll(weekNums);
        }

        @Override
        public void onBindViewHolder(WeekAdapter.WeekViewHolder holder, int position) {
            holder.mTextView.setBackgroundResource(R.drawable.shape_rectangle_grey_stroke);
            holder.mTextView.setTextColor(Color.parseColor("#666666"));
            holder.isChoose = false;
            holder.mTextView.setText(weeks.get(position));
            if (mWeeks.contains(position + 1)) {
                holder.mTextView.setTextColor(Color.parseColor("#ffffff"));
                holder.mTextView.setBackgroundResource(R.drawable.shape_rectangle_blue_gradient);
                holder.isChoose = true;
            }
            holder.mTextView.setOnClickListener((v) -> {
                if (holder.isChoose) {
                    holder.mTextView.setBackgroundResource(R.drawable.shape_rectangle_grey_stroke);
                    holder.mTextView.setTextColor(Color.parseColor("#666666"));
                    mWeeks.remove(position + 1);
                    holder.isChoose = false;
                } else {
                    holder.mTextView.setTextColor(Color.parseColor("#ffffff"));
                    holder.mTextView.setBackgroundResource(R.drawable.shape_rectangle_blue_gradient);
                    mWeeks.add(position + 1);
                    holder.isChoose = true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return weeks.size();
        }

        class WeekViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.item_tv_choose_week)
            TextView mTextView;
            private boolean isChoose = false;
            private RelativeLayout layout;

            public boolean isChoose() {
                return isChoose;
            }

            public void setChoose(boolean choose) {
                isChoose = choose;
            }

            public WeekViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(itemView);
                mTextView = (TextView) itemView.findViewById(R.id.item_tv_choose_week);
            }
        }
    }
}
