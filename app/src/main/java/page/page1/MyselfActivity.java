package page.page1;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class MyselfActivity extends AppCompatActivity {

    private RadioButton button1;
    private RadioButton button2;
    private RadioButton button3;
    private Button myself;
    private Button myshow;
    private Button changepwd;
    private Button about;
    private Button login;
    private TextView myId;

    protected Intent intent;
    private String a;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myself);

        button1=(RadioButton)findViewById(R.id.button_1);
        button2=(RadioButton)findViewById(R.id.button_2);
        button3=(RadioButton)findViewById(R.id.button_3);
        myself=(Button)findViewById(R.id.myself);
        myshow=(Button)findViewById(R.id.myShow);
        changepwd=(Button)findViewById(R.id.changepwd);
        about=(Button)findViewById(R.id.about);
        login=(Button)findViewById(R.id.login) ;
        myId=(TextView)findViewById(R.id.myId);
        a=LoginMainActivity.post_userid;
        myId.setText(a);

        Log.i("123",a);
        if(a.equals("")||a==null){
            login.setText(getString(R.string.Login_Now));
        }else{
            login.setText(getString(R.string.LogOut));
        }

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MyselfActivity.this,main_page.class);
                startActivity(intent);
            }
        });


        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {if(a.equals("")||a==null){
                Toast.makeText(getApplicationContext(), getString(R.string.login_remind), Toast.LENGTH_SHORT).show();
                intent = new Intent(MyselfActivity.this,LoginMainActivity.class);
                startActivity(intent);
            }
                intent = new Intent(MyselfActivity.this,AddItem.class);
                startActivity(intent);
            }
        });


        myself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(a.equals("")){
                    Toast.makeText(getApplicationContext(), getString(R.string.login_remind), Toast.LENGTH_SHORT).show();
                    intent = new Intent(MyselfActivity.this,LoginMainActivity.class);
                    startActivity(intent);
                }
                Log.i("123","111111111");
                intent = new Intent(MyselfActivity.this,userMsgActivity.class);
                startActivity(intent);
            }
        });


        myshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(a.equals("")||a==null){
                    Toast.makeText(getApplicationContext(), getString(R.string.login_remind), Toast.LENGTH_SHORT).show();
                    intent = new Intent(MyselfActivity.this,LoginMainActivity.class);
                    startActivity(intent);
                }
                else {
                    intent = new Intent(MyselfActivity.this, MyItems.class);
                    startActivity(intent);
                }
            }
        });

        changepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(a.equals("")||a==null){
                    Toast.makeText(getApplicationContext(), getString(R.string.login_remind), Toast.LENGTH_SHORT).show();
                    intent = new Intent(MyselfActivity.this,LoginMainActivity.class);
                    startActivity(intent);
                }
                intent = new Intent(MyselfActivity.this,changepwdActivity.class);
                startActivity(intent);
            }
        });


        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MyselfActivity.this,AboutMainActivity.class);
                startActivity(intent);
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(a.equals("")||a==null){
                    intent = new Intent(MyselfActivity.this,LoginMainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
                    LoginMainActivity.post_userid="";
                    BitmapCache.getInstance().clearCache();
                    intent = new Intent(MyselfActivity.this,LoginMainActivity.class);
                    startActivity(intent);
                }
            }
        });

        Button languageSetting = findViewById(R.id.language_setting);


        languageSetting.setOnClickListener(v -> {
            if (a.equals("") || a == null) {

                Intent intent = new Intent(MyselfActivity.this, LanguageSettingActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MyselfActivity.this, LanguageSettingActivity.class);
                startActivity(intent);
            }
        });

    }
}
