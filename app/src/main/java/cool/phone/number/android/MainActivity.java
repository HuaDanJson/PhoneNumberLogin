package cool.phone.number.android;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.util.LogUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

/**
 * http://wiki.mob.com/sdk-sms-android-3-0-0/
 */

public class MainActivity extends AppCompatActivity {

    EventHandler eventHandler = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            // afterEvent会在子线程被调用，因此如果后续有UI相关操作，需要将数据发送到UI线程
            Message msg = new Message();
            msg.arg1 = event;
            msg.arg2 = result;
            msg.obj = data;
            new Handler(Looper.getMainLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    int event = msg.arg1;
                    int result = msg.arg2;
                    Object data = msg.obj;
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            // TODO 处理成功得到验证码的结果
                            // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                            LogUtils.d("MainActivity  000 处理成功得到验证码的结果");
                        } else {
                            // TODO 处理错误的结果
                            LogUtils.d("MainActivity  111 处理错误验证码的结果");
                            ((Throwable) data).printStackTrace();
                        }
                    } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            // TODO 处理验证码验证通过的结果
                            LogUtils.d("MainActivity  222 处理验证码验证通过的结果");
                        } else {
                            // TODO 处理错误的结果
                            LogUtils.d("MainActivity  333 处理错误的结果");
                            ((Throwable) data).printStackTrace();
                        }
                    }
                    // TODO 其他接口的返回结果也类似，根据event判断当前数据属于哪个接口
                    return false;
                }
            }).sendMessage(msg);
        }
    };

    @BindView(R.id.edt_phone_number_main_activity) EditText mEdtPhoneNumber;
    @BindView(R.id.edt_code_main_activity) EditText mEdtCode;
    @BindView(R.id.btn1_main_activity) Button btn1;
    @BindView(R.id.btn2_main_activity) Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sendCode(this);//第一种方法  用SDK提供的UI
//        phoneNumberLogin();//第二种方法  用自己的UI
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.btn1_main_activity)
    public void click1() {
        SMSSDK.getVerificationCode("86", mEdtPhoneNumber.getText().toString());
    }

    @OnClick(R.id.btn2_main_activity)
    public void click2() {
        SMSSDK.submitVerificationCode("86", mEdtPhoneNumber.getText().toString(), mEdtCode.getText().toString());
    }

    public void sendCode(Context context) {
        RegisterPage page = new RegisterPage();
        //如果使用我们的ui，没有申请模板编号的情况下需传null
        page.setTempCode(null);
        page.setRegisterCallback(new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 处理成功的结果
                    HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country"); // 国家代码，如“86”
                    String phone = (String) phoneMap.get("phone"); // 手机号码，如“13800138000”
                    // TODO 利用国家代码和手机号码进行后续的操作
                    LogUtils.d("MainActivity country = " + country);
                    LogUtils.d("MainActivity phone = " + phone);
                } else {
                    // TODO 处理错误的结果
                    LogUtils.d("MainActivity  // TODO 处理错误的结果  event = " + event + "   result = " + result + "  data = " + data);
                }
            }
        });
        page.show(context);
    }

    public void phoneNumberLogin() {
        // 在尝试读取通信录时以弹窗提示用户（可选功能）
        SMSSDK.setAskPermisionOnReadContact(true);

        // 注册一个事件回调，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eventHandler);

        // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
//        SMSSDK.getVerificationCode(country, phone);

        // 提交验证码，其中的code表示验证码，如“1357”

    }
}
