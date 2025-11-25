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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class main_page extends AppCompatActivity implements View.OnClickListener {

    String TABLENAME = "iteminfo";
    private ListView listView;
    private List<Map<String, Object>> data;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        listView = findViewById(R.id.listView);
        data = new ArrayList<>();

        // 初始化底部导航
        RadioButton btn1 = findViewById(R.id.button_1);
        RadioButton btn2 = findViewById(R.id.button_2);
        RadioButton btn3 = findViewById(R.id.button_3);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn1.setChecked(true);


        findViewById(R.id.kind1).setOnClickListener(v -> startActivity(new Intent(this, kind_page1.class)));
        findViewById(R.id.kind2).setOnClickListener(v -> startActivity(new Intent(this, kind_page2.class)));
        findViewById(R.id.kind3).setOnClickListener(v -> startActivity(new Intent(this, kind_page3.class)));
        findViewById(R.id.kind4).setOnClickListener(v -> startActivity(new Intent(this, kind_page4.class)));


        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(main_page.this, item_info.class);
            intent.putExtra("id", data.get(position).get("id").toString());
            startActivity(intent);
        });


        loadData();
    }


    private void loadData() {
        data.clear();

        DatabaseHelper database = new DatabaseHelper(this);
        SQLiteDatabase db = database.getWritableDatabase();

        Cursor cursor = db.query(TABLENAME, null, null, null, null, null, "id DESC");
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
                String cacheKey = "list_item_" + id;
                Bitmap bitmap = BitmapCache.getInstance().getBitmapFromCache(cacheKey);
                if (bitmap == null || bitmap.isRecycled()) {
                    bitmap = decodeSampledBitmapFromByteArray(imagedata, 300, 300);
                    if (bitmap != null) {
                        BitmapCache.getInstance().addBitmapToCache(cacheKey, bitmap);
                    }
                }
                item.put("image", bitmap != null ? bitmap : BitmapFactory.decodeByteArray(imagedata, 0, imagedata.length));
                data.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();


        if (adapter == null) {
            adapter = new SimpleAdapter(this, data, R.layout.listitem,
                    new String[]{"image", "title", "kind", "info", "price"},
                    new int[]{R.id.item_image, R.id.title, R.id.kind, R.id.info, R.id.price});

            adapter.setViewBinder((view, dataObj, textRepresentation) -> {
                if (view instanceof ImageView && dataObj instanceof Bitmap) {
                    ((ImageView) view).setImageBitmap((Bitmap) dataObj);
                    return true;
                }
                return false;
            });
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_1:
                break;
            case R.id.button_2:
                startActivity(new Intent(this, AddItem.class));
                break;
            case R.id.button_3:
                startActivity(new Intent(this, MyselfActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
    }

    private Bitmap decodeSampledBitmapFromByteArray(byte[] data, int reqWidth, int reqHeight) {
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