package page.page1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static page.page1.LoginMainActivity.post_userid;

public class item_info extends AppCompatActivity {
    String TABLENAME = "iteminfo";
    byte[] imagedata;
    Bitmap imagebm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_info);
        final DatabaseHelper dbtest = new DatabaseHelper(this);
        final Intent intent = getIntent();
        final SQLiteDatabase db = dbtest.getWritableDatabase();
        ImageView image = (ImageView)findViewById(R.id.imageView);
        TextView price = (TextView)findViewById(R.id.item_price);
        TextView title = (TextView)findViewById(R.id.item_title) ;
        TextView info = (TextView)findViewById(R.id.item_info);
        TextView contact = (TextView)findViewById(R.id.contact);

        String itemId = intent.getStringExtra("id");
        String cacheKey = "item_detail_" + itemId;

        Cursor cursor = db.query(TABLENAME,null,"id=?",new String[]{itemId},null,null,null,null);
        Log.i(getString(R.string.commodity_id), itemId);

        if (cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                imagedata = cursor.getBlob(6);


                imagebm = BitmapCache.getInstance().getBitmapFromCache(cacheKey);
                if (imagebm == null || imagebm.isRecycled()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeByteArray(imagedata, 0, imagedata.length, options);


                    options.inSampleSize = calculateInSampleSize(options, 800, 800);
                    options.inJustDecodeBounds = false;
                    options.inMutable = true;

                    try {
                        imagebm = BitmapFactory.decodeByteArray(imagedata, 0, imagedata.length, options);
                        if (imagebm != null) {
                            BitmapCache.getInstance().addBitmapToCache(cacheKey, imagebm);
                        }
                    } catch (Exception e) {
                        options.inBitmap = null;
                        imagebm = BitmapFactory.decodeByteArray(imagedata, 0, imagedata.length, options);
                        if (imagebm != null) {
                            BitmapCache.getInstance().addBitmapToCache(cacheKey, imagebm);
                        }
                    }
                }

                image.setImageBitmap(imagebm);
                title.setText(cursor.getString(2));
                price.setText(cursor.getString(5) + "  HKD");
                info.setText(cursor.getString(4));
                contact.setText(cursor.getString(8));
                cursor.moveToNext();
            }
        }
        cursor.close();


        ListView commentList = (ListView)findViewById(R.id.commentList);
        final List<Map<String, Object>> data = new ArrayList<>();
        Cursor cursor_ = db.query("comments",null,"itemId=?",new String[]{itemId},null,null,null,null);
        if (cursor_.moveToFirst()){
            while (!cursor_.isAfterLast()){
                Map<String, Object> item = new HashMap<>();
                item.put("userId", cursor_.getString(0));
                item.put("comment", cursor_.getString(2));
                item.put("time", cursor_.getString(3));
                cursor_.moveToNext();
                data.add(item);
            }
        }
        cursor_.close();

        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, data, R.layout.comment_item,
                new String[] { "userId", "comment", "time"},
                new int[] { R.id.userId, R.id.commentInfo, R.id.time });
        commentList.setAdapter(simpleAdapter);

        Button submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(v -> {
            EditText comment = (EditText)findViewById(R.id.comment);
            String submit_comment = comment.getText().toString().trim();

            if (submit_comment.isEmpty()) {
                Toast.makeText(getApplicationContext(), R.string.comment_null, Toast.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
            String time = formatter.format(new Date(System.currentTimeMillis()));

            ContentValues values = new ContentValues();
            values.put("userId", post_userid);
            values.put("itemId", intent.getStringExtra("id"));
            values.put("comment", submit_comment);
            values.put("time", time);
            db.insert("comments", null, values);

            Toast.makeText(getApplicationContext(), getString(R.string.comment_success), Toast.LENGTH_SHORT).show();

            Map<String, Object> newCommentItem = new HashMap<>();
            newCommentItem.put("userId", post_userid);
            newCommentItem.put("comment", submit_comment);
            newCommentItem.put("time", time);
            data.add(newCommentItem);
            simpleAdapter.notifyDataSetChanged();
            comment.setText("");
        });
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}