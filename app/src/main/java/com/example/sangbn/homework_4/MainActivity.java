package com.example.sangbn.homework_4;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.ActivityCompat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ja.burhanrashid52.photoeditor.PhotoFilter;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "PermissionDemo";
    private static final int RECORD_REQUEST_CODE = 101;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static String mCurrentPhotoPath;
    public static int rotation = 0;

    GridView androidGridView;
    List<String> imageURL = new ArrayList<>();

    // your Final lat Long Values
    Float Latitude = 0.0f;
    Float Longitude = 0.0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button recordButton = (Button) findViewById(R.id.button);
        setupPermissions();
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            getImages();
        }
        if (!hasCamera()) {
            recordButton.setEnabled(false);
        }


        androidGridView = (GridView) findViewById(R.id.Grid);
        androidGridView.setAdapter(new ImageAdapterGridView(this));

        androidGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id) {
                Toast.makeText(getBaseContext(), "Grid Item " + (position + 1) + " Selected", Toast.LENGTH_LONG).show();
            }
        });


    }

    public class ImageAdapterGridView extends BaseAdapter {
        private Context mContext;

        public ImageAdapterGridView(Context c) {
            mContext = c;
        }

        public int getCount() {
            return imageURL.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView mImageView;

            if (convertView == null) {
                mImageView = new ImageView(mContext);
                mImageView.setLayoutParams(new GridView.LayoutParams(130, 130));
                mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mImageView.setPadding(16, 16, 16, 16);
            } else {
                mImageView = (ImageView) convertView;
            }


            System.out.println("Position: " + position);
            System.out.println("HMMMMMM " + imageURL.get(position));
//            mImageView.setImageResource(imageURL.get(position));
            mImageView.setImageURI(Uri.parse(imageURL.get(position)));
            return mImageView;
        }
    }

    private void getImages() {
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        System.out.println(MediaStore.Images.Media.DATA);
        System.out.println(MediaStore.Images.Media._ID);
        final String orderBy = MediaStore.Images.Media._ID;
        //Stores all the images from the gallery in Cursor
        Uri imageUri = Uri.parse("content://media/DCIM/images/media");
        Cursor cursor = getContentResolver().query(imageUri, columns, null,
                null, orderBy);
        System.out.println("HAHAHAHAHHA " + MediaStore.Images.Media.getContentUri("Musics"));
        //Total number of images
        int count = cursor.getCount();
        //Create an array to store path to all the images
        String[] arrPath = new String[count];

        for (int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            //Store the path of the image
            arrPath[i] = cursor.getString(dataColumnIndex);
            Log.i("PATH", arrPath[i]);
            imageURL.add(arrPath[i]);
            System.out.println(arrPath[i]);
        }
        // The cursor should be freed up after use with close()
        cursor.close();
    }

    public void onClick(View v) {
        dispatchTakePictureIntent();
    }

    private boolean hasCamera() {
        return (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY));
    }

    private void setupPermissions() {
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest();
        }
        //-----------------
        permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest();
        }
        //
        permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest();
        }
        //
        permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest();
        }
    }

    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                RECORD_REQUEST_CODE);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString());
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            getData(mCurrentPhotoPath);
            Intent i = new Intent(this, EditPhoto.class);
            i.putExtra("photoPath", mCurrentPhotoPath);
            i.putExtra("latitude", Latitude);
            i.putExtra("longitude", Longitude);
            startActivity(i);
            ImageView mImageView = (ImageView) findViewById(R.id.imageView);
            mImageView.setImageURI(Uri.parse(mCurrentPhotoPath));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RECORD_REQUEST_CODE: {
                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission has been denied by user");
                } else {
                    getImages();
                    Log.i(TAG, "Permission has been granted by user");
                }
            }
        }
    }

    private void getData(String uri) {
        InputStream in = null;
        try {
            in = getContentResolver().openInputStream(Uri.fromFile(new File(uri)));
            ExifInterface exifInterface = new ExifInterface(in);

            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
            }
            TextView textRotate = (TextView) findViewById(R.id.textRotate);
            textRotate.setText("Rolation: " + rotation);

            String LATITUDE = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String LATITUDE_REF = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String LONGITUDE = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String LONGITUDE_REF = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

            convert(LATITUDE, LONGITUDE, LATITUDE_REF, LONGITUDE_REF);

            TextView textLatitude = (TextView) findViewById(R.id.textLatitude);
            textLatitude.setText("Latitude: " + Latitude);
            TextView textLongitude = (TextView) findViewById(R.id.textLongitude);
            textLongitude.setText("Longitude: " + Longitude);

            // Now you can extract any Exif tag you want
            // Assuming the image is a JPEG or supported raw format
        } catch (IOException e) {
            // Handle any errors
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private void convert(String LATITUDE, String LONGITUDE, String LATITUDE_REF, String LONGITUDE_REF) {
        if ((LATITUDE != null)
                && (LATITUDE_REF != null)
                && (LONGITUDE != null)
                && (LONGITUDE_REF != null)) {
            if (LATITUDE_REF.equals("N")) {
                Latitude = convertToDegree(LATITUDE);
            } else {
                Latitude = 0 - convertToDegree(LATITUDE);
            }

            if (LONGITUDE_REF.equals("E")) {
                Longitude = convertToDegree(LONGITUDE);
            } else {
                Longitude = 0 - convertToDegree(LONGITUDE);
            }

        }
    }

    private Float convertToDegree(String stringDMS) {
        Float result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Float D0 = new Float(stringD[0]);
        Float D1 = new Float(stringD[1]);
        Float FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Float M0 = new Float(stringM[0]);
        Float M1 = new Float(stringM[1]);
        Float FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Float S0 = new Float(stringS[0]);
        Float S1 = new Float(stringS[1]);
        Float FloatS = S0 / S1;

        result = new Float(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;
    }


    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return (String.valueOf(Latitude)
                + ", "
                + String.valueOf(Longitude));
    }

    public int getLatitudeE6() {
        return (int) (Latitude * 1000000);
    }

    public int getLongitudeE6() {
        return (int) (Longitude * 1000000);
    }
}
