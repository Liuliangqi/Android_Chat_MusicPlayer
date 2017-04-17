package com.example.liuliangqi.CP.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.AdapterView.OnItemClickListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.android.utils.TextUtils;
import com.example.liuliangqi.CP.Config.ConstantKeys;
import com.example.liuliangqi.CP.R;
import com.example.liuliangqi.CP.Utils.HomeWatcher;
import com.example.liuliangqi.CP.Utils.L;
import com.example.liuliangqi.CP.Utils.MusicUtils;
import com.example.liuliangqi.CP.Utils.SendMsgAsyncTask;
import com.example.liuliangqi.CP.Utils.SharePreferenceUtil;
import com.example.liuliangqi.CP.Utils.SoundUtil;
import com.example.liuliangqi.CP.Utils.T;
import com.example.liuliangqi.CP.Utils.TimeUtil;
import com.example.liuliangqi.CP.adapter.FaceAdapter;
import com.example.liuliangqi.CP.adapter.FacePageAdeapter;
import com.example.liuliangqi.CP.adapter.MessageAdapter;
import com.example.liuliangqi.CP.adapter.MyMusicAdapter;
import com.example.liuliangqi.CP.album.AlbumHelper;
import com.example.liuliangqi.CP.bean.ImageBucket;
import com.example.liuliangqi.CP.bean.ImageTool;
import com.example.liuliangqi.CP.bean.Message;
import com.example.liuliangqi.CP.bean.MessageItem;
import com.example.liuliangqi.CP.bean.Music;
import com.example.liuliangqi.CP.bean.RecentItem;
import com.example.liuliangqi.CP.bean.User;
import com.example.liuliangqi.CP.db.MessageDB;
import com.example.liuliangqi.CP.db.RecentDB;
import com.example.liuliangqi.CP.db.UserDB;
import com.example.liuliangqi.CP.push_client.PushMessageReceiver;
import com.example.liuliangqi.CP.service.PushApplication;
import com.example.liuliangqi.CP.ui.MyListView;
import com.example.liuliangqi.CP.view.CirclePageIndicator;
import com.example.liuliangqi.CP.view.JazzyViewPager;
import com.example.liuliangqi.CP.view.JazzyViewPager.TransitionEffect;
import com.example.liuliangqi.CP.view.Util;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;

/**
 * Created by liuliangqi on 2017/4/2.
 */

public class ChatFragment extends Fragment implements View.OnClickListener, PushMessageReceiver.EventHandler, HomeWatcher.OnHomePressedListener,OnTouchListener, MyListView.IXListViewListener{

    private Context context = MainActivity.getAppContext();
    private Activity activity;
    private PushApplication mApplication;
    private UserDB mUserDB;
    private MessageDB mMsgDB;// 保存消息的数据库
    private RecentDB mRecentDB;
    private static MessageAdapter mAdapter;
    private Gson mGson;
    private HomeWatcher mHomeWatcher;


    public static final int NEW_MESSAGE = 0x001;// 收到消息
    public static int MSGPAGERNUM;
    private static final int POLL_INTERVAL = 300;
    private static final long DELAY_VOICE = 1000;// 语音录制计时
    private static final int CAMERA_WITH_DATA = 10;

    private SharePreferenceUtil mSpUtil;
    public static String DEFAULT_ID = "1100877319654414526";
    public static String defaulgUserName = "在飞";
    public static String defaulgIcon = "4";
    public static int defaultCount = 0;

    private ImageButton mFaceBtn;
    private boolean isFaceShow = false;
    private InputMethodManager mInputMethodManager;
    private EditText mEtMsg;


    private LinearLayout mllFace;// 表情显示的布局
    private JazzyViewPager mFaceViewPager;// 表情viewpager
    private int mCurrentPage = 0;// 表情页数
    private List<String> mKeyList;// 表情list

    private Button mBtnSend;// 发送消息按钮
    private MyListView mMsgListView;// 展示消息的
    private WindowManager.LayoutParams mParams;
    private SoundUtil mSoundUtil;


    // 接受数据
    private TextView mTvVoiceBtn;// 语音按钮
    private ImageButton mIbMsgBtn;// 文字按钮
    private View mViewVoice;// 语音界面
    private View mViewInput;
    private ImageButton mIbVoiceBtn;


    private View mChatPopWindow;
    private LinearLayout mRecording;
    private LinearLayout mLlVoiceShort;// 录制时间过短
    private Handler mHandler = new Handler();
    private int flag = 1;
    private boolean isShosrt = false;

    private long mStartRecorderTime;
    private long mEndRecorderTime;

    private ImageView volume;
    private String mRecordTime;
    private TextView mTvVoiceRecorderTime;// 录制的时间
    private int mRcdStartTime = 0;// 录制的开始时间
    private int mRcdVoiceDelayTime = 1000;
    private int mRcdVoiceStartDelayTime = 300;
    private boolean isCancelVoice;// 不显示语音

    /**
     * 表情viewPager切换效果
     */
    private TransitionEffect mEffects[] = { TransitionEffect.Standard,
            TransitionEffect.Tablet, TransitionEffect.CubeIn,
            TransitionEffect.CubeOut, TransitionEffect.FlipVertical,
            TransitionEffect.FlipHorizontal, TransitionEffect.Stack,
            TransitionEffect.ZoomIn, TransitionEffect.ZoomOut,
            TransitionEffect.RotateUp, TransitionEffect.RotateDown,
            TransitionEffect.Accordion, };// 表情翻页效果

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            stopRecord();
        }
    };

    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSoundUtil.getAmplitude();
            Log.e("fff", "音量:" + amp);
            updateDisplay(amp);
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);

        }
    };


    private void updateDisplay(double signalEMA) {

        switch ((int) signalEMA) {
            case 0:
            case 1:
                volume.setImageResource(R.drawable.amp1);
                break;
            case 2:
            case 3:
                volume.setImageResource(R.drawable.amp2);

                break;
            case 4:
            case 5:
                volume.setImageResource(R.drawable.amp3);
                break;
            case 6:
            case 7:
                volume.setImageResource(R.drawable.amp4);
                break;
            case 8:
            case 9:
                volume.setImageResource(R.drawable.amp5);
                break;
            case 10:
            case 11:
                volume.setImageResource(R.drawable.amp6);
                break;
            default:
                volume.setImageResource(R.drawable.amp7);
                break;
        }
    }



    /**
     * 录制语音计时器
     *
     * @desc:
     * @author: pangzf
     * @date: 2014年11月10日 下午3:46:46
     */
    private class VoiceRcdTimeTask implements Runnable {
        int time = 0;

        public VoiceRcdTimeTask(int startTime) {
            time = startTime;
        }

        @Override
        public void run() {
            time++;

            updateTimes(time);
        }
    }

    public void updateTimes(final int time) {
        Log.e("fff", "时间:" + time);
        ((Activity)context).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mTvVoiceRecorderTime.setText(TimeUtil
                        .getVoiceRecorderTime(time));
            }
        });

    }

    private VoiceRcdTimeTask mVoiceRcdTimeTask;
    private ScheduledExecutorService mExecutor;// 录制计时器
    private Button mBtnAffix;
    private LinearLayout mLlAffix;
    private TextView mTvTakPicture;// 拍照
    private String mTakePhotoFilePath;
    private TextView mIvAffixAlbum;// 相册
    private AlbumHelper albumHelper = null;// 相册管理类
    private static List<ImageBucket> albumList = null;// 相册数据list
    private TextView mTvChatTitle;


    private Handler handler = new Handler() {
        // 接收到消息
        public void handleMessage(android.os.Message msg) {
            if (msg.what == NEW_MESSAGE) {
                // String message = (String) msg.obj;
                Message msgItem = (Message) msg.obj;
                String userId = msgItem.getUser_id();
                if (!userId.equals(mSpUtil.getUserId()))// 如果不是当前正在聊天对象的消息，不处理
                    return;

                int headId = msgItem.getHead_id();
                /*
                 * try { headId = Integer
                 * .parseInt(JsonUtil.getFromUserHead(message)); } catch
                 * (Exception e) { L.e("head is not integer  " + e); }
                 */
                // ===接收的额数据，如果是record语音的话，用播放展示
                MessageItem item = null;
                RecentItem recentItem = null;
                if (msgItem.getMessagetype() == MessageItem.MESSAGE_TYPE_TEXT) {
                    item = new MessageItem(MessageItem.MESSAGE_TYPE_TEXT,
                            msgItem.getNick(), System.currentTimeMillis(),
                            msgItem.getMessage(), headId, true, 0,
                            msgItem.getVoiceTime());
                    recentItem = new RecentItem(MessageItem.MESSAGE_TYPE_TEXT,
                            userId, headId, msgItem.getNick(),
                            msgItem.getMessage(), 0,
                            System.currentTimeMillis(), msgItem.getVoiceTime());

                } else if (msgItem.getMessagetype() == MessageItem.MESSAGE_TYPE_RECORD) {
                    item = new MessageItem(MessageItem.MESSAGE_TYPE_RECORD,
                            msgItem.getNick(), System.currentTimeMillis(),
                            msgItem.getMessage(), headId, true, 0,
                            msgItem.getVoiceTime());
                    recentItem = new RecentItem(
                            MessageItem.MESSAGE_TYPE_RECORD, userId, headId,
                            msgItem.getNick(), msgItem.getMessage(), 0,
                            System.currentTimeMillis(), msgItem.getVoiceTime());
                } else if (msgItem.getMessagetype() == MessageItem.MESSAGE_TYPE_IMG) {
                    item = new MessageItem(MessageItem.MESSAGE_TYPE_IMG,
                            msgItem.getNick(), System.currentTimeMillis(),
                            msgItem.getMessage(), headId, true, 0,
                            msgItem.getVoiceTime());
                    recentItem = new RecentItem(MessageItem.MESSAGE_TYPE_IMG,
                            userId, headId, msgItem.getNick(),
                            msgItem.getMessage(), 0,
                            System.currentTimeMillis(), msgItem.getVoiceTime());
                }

                mAdapter.upDateMsg(item);// 更新界面
                mMsgDB.saveMsg(msgItem.getUser_id(), item);// 保存数据库
                mRecentDB.saveRecent(recentItem);

                scrollToBottomListItem();

            }
        }

    };


    /**
     *Fragment 初始化
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View chat_view = inflater.inflate(R.layout.tab01, container, false);
        //获取到屏幕属性
        mParams = activity.getWindow().getAttributes();
        //进入界面，先隐藏键盘，点击再显示
        mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mSpUtil = PushApplication.getInstance().getSpUtil();
        Set<String> keySet = PushApplication.getInstance().getFaceMap().keySet();
        mKeyList = new ArrayList<String>();
        mKeyList.addAll(keySet);
        MSGPAGERNUM = 0;
        //获取音频处理工具实例
        mSoundUtil = SoundUtil.getInstance();


        initView(chat_view);
        initFacePage(chat_view);

        mApplication.getNotificationManager().cancel(PushMessageReceiver.NOTIFY_ID);
        PushMessageReceiver.mNewNum = 0;

        mUserDB = mApplication.getUserDB();

        // 启动百度推送服务
        PushManager.startWork(context,
                PushConstants.LOGIN_TYPE_API_KEY, PushApplication.API_KEY);// 无baidu帐号登录,以apiKey随机获取一个id

        // 设置表情翻页效果
        // mSpUtil.setFaceEffect(8);

        initUserInfo();

        return chat_view;
    }

    private void initUserInfo() {

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }




    //初始化相册数据
    private void initAlbumData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                albumHelper = AlbumHelper.getHelper(MainActivity.getAppContext());
                albumList = albumHelper.getImagesBucketList(false);
            }
        }).start();
    }

    private void initView(View parent){
        //初始化相册数据
        initAlbumData();
        //初始化图片附件
        mBtnAffix = (Button) parent.findViewById(R.id.btn_chat_affix);
        mLlAffix = (LinearLayout) parent.findViewById(R.id.ll_chatmain_affix);
        mTvTakPicture = (TextView) parent.findViewById(R.id.tv_chatmain_affix_take_picture);

        mBtnAffix.setOnClickListener(this);
        mTvTakPicture.setOnClickListener(this);

        //初始化相册
        mIvAffixAlbum = (TextView) parent.findViewById(R.id.tv_chatmain_affix_album);
        mIvAffixAlbum.setOnClickListener(this);

        //初始化底部栏
        mFaceBtn = (ImageButton) parent.findViewById(R.id.face_btn);
        mEtMsg = (EditText) parent.findViewById(R.id.msg_et);
        mInputMethodManager.hideSoftInputFromWindow(mEtMsg.getWindowToken(), 0);
        mFaceBtn.setOnClickListener(this);

        //初始化表情栏
        mllFace = (LinearLayout) parent.findViewById(R.id.face_ll);
        mFaceViewPager = (JazzyViewPager) parent.findViewById(R.id.face_pager);
        mBtnSend = (Button) parent.findViewById(R.id.send_btn);
        mBtnSend.setClickable(true);
        mBtnSend.setEnabled(true);
        mBtnSend.setOnClickListener(this);


        // 消息
        mApplication = PushApplication.getInstance();
        mMsgDB = mApplication.getMessageDB();// 发送数据库
        mRecentDB = mApplication.getRecentDB();// 接收消息数据库
        mGson = mApplication.getGson();

        mAdapter = new MessageAdapter(context, initMsgData());
        mMsgListView = (MyListView) parent.findViewById(R.id.msg_listview);

        //触摸ListView隐藏表情和输入法
        mMsgListView.setOnTouchListener(this);
        mMsgListView.setPullLoadEnable(false);
        mMsgListView.setXListViewListener(this);
        mMsgListView.setAdapter(mAdapter);
        mMsgListView.setSelection(mAdapter.getCount() - 1);

//       输入框key监听事件
        mEtMsgOnKeyListener();

        //语音初始化
        initRecorderView(parent);

        mTvVoicePreeListener();

    }



    //输入框key监听事件
    //监听了返回键，文本框输入，以及获取焦点
    private void mEtMsgOnKeyListener() {
        mEtMsg.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mParams.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
                        //如果键盘显示了，就隐藏表情
                        hideFaceAffic();
                        return true;
                    }
                }
                return false;
            }
        });
        mEtMsg.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            //输入完成后，让发送按钮初始并隐藏附件栏
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mBtnSend.setEnabled(true);
                    mBtnAffix.setVisibility(View.GONE);
                    mBtnSend.setVisibility(View.VISIBLE);
                } else {
                    //否则显示附件栏并隐藏发送按钮
                    mBtnSend.setEnabled(false);
                    mBtnAffix.setVisibility(View.VISIBLE);
                    mBtnSend.setVisibility(View.GONE);
                }
            }
        });

        mEtMsg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    hideFaceAffic();
                }
            }
        });

    }


    //初始化语音布局
    private void initRecorderView(View parent){
        //初始化语音按钮
        mIbMsgBtn = (ImageButton) parent.findViewById(R.id.ib_chatmain_msg);
        //初始化语音界面
        mViewVoice = parent.findViewById(R.id.ll_chatmain_voice);
        //T 按钮
        mIbVoiceBtn = (ImageButton) parent.findViewById(R.id.ib_chatmain_voice);

        //这是聊天输入界面部件
        mViewInput = parent.findViewById(R.id.ll_chatmain_input);
        //按住说话
        mTvVoiceBtn = (TextView) parent.findViewById(R.id.tv_chatmain_press_voice);

        mIbMsgBtn.setOnClickListener(this);
        mTvVoiceBtn.setOnClickListener(this);
        mIbVoiceBtn.setOnClickListener(this);

        //include 包含的语音布局 显示屏幕中间的界面
        //在聊天界面显示的界面
        mChatPopWindow = parent.findViewById(R.id.rcChat_popup);

        //正在录音的弹出窗口
        mRecording = (LinearLayout) parent.findViewById(R.id.voice_recoding);

        //时间太短界面
        mLlVoiceShort = (LinearLayout) parent.findViewById(R.id.voice_rcd_hint_tooshort);

        //音量显示
        volume = (ImageView) parent.findViewById(R.id.volume);
    }


    //录音动作
    private void mTvVoicePreeListener() {
        // 按住录音添加touch事件
        mTvVoiceBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!Environment.getExternalStorageDirectory().exists()) {
                    Toast.makeText(context, "No SDCard",
                            Toast.LENGTH_LONG).show();
                    return false;
                }

                int[] location = new int[2];
                mTvVoiceBtn.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
                int[] del_location = new int[2];
                int del_Y = del_location[1];
                int del_x = del_location[0];
                if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
                    if (!Environment.getExternalStorageDirectory().exists()) {
                        Toast.makeText(context, "No SDCard",
                                Toast.LENGTH_LONG).show();
                        return false;
                    }
                    // 判断手势按下的位置是否是语音录制按钮的范围内
                    mTvVoiceBtn.setBackgroundResource(R.color.grey);
                    mTvVoiceBtn.setText("松开发送");
                    mChatPopWindow.setVisibility(View.VISIBLE);
                    //如果录音超过300ms就显示录音界面
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            if (!isShosrt) {
                                mRecording.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 300);
                    //确认删除界面也不要显示s
                    startRecord();
                    flag = 2;
                } else if (event.getAction() == MotionEvent.ACTION_UP
                        && flag == 2) {// 松开手势时执行录制完成
                    mTvVoiceBtn.setBackgroundResource(R.drawable.voice_background);
                    mTvVoiceBtn.setText("按住说话");
                    //松开手势表示录音完成，录制界面隐藏
                    mRecording.setVisibility(View.GONE);
                    try {
                        stopRecord();
                    } catch (IllegalStateException e) {
                        Toast.makeText(context, "麦克风不可用", Toast.LENGTH_SHORT).show();
                        isCancelVoice = true;
                    }
                    mEndRecorderTime = System.currentTimeMillis();
                    flag = 1;
                    int mVoiceTime = (int) ((mEndRecorderTime - mStartRecorderTime) / 1000);
                    //如果小于三秒，加载和录制界面都不显示
                    if (mVoiceTime < 1.5) {
                        isShosrt = true;
                        mLlVoiceShort.setVisibility(View.VISIBLE);

                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                mLlVoiceShort.setVisibility(View.GONE);
                                mChatPopWindow.setVisibility(View.GONE);
                                isShosrt = false;
                            }
                        }, 500);

                        File file = new File(mSoundUtil.getFilePath(context, mRecordTime).toString());
                        if (file.exists()) {
                            file.delete();
                        }
                        return false;
                    }
                    // ===发送出去,界面展示
                    if (!isCancelVoice) {
                        showVoice(mVoiceTime);
                    }
                    // }
                }
                return false;

            }
        });
    }
    //停止录音
    private void stopRecord() throws IllegalStateException {
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);

        volume.setImageResource(R.drawable.amp1);
        if (mExecutor != null && !mExecutor.isShutdown()) {
            mExecutor.shutdown();
            mExecutor = null;
        }
        if (mSoundUtil != null) {
            mSoundUtil.stopRecord();
        }
    }

    //开始录音
    private void startRecord() {
        // ===录音格式：用户id_时间戳_send_sound
        // SoundUtil.getInstance().startRecord(MainActivity.this,
        // id_time_send_sound);

        mStartRecorderTime = System.currentTimeMillis();
        if (mSoundUtil != null) {
            mRecordTime = mSoundUtil.getRecordFileName();
            mSoundUtil.startRecord(MainActivity.getAppContext(), mRecordTime);
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);

            mVoiceRcdTimeTask = new VoiceRcdTimeTask(mRcdStartTime);

            if (mExecutor == null) {
                mExecutor = Executors.newSingleThreadScheduledExecutor();
                mExecutor.scheduleAtFixedRate(mVoiceRcdTimeTask,
                        mRcdVoiceStartDelayTime, mRcdVoiceDelayTime,
                        TimeUnit.MILLISECONDS);
            }

        }

    }


    public void initFacePage(View parent){
        List<View> lv = new ArrayList<View>();

        //TODO PushApplication
        for(int i = 0; i < PushApplication.NUM_PAGE; i++){
            lv.add(getGridView(i));
        }
        FacePageAdeapter adapter = new FacePageAdeapter(lv, mFaceViewPager);
        mFaceViewPager.setAdapter(adapter);
        mFaceViewPager.setCurrentItem(mCurrentPage);
        mFaceViewPager.setTransitionEffect(mEffects[mSpUtil.getFaceEffect()]);
        CirclePageIndicator indicator = (CirclePageIndicator) parent.findViewById(R.id.indicator);// 圆点
        indicator.setViewPager(mFaceViewPager);
        adapter.notifyDataSetChanged();
        mllFace.setVisibility(View.GONE);
        indicator.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                mCurrentPage = arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // do nothing
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // do nothing
            }
        });

    }

    private GridView getGridView(int i) {
        GridView gv = new GridView(context);
        gv.setNumColumns(7);
        gv.setSelector(new ColorDrawable(Color.TRANSPARENT));// 屏蔽GridView默认点击效果
        gv.setBackgroundColor(Color.TRANSPARENT);
        gv.setCacheColorHint(Color.TRANSPARENT);
        gv.setHorizontalSpacing(1);
        gv.setVerticalSpacing(1);
        gv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        gv.setGravity(Gravity.CENTER);
        gv.setAdapter(new FaceAdapter(context, i));
        gv.setOnTouchListener(forbidenScroll());
        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                if (position == PushApplication.NUM) {// 删除键的位置
                    int selection = mEtMsg.getSelectionStart();
                    String text = mEtMsg.getText().toString();
                    if (selection > 0) {
                        String text2 = text.substring(selection - 1);
                        if ("]".equals(text2)) {
                            int start = text.lastIndexOf("[");
                            int end = selection;
                            mEtMsg.getText().delete(start, end);
                            return;
                        }
                        mEtMsg.getText().delete(selection - 1, selection);
                    }
                } else {// 选择表情==
                    int count = mCurrentPage * PushApplication.NUM + position;
                    defaultCount = count;
                    // 下面这部分，在EditText中显示表情
                    Bitmap bitmap = BitmapFactory.decodeResource(
                            getResources(), (Integer) PushApplication
                                    .getInstance().getFaceMap().values()
                                    .toArray()[count]);
                    if (bitmap != null) {
                        int rawHeigh = bitmap.getHeight();
                        int rawWidth = bitmap.getHeight();
                        // 设置表情的大小===
                        int newHeight = Util.dip2px(context, 30);
                        int newWidth = Util.dip2px(context, 30);
                        // 计算缩放因子
                        float heightScale = ((float) newHeight) / rawHeigh;
                        float widthScale = ((float) newWidth) / rawWidth;
                        // 新建立矩阵
                        Matrix matrix = new Matrix();
                        matrix.postScale(heightScale, widthScale);
                        // 将图片大小压缩
                        // 压缩后图片的宽和高以及kB大小均会变化
                        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                rawWidth, rawHeigh, matrix, true);
                        ImageSpan imageSpan = new ImageSpan(context, newBitmap);
                        String emojiStr = mKeyList.get(count);
                        SpannableString spannableString = new SpannableString(
                                emojiStr);
                        spannableString.setSpan(imageSpan,
                                emojiStr.indexOf('['),
                                emojiStr.indexOf(']') + 1,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mEtMsg.append(spannableString);
                    } else {
                        String ori = mEtMsg.getText().toString();
                        int index = mEtMsg.getSelectionStart();
                        StringBuilder stringBuilder = new StringBuilder(ori);
                        stringBuilder.insert(index, mKeyList.get(count));
                        mEtMsg.setText(stringBuilder.toString());
                        mEtMsg.setSelection(index
                                + mKeyList.get(count).length());
                    }
                }
            }
        });
        return gv;
    }


    //防止乱抖动
    private OnTouchListener forbidenScroll() {
        return new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true;
                }
                return false;
            }
        };
    }

    //滑动到列表底部
    private void scrollToBottomListItem() {

        // todo eric, why use the last one index + 2 can real scroll to the
        // bottom?
        if (mMsgListView != null) {
            mMsgListView.setSelection(mAdapter.getCount() + 1);
        }
    }



    protected void showVoice(int mVoiceTime) {
        if (mRecordTime == null || "".equals(mRecordTime)) {
            return;
        }
        MessageItem item = new MessageItem(MessageItem.MESSAGE_TYPE_RECORD,
                mSpUtil.getNick(), System.currentTimeMillis(), mRecordTime,
                mSpUtil.getHeadIcon(), false, 0, mVoiceTime);
        mAdapter.upDateMsg(item);
        mMsgListView.setSelection(mAdapter.getCount() - 1);
        mMsgDB.saveMsg(mSpUtil.getUserId(), item);// 消息保存数据库
        // ===发送消息到服务器
        Message msgItem = new Message(
                MessageItem.MESSAGE_TYPE_RECORD, System.currentTimeMillis(),
                item.getMessage(), "", item.getVoiceTime());
        if ("".equals(mSpUtil.getUserId())) {
            Log.e("fff", "用户id为空3");
            return;
        }
        new SendMsgAsyncTask(mGson.toJson(msgItem), mSpUtil.getUserId()).send();// push发送消息到服务器
        // ===保存近期的消息
        RecentItem recentItem = new RecentItem(MessageItem.MESSAGE_TYPE_RECORD,
                mSpUtil.getUserId(), defaultCount, defaulgUserName, mSoundUtil
                .getFilePath(context, item.getMessage())
                .toString(), 0, System.currentTimeMillis(),
                item.getVoiceTime());
        mRecentDB.saveRecent(recentItem);
    }

    //TODO 接收到消息，用来更新ListView


    //隐藏界面
    private void hideInput(){
        mInputMethodManager.hideSoftInputFromWindow(mEtMsg.getWindowToken(), 0);
        //这里是为了解决屏幕黑一下的问题
        try {
            Thread.sleep(80);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void hideFaceAffic(){
        if(mllFace.isShown())
            mllFace.setVisibility(View.GONE);
        if(mLlAffix.isShown())
            mLlAffix.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.face_btn:
                //如果点击表情的时候是没有显示的，就先隐藏键盘
                if(mLlAffix.isShown()){
                    mLlAffix.setVisibility(View.GONE);
                }
                if(mllFace.isShown())
                    mllFace.setVisibility(View.GONE);
                else{
                    hideInput();
                    mllFace.setVisibility(View.VISIBLE);
                    mEtMsg.clearFocus();
                }

                break;
            case R.id.send_btn:
                String msg = mEtMsg.getText().toString();
                MessageItem item = new MessageItem(
                        MessageItem.MESSAGE_TYPE_TEXT, mSpUtil.getNick(),
                        System.currentTimeMillis(), msg, mSpUtil.getHeadIcon(),
                        false, 0, 0);
                mAdapter.upDateMsg(item);
                mMsgListView.setSelection(mAdapter.getCount() - 1);
                mMsgDB.saveMsg(mSpUtil.getUserId(), item);// 消息保存数据库
                mEtMsg.setText("");
                // ===发送消息到服务器
                Message msgItem = new Message(
                        MessageItem.MESSAGE_TYPE_TEXT,
                        System.currentTimeMillis(), msg, "", 0);
                if ("".equals(mSpUtil.getUserId())) {
                    T.show(context, "百度push id为空，不能发送消息,请到百度开发者官网生成新的push key，替换", 1);
                    return;
                }
                new SendMsgAsyncTask(mGson.toJson(msgItem), mSpUtil.getUserId())
                        .send();// push发送消息到服务器
                // ===保存近期的消息

                RecentItem recentItem = new RecentItem(
                        MessageItem.MESSAGE_TYPE_TEXT, mSpUtil.getUserId(),
                        defaultCount, defaulgUserName, msg, 0,
                        System.currentTimeMillis(), 0);
                mRecentDB.saveRecent(recentItem);
                break;
            case R.id.ib_chatmain_msg:
                //如果录音界面没显示，先显示,并隐藏输入框
                if(!mViewVoice.isShown()){
                    hideInput();
                    mViewVoice.setVisibility(View.VISIBLE);
                    mViewInput.setVisibility(View.GONE);
                }else{
                    mViewVoice.setVisibility(View.GONE);
                    mViewInput.setVisibility(View.VISIBLE);
                    mEtMsg.requestFocus();
                    mInputMethodManager.showSoftInput(mEtMsg, 0);
                }
                //语音切换时隐藏表情界面
                hideFaceAffic();
                break;
            case R.id.ib_chatmain_voice:
                //如果语音界面显示，则先隐藏语音界面，并显示输入法
                if(!mViewVoice.isShown()){
                    hideInput();
                    mViewVoice.setVisibility(View.VISIBLE);
                    mViewInput.setVisibility(View.GONE);
                }else{
                    mViewVoice.setVisibility(View.GONE);
                    mViewInput.setVisibility(View.VISIBLE);
                    mEtMsg.requestFocus();
                    mInputMethodManager.showSoftInput(mEtMsg, 0);
                }
               hideFaceAffic();
                break;
            case R.id.tv_chatmain_press_voice:
                //按住说话，弹出音量框
                break;
            case R.id.btn_chat_affix:
                hideInput();
                mllFace.setVisibility(View.GONE);
                //图片附件
                if(mLlAffix.isShown()){
                    mLlAffix.setVisibility(View.GONE);
                }else{
                    mLlAffix.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_chatmain_affix_take_picture:
                //拍照
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mTakePhotoFilePath = AlbumHelper.getHelper(context)
                        .getFileDiskCache()
                        + File.separator
                        + System.currentTimeMillis() + ".jpg";
                // mTakePhotoFilePath = getImageSavePath(String.valueOf(System
                // .currentTimeMillis()) + ".jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(mTakePhotoFilePath)));
                startActivityForResult(intent, CAMERA_WITH_DATA);
                mLlAffix.setVisibility(View.GONE);
                break;
            case R.id.tv_chatmain_affix_album:
                //相册
                if (albumList.size() < 1) {
                    Toast.makeText(context, "相册中没有图片",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                Intent mIntent = new Intent(context,
                        PickPhotoActivity.class);
                mIntent.putExtra(ConstantKeys.EXTRA_CHAT_USER_ID,
                        mSpUtil.getUserId());
                startActivityForResult(mIntent, ConstantKeys.ALBUM_BACK_DATA);
                (MainActivity.getMainActivity()).overridePendingTransition(
                        R.anim.zf_album_enter, R.anim.zf_stay);
                mLlAffix.setVisibility(View.GONE);

                scrollToBottomListItem();
                break;
        }

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()){
            case R.id.msg_listview:
                hideInput();
                hideFaceAffic();
                break;
            case R.id.msg_et:
                mInputMethodManager.showSoftInput(mEtMsg, 0);
                hideFaceAffic();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onRefresh() {
        MSGPAGERNUM++;
        List<MessageItem> msgList = initMsgData();
        int position = mAdapter.getCount();
        mAdapter.setmMsgList(msgList);
        mMsgListView.stopRefresh();
        mMsgListView.setSelection(mAdapter.getCount() - position - 1);
        L.i("MsgPagerNum = " + MSGPAGERNUM + ", adapter.getCount() = "
                + mAdapter.getCount());

    }




    @Override
    public void onLoadMore() {

    }



    @Override
    public void onResume() {
        super.onResume();
        mHomeWatcher = new HomeWatcher(context);
        mHomeWatcher.setOnHomePressedListener(this);
        mHomeWatcher.startWatch();
        PushMessageReceiver.ehList.add(this);
    }

    @Override
    public void onPause() {
        //隐藏键盘
        mInputMethodManager.hideSoftInputFromWindow(mEtMsg.getWindowToken(), 0);
        hideFaceAffic();
        super.onPause();
        mHomeWatcher.setOnHomePressedListener(null);
        mHomeWatcher.stopWatch();
        PushMessageReceiver.ehList.remove(this);
    }

    //初始化消息数据库消息
    private List<MessageItem> initMsgData() {
        List<MessageItem> list = mMsgDB.getMsg(mSpUtil.getUserId(), MSGPAGERNUM);
        List<MessageItem> msgList = new ArrayList<MessageItem>();// 消息对象数组
        if (list.size() > 0) {
            for (MessageItem entity : list) {
                if (entity.getName().equals("")) {
                    entity.setName(defaulgUserName);
                }
                if (entity.getHeadImg() < 0) {
                    entity.setHeadImg(defaultCount);
                }
                msgList.add(entity);
            }
        }
        return msgList;

    }


    //获取图片路径
    public static String getImageSavePath(String fileName) {

        if (TextUtils.isEmpty(fileName)) {
            return null;
        }

        final File folder = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator
                + "PngZaiFei-IM"
                + File.separator
                + "images");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder.getAbsolutePath() + File.separator + fileName;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("fff", "结果:" + resultCode);
        if (RESULT_OK != resultCode) {
            return;
        }
        switch (requestCode) {
            case CAMERA_WITH_DATA:
                hanlderTakePhotoData(data);
                break;

            default:
                break;
        }
    }

    private void hanlderTakePhotoData(Intent data) {
        // Bitmap bitmap = null;
        // if (data == null) {
        // bitmap = ImageTool.createImageThumbnail(mTakePhotoFilePath);
        // } else {
        // Bundle extras = data.getExtras();
        // bitmap = extras == null ? null : (Bitmap) extras.get("data");
        // }
        // if (bitmap == null) {
        // return;
        // }

        if (data == null) {
            // 新建bitmap
            Bitmap newBitmap = ImageTool
                    .createImageThumbnail(mTakePhotoFilePath);
        } else {
            // 生成bitmap
            Bundle extras = data.getExtras();
            Bitmap bitmap = extras == null ? null : (Bitmap) extras.get("data");
            if (bitmap == null) {
                return;
            }
        }

        // listview展示
        MessageItem item = new MessageItem(MessageItem.MESSAGE_TYPE_IMG,
                mSpUtil.getNick(), System.currentTimeMillis(),
                mTakePhotoFilePath, mSpUtil.getHeadIcon(), false, 0, 0);
        mAdapter.upDateMsg(item);

        // 保存到数据库中
        MessageItem messageItem = new MessageItem(MessageItem.MESSAGE_TYPE_IMG,
                mSpUtil.getNick(), System.currentTimeMillis(),
                mTakePhotoFilePath, mSpUtil.getHeadIcon(), false, 0, 0);
        mMsgDB.saveMsg(mSpUtil.getUserId(), messageItem);

        // 保存到最近数据库中
        RecentItem recentItem = new RecentItem(MessageItem.MESSAGE_TYPE_IMG,
                mSpUtil.getUserId(), mSpUtil.getHeadIcon(), mSpUtil.getNick(),
                mTakePhotoFilePath, 0, System.currentTimeMillis(), 0);
        mRecentDB.saveRecent(recentItem);
        // 发送push
        Message message = new Message(MessageItem.MESSAGE_TYPE_IMG,
                System.currentTimeMillis(), messageItem.getMessage(), "", 0);
        if ("".equals(mSpUtil.getUserId())) {
            Log.e("fff", "用户id为空4");
            return;
        }
        new SendMsgAsyncTask(mGson.toJson(message), mSpUtil.getUserId()).send();

    }


    //TODO 还要监听HOME键的动作
    @Override
    public void onHomePressed() {
        mApplication.showNotification();
    }

    @Override
    public void onHomeLongPressed() {

    }

    @Override
    public void onMessage(Message message) {
        android.os.Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
        handlerMsg.obj = message;
        handler.sendMessage(handlerMsg);
    }

    @Override
    public void onBind(String method, int errorCode, String content) {
        if (errorCode == 0) {// 如果绑定账号成功，由于第一次运行，给同一tag的人推送一条新人消息
            User u = new User(mSpUtil.getUserId(), mSpUtil.getChannelId(),
                    mSpUtil.getNick(), mSpUtil.getHeadIcon(), 0);
            mUserDB.addUser(u);// 把自己添加到数据库
            // com.way.bean.Message msgItem = new com.way.bean.Message(
            // System.currentTimeMillis(), " ", mSpUtil.getTag());
            // new SendMsgAsyncTask(mGson.toJson(msgItem), "").send();;
        }
    }

    @Override
    public void onNotify(String title, String content) {

    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        if (!isNetConnected)
            T.showShort(context, "网络连接已断开");
    }

    @Override
    public void onNewFriend(User u) {

    }

    public static MessageAdapter getMessageAdapter() {
        return mAdapter;
    }

}
