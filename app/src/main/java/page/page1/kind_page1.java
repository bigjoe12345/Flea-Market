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

public class kind_page1 extends AppCompatActivity {
    String TABLENAME = "iteminfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kind_page1);

        DatabaseHelper dbtest = new DatabaseHelper(this);
        final SQLiteDatabase db = dbtest.getWritableDatabase();
        ListView listView = findViewById(R.id.kind_list1);
        final List<Map<String, Object>> data = new ArrayList<>();

        Cursor cursor = db.query(TABLENAME, null, "kind=?", new String[]{getString(R.string.sport_item)}, null, null, "id DESC");
        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> item = new HashMap<>();
                int id = cursor.getInt(0);
                item.put("id", id);
                item.put("title", cursor.getString(2));
                item.put("kind", cursor.getString(3));
                item.put("info", cursor.getString(4));
                item.put("price", cursor.getString(5));

                byte[] imagedata = cursor.getBlob(6);
                String cacheKey = "list_item_" + id;
                Bitmap bitmap = BitmapCache.getInstance().getBitmapFromCache(cacheKey);
                if (bitmap == null || bitmap.isRecycled()) {
                    bitmap = decodeSampledBitmapFromByteArray(imagedata, 300, 300);
                    if (bitmap != null) BitmapCache.getInstance().addBitmapToCache(cacheKey, bitmap);
                }
                item.put("image", bitmap);

                data.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();

        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.listitem,
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

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(kind_page1.this, item_info.class);
            intent.putExtra("id", data.get(position).get("id").toString());
            startActivity(intent);
        });

        setupRadioButtons();
    }

    private void setupRadioButtons() {
        findViewById(R.id.button_1).setOnClickListener(v -> startActivity(new Intent(this, main_page.class)));
        findViewById(R.id.button_2).setOnClickListener(v -> startActivity(new Intent(this, AddItem.class)));
        findViewById(R.id.button_3).setOnClickListener(v -> startActivity(new Intent(this, MyselfActivity.class)));
    }

    // 共用方法
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