package page.page1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static page.page1.LoginMainActivity.post_userid;

public class MyItems extends AppCompatActivity implements View.OnClickListener {
    String TABLENAME = "iteminfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper database = new DatabaseHelper(this);
        final SQLiteDatabase db = database.getWritableDatabase();
        ListView listView = findViewById(R.id.show_fabu);
        final List<Map<String, Object>> data = new ArrayList<>();

        String currentUserId = post_userid;
        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(this, getString(R.string.login_remind), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginMainActivity.class));
            finish();
            return;
        }

        Cursor cursor = db.query(TABLENAME, null, "userId=?", new String[]{currentUserId}, null, null, "id DESC");

        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> item = new HashMap<>();
                int id = cursor.getInt(0);
                item.put("id", id);
                item.put("userid", cursor.getString(1));
                item.put("title", cursor.getString(2));
                item.put("kind", cursor.getString(3));
                item.put("info", cursor.getString(4));
                item.put("price", cursor.getString(5));

                byte[] imagedata = cursor.getBlob(6);
                String cacheKey = "myitem_" + id;


                Bitmap bitmap = BitmapCache.getInstance().getBitmapFromCache(cacheKey);
                if (bitmap == null || bitmap.isRecycled()) {
                    bitmap = decodeSampledBitmap(imagedata, 300, 300);
                    if (bitmap != null) {
                        BitmapCache.getInstance().addBitmapToCache(cacheKey, bitmap);
                    }
                }
                item.put("image", bitmap);
                data.add(item);
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, getString(R.string.pub_remind), Toast.LENGTH_SHORT).show();
        }
        cursor.close();

        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.activity_my_fabu,
                new String[]{"image", "title", "kind", "info", "price"},
                new int[]{R.id.item_image, R.id.title, R.id.kind, R.id.info, R.id.price});

        adapter.setViewBinder((view, dataObj, text) -> {
            if (view instanceof ImageView && dataObj instanceof Bitmap) {
                ((ImageView) view).setImageBitmap((Bitmap) dataObj);
                return true;
            }
            return false;
        });

        listView.setAdapter(adapter);


        Button button1 = findViewById(R.id.but1);
        button1.setOnClickListener(v -> {
            startActivity(new Intent(MyItems.this, main_page.class));

        });


        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MyItems.this, item_info.class);
            intent.putExtra("id", data.get(position).get("id").toString());
            startActivity(intent);
        });


        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            String delId = data.get(position).get("id").toString();
            if (db.delete(TABLENAME, "id=?", new String[]{delId}) > 0) {
                Toast.makeText(this, getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                recreate();
            } else {
                Toast.makeText(this, getString(R.string.delete_fail), Toast.LENGTH_SHORT).show();
            }
            return true;
        });


        RadioButton btn1 = findViewById(R.id.button_1);
        RadioButton btn2 = findViewById(R.id.button_2);
        RadioButton btn3 = findViewById(R.id.button_3);

        if (btn1 != null) btn1.setOnClickListener(this);
        if (btn2 != null) btn2.setOnClickListener(this);
        if (btn3 != null) btn3.setOnClickListener(this);


        if (btn2 != null) btn2.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_1:
                startActivity(new Intent(this, main_page.class));
                finish();
                break;
            case R.id.button_2:

                break;
            case R.id.button_3:
                startActivity(new Intent(this, MyselfActivity.class));
                finish();
                break;
        }
    }


    private Bitmap decodeSampledBitmap(byte[] data, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        options.inMutable = true;

        try {
            return BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } catch (Exception e) {
            options.inBitmap = null;
            return BitmapFactory.decodeByteArray(data, 0, data.length, options);
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}