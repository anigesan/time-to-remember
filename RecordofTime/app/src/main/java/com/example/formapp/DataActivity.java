package com.example.formapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("depreciation")
public class DataActivity extends AppCompatActivity implements LocationListener {
    EditText inTitle, inNote;
    Button locationButton, cameraButton, deleteDataButton;
    TextView locationRes;
    LocationManager locationManager;
    ImageView displayPhoto;
    private Data selectedData;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    public static final int CAMERA_PERM_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        initWidgets();
        checkForEditData();

        locationButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(DataActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DataActivity.this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 100);
            }
            getLocation();
        });
        cameraButton.setOnClickListener(v -> {
            if(ContextCompat.checkSelfPermission(DataActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(DataActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
            } else{
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(camera);
            }
        });
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Bundle bundle = null;
                if (result.getData() != null) {
                    bundle = result.getData().getExtras();
                }
                Bitmap image = null;
                if (bundle != null) {
                    image = (Bitmap) bundle.get("data");
                }
                displayPhoto.setImageBitmap(image);
            }
        });
    }
    private void initWidgets() {
        inTitle = findViewById(R.id.inTitle);
        inNote = findViewById(R.id.inNote);
        locationRes = findViewById(R.id.locationResult);
        locationButton = findViewById(R.id.getLocationButton);
        displayPhoto = findViewById(R.id.displayPhoto);
        cameraButton = findViewById(R.id.cameraButton);
        deleteDataButton = findViewById(R.id.deleteDataButton);
    }

    private void checkForEditData() {
        Intent previousIntent = getIntent();
        int passedDataID = previousIntent.getIntExtra(Data.DATA_EDIT_EXTRA, -1);
        selectedData = Data.getDataForID(passedDataID);

        if (selectedData != null)
        {
            inTitle.setText(selectedData.getTitle());
            inNote.setText(selectedData.getNote());
            locationRes.setText(selectedData.getLocation());
            byte[] photo = selectedData.getImage();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(photo);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            displayPhoto.setImageBitmap(bitmap);
        } else{
            deleteDataButton.setVisibility(View.INVISIBLE);
        }
    }
    public void saveData(View view) {
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        String title = String.valueOf(inTitle.getText());
        String note = String.valueOf(inNote.getText());
        String location = String.valueOf(locationRes.getText());
        byte[] image = getImageByte(displayPhoto);

        if(selectedData == null){
            int id = Data.dataArrayList.size();
            Data newData = new Data(id, title, note, location, image);
            Data.dataArrayList.add(newData);
            sqLiteManager.addDataToDatabase(newData);
        } else{
            selectedData.setTitle(title);
            selectedData.setNote(note);
            selectedData.setLocation(location);
            selectedData.setImage(image);
            sqLiteManager.updateDataInDB(selectedData);
        }
        finish();
    }
    public void deleteData(View view) {
        selectedData.setDeleted(new Date());
        SQLiteManager sqLiteManager = SQLiteManager.instanceOfDatabase(this);
        sqLiteManager.updateDataInDB(selectedData);
        finish();
    }
    public byte[] getImageByte(ImageView displayPic) {
        Bitmap bitmap = ((BitmapDrawable) displayPic.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
    }
    public void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, DataActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this, "" + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
        try {
            Geocoder geocoder = new Geocoder(DataActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            locationRes.setText(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }
    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }
}