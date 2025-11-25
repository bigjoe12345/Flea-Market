package page.page1;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static page.page1.LoginMainActivity.post_userid;

public class AddItem extends AppCompatActivity {
    private static final int REQUEST_PICK_IMAGE = 1;
    private ImageButton imageButton;
    private byte[] imageData;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_m1);


        findViewById(R.id.but1_m1).setOnClickListener(v -> {
            startActivity(new Intent(AddItem.this, main_page.class));
            finish();
        });

        findViewById(R.id.but2_m1).setOnClickListener(v -> {
            startActivity(new Intent(AddItem.this, MyItems.class));
            finish();
        });

        if (post_userid == null || post_userid.isEmpty()) {
            Toast.makeText(this, R.string.login_remind, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginMainActivity.class));
            finish();
            return;
        }


        String[] types = {
                getString(R.string.household_goods),
                getString(R.string.study_stuffs),
                getString(R.string.electronic_devices),
                getString(R.string.sport_item)
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = findViewById(R.id.m1_style);
        spinner.setAdapter(adapter);

        imageButton = findViewById(R.id.m1_image);
        imageButton.setOnClickListener(v -> checkPermissionAndPickImage());

        Button fabu = findViewById(R.id.fabu);
        fabu.setOnClickListener(v -> publishItem());
    }

    private void checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            Toast.makeText(this, R.string.authority_command, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 600, 600, true);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    scaled.compress(Bitmap.CompressFormat.JPEG, 85, baos);
                    imageData = baos.toByteArray();

                    imageButton.setImageBitmap(scaled);
                    Toast.makeText(this, R.string.select_success, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, R.string.select_fail, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void publishItem() {
        EditText titleEt = findViewById(R.id.m1_title);
        EditText priceEt = findViewById(R.id.m1_price);
        EditText phoneEt = findViewById(R.id.m1_phone);
        EditText infoEt = findViewById(R.id.m1_nr);

        String title = titleEt.getText().toString().trim();
        String price = priceEt.getText().toString().trim();
        String phone = phoneEt.getText().toString().trim();
        String info = infoEt.getText().toString().trim();
        String kind = spinner.getSelectedItem().toString();

        if (title.isEmpty() || price.isEmpty() || imageData == null) {
            Toast.makeText(this, R.string.Whole_mess, Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", post_userid);
        values.put("title", title);
        values.put("kind", kind);
        values.put("info", info);
        values.put("price", price);
        values.put("image", imageData);
        values.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        values.put("contact", phone);

        long result = db.insert("iteminfo", null, values);
        db.close();

        if (result != -1) {
            Toast.makeText(this, R.string.submit_success, Toast.LENGTH_LONG).show();


            Intent intent = new Intent(this, main_page.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);


            titleEt.setText("");
            priceEt.setText("");
            phoneEt.setText("");
            infoEt.setText("");
            imageButton.setImageResource(R.drawable.m1_sc);
            imageData = null;

            finish();
        } else {
            Toast.makeText(this, R.string.submit_failed, Toast.LENGTH_SHORT).show();
        }
    }
}