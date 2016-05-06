package com.mredrock.cyxbsmobile.ui.activity.social;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mredrock.cyxbsmobile.R;
import com.mredrock.cyxbsmobile.component.widget.recycler.DividerItemDecoration;
import com.mredrock.cyxbsmobile.model.social.CommentContent;
import com.mredrock.cyxbsmobile.model.social.HotNewsContent;
import com.mredrock.cyxbsmobile.model.social.OfficeNewsContent;
import com.mredrock.cyxbsmobile.network.RequestManager;
import com.mredrock.cyxbsmobile.subscriber.SimpleSubscriber;
import com.mredrock.cyxbsmobile.subscriber.SubscriberListener;
import com.mredrock.cyxbsmobile.ui.activity.BaseActivity;
import com.mredrock.cyxbsmobile.ui.adapter.HeaderViewRecyclerAdapter;
import com.mredrock.cyxbsmobile.ui.adapter.NewsAdapter;
import com.mredrock.cyxbsmobile.ui.adapter.SpecificNewsCommentAdapter;
import com.mredrock.cyxbsmobile.util.Util;
import com.mredrock.cyxbsmobile.util.download.DownloadHelper;
import com.mredrock.cyxbsmobile.util.download.callback.OnDownloadListener;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SpecificNewsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {


    public static final String STRAT_DATA       = "dataBean";
    public static final String ITEM_VIEW_HEIGHT = "itemViewHeight";

    @Bind(R.id.toolbar)
    Toolbar            mToolbar;
    @Bind(R.id.toolbar_title)
    TextView           mToolBarTitle;
    @Bind(R.id.news_edt_comment)
    EditText           mNewsEdtComment;
    @Bind(R.id.refresh)
    SwipeRefreshLayout mRefresh;
    @Bind(R.id.recyclerView)
    RecyclerView       mRecyclerView;
    @Bind(R.id.btn_send)
    TextView           mSendText;
    @Bind(R.id.downText)
    TextView           mTextDown;

    private NewsAdapter.ViewHolder mWrapView;

    private View mHeaderView;

    public static final String TAG = "SpecificNewsActivity";
    private HotNewsContent dataBean;

    private SpecificNewsCommentAdapter mSpecificNewsCommentAdapter;
    private HeaderViewRecyclerAdapter  mHeaderViewRecyclerAdapter;
    private List<CommentContent> mDatas = null;


    private View mFooterView;

    public static void startActivityWithDataBean(Context context, HotNewsContent dataBean, int itemViewHeight) {
        Intent intent = new Intent(context, SpecificNewsActivity.class);
        intent.putExtra(STRAT_DATA, dataBean);
        intent.putExtra(ITEM_VIEW_HEIGHT, itemViewHeight);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_news);
        ButterKnife.bind(this);
        mRefresh.setColorSchemeColors(R.color.colorAccent);
        mHeaderView = LayoutInflater.from(this)
                                    .inflate(R.layout.list_news_item_header, null, false);
        mWrapView = new NewsAdapter.ViewHolder(mHeaderView);
        String article_id = getIntent().getStringExtra("article_id");
        if (article_id != null) {
            getDataBeanById(article_id);
        } else {
            dataBean = getIntent().getParcelableExtra(STRAT_DATA);
            mWrapView.setData(dataBean, true);
            if (dataBean.content.getArticletype_id() != null)
                doWithNews(mWrapView, dataBean.content);
            requestComments();
        }
        // itemViewHeight = getIntent().getIntExtra(ITEM_VIEW_HEIGHT, 100);
        init();
    }

    private void init() {
        initToolbar();
        mRefresh.setOnRefreshListener(this);
        mSpecificNewsCommentAdapter = new SpecificNewsCommentAdapter(mDatas, this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(mSpecificNewsCommentAdapter);
        mRecyclerView.setAdapter(mHeaderViewRecyclerAdapter);
        mHeaderViewRecyclerAdapter.addHeaderView(mWrapView.itemView);

    }

    private void doWithNews(NewsAdapter.ViewHolder mWrapView, OfficeNewsContent bean) {
        mWrapView.mTextContent.setText(Html.fromHtml(dataBean.content != null ? dataBean.content.content : ""));
        mWrapView.mTextName.setText(!bean.unit.equals("") ? bean.unit : getString(R.string.jwzx));
        mWrapView.mTextView_ex.setVisibility(View.INVISIBLE);
        if (dataBean.content.content.charAt(0) == '<')
            mWrapView.mTextContent.setText(dataBean.content.title);
        if (!bean.address.equals("")) {
            mTextDown.setVisibility(View.VISIBLE);
            String[] address = bean.address.split("\\|");
            String[] names = bean.name.split("\\|");

            mTextDown.setOnClickListener(view -> showDownListDialog(address, names));
        }
    }

    public void showDownListDialog(String[] address, String[] names) {

        DownloadHelper downloadHelper = new DownloadHelper(this, true);
        downloadHelper.prepare(Arrays.asList(names), Arrays.asList(address),
                new OnDownloadListener() {
                    @Override
                    public void startDownload() {
                    }

                    @Override
                    public void downloadSuccess() {
                        downloadHelper.tryOpenFile();
                    }

                    @Override
                    public void downloadFailed(String message) {
                        Util.toast(SpecificNewsActivity.this, getResources().getString(R.string.load_failed));
                        if (message != null) {
                            Log.d(TAG, "error is---->>> " + message);
                        }
                    }
                });

    }

    private void requestComments() {
        RequestManager.getInstance()
                      .getRemarks(dataBean.id, dataBean.type_id)
                      .doOnSubscribe(() -> showLoadingProgress())
                      .subscribe(reMarks -> {
                          mDatas = reMarks;
                          if ((mDatas == null || mDatas.size() == 0) && mFooterView == null)
                              addFooterView();
                          if ((mDatas.size() != 0) && mFooterView != null)
                              removeFooterView();
                          mSpecificNewsCommentAdapter = new SpecificNewsCommentAdapter(mDatas, SpecificNewsActivity.this);
                          mHeaderViewRecyclerAdapter.setAdapter(mSpecificNewsCommentAdapter);
                          closeLoadingProgress();
                      }, throwable -> {
                          closeLoadingProgress();
                          getDataFailed(throwable.toString());
                      });
    }

    private void removeFooterView() {
        mHeaderViewRecyclerAdapter.reMoveFooterView();
        mHeaderViewRecyclerAdapter.notifyDataSetChanged();
    }

    private void addFooterView() {
        mFooterView = LayoutInflater.from(this)
                                    .inflate(R.layout.list_footer_item_remark, mRecyclerView, false);
        mHeaderViewRecyclerAdapter.addFooterView(mFooterView);
    }

    private void initToolbar() {
        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        mToolbar.setTitle("");
        mToolBarTitle.setText(getString(R.string.specific_news_title));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(view -> SpecificNewsActivity.this.finish());
    }

    private void getDataFailed(String reason) {
        Toast.makeText(this, getString(R.string.erro), Toast.LENGTH_SHORT).show();
        Log.e(TAG, reason);
    }

    @OnClick(R.id.btn_send)
    public void sendComment(View view) {
        if (mNewsEdtComment.getText().toString().equals(""))
            Toast.makeText(SpecificNewsActivity.this, getString(R.string.alter), Toast.LENGTH_SHORT)
                 .show();
        else
            RequestManager.getInstance()
                          .postReMarks(dataBean.id, dataBean.type_id, mNewsEdtComment.getText()
                                                                                     .toString())
                          .doOnSubscribe(() -> showLoadingProgress())
                          .subscribe(new SimpleSubscriber<>(this, new SubscriberListener<String>() {
                              @Override
                              public void onCompleted() {
                                  super.onCompleted();
                                  requestComments();
                                  mNewsEdtComment.getText().clear();
                              }

                              @Override
                              public void onError(Throwable e) {
                                  super.onError(e);
                                  showUploadFail(e.toString());
                              }
                          }));

    }

    private void getDataBeanById(String article_id) {
        RequestManager.getInstance().getTrendDetail("2014213983", "26722X", 5,
                article_id)
                      .subscribe(newses -> {
                          if (newses != null && newses.size() > 0) {
                              dataBean = newses.get(0).data;
                              mWrapView.setData(dataBean, false);
                              requestComments();
                          }
                      }, throwable -> {
                          showUploadFail(throwable.getMessage());
                      });
    }

    @Override
    public void onRefresh() {
        requestComments();
    }

    private void showLoadingProgress() {
        mRefresh.setRefreshing(true);
    }

    private void closeLoadingProgress() {
        mRefresh.setRefreshing(false);
    }

    private void showUploadFail(String reason) {
        Log.e(TAG, "showUploadFail---->>>" + reason);
    }

    private void showUploadSucess() {
        Log.d(TAG, "showUploadSuccess");
    }


}
