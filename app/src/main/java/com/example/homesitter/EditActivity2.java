package com.example.homesitter;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;


public class EditActivity2<Disposable> extends AppCompatActivity {
    public EditText mEditName;
    private int mItem = -1;
    private ImageButton mButton;
    ImageView btnSave;
    private static final String TAG = "MultiImageActivity";
    private int mISelectedID = 0;

    int i=0;
    int j=1;
    int k=0;
    int index;

    private ImageView iv_image;
    private List<Uri> selectedUriList;
    private Uri selectedUri;
    private Disposable singleImageDisposable;
    private Disposable multiImageDisposable;
    private ViewGroup mSelectedImagesContainer;
    private RequestManager requestManager;
    private StorageReference mStorageRef;
    private ImageView[] thumbnail;
    public static int UpdateCount;
    int DownCount,oneface;
    SimpleDateFormat formatter;
    private final String SAMPLE_CROPPED_IMAGE_NAME = "sample_image";
    private Button buttonDel;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference childreference10 = firebaseDatabase.getReference().child("cctv/PhotoLink/realname");
    DatabaseReference childreference100 = firebaseDatabase.getReference().child("cctv/PhotoLink/name");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRefe = storage.getReference().child("cctv/Photo/");

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mButton = findViewById(R.id.buttonGall);
        mEditName = findViewById(R.id.editTextName);

        FirebaseApp.initializeApp(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        iv_image = findViewById(R.id.iv_image);
        mSelectedImagesContainer = findViewById(R.id.selected_photos_container);
        requestManager = Glide.with(this);
        String name = mEditName.getText().toString();
        formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
      //  String origin=listlist.get(selectedPosition2);
        Intent intent = getIntent();

      //  mItem = intent.getIntExtra("item", -1);
        mISelectedID = intent.getIntExtra("id", 0);
        String rname = intent.getExtras().getString("name");/*String형*/
        mEditName.setText(rname);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2222);
            }
        });

        ImageButton btnBack;
        btnBack = findViewById(R.id.buttonBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSave = findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sName = mEditName.getText().toString();
                if(sName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(selectedUriList.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "사진을 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedUriList!= null&&mEditName!=null) {
                    final ProgressDialog progressDialog = new ProgressDialog(EditActivity2.this);
                    progressDialog.setTitle("업로드 중");
                    progressDialog.show();
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef;
                    storageRef = FirebaseStorage.getInstance().getReference();
                    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();

                    childreference100.child(rname).removeValue();
                    childreference10.child(rname).removeValue();

                    DatabaseReference childreference=firebaseDatabase.getReference().child("cctv/PhotoLink/name/"+sName);
                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.child("UpdateCount").getValue()!=null) {
                                UpdateCount = dataSnapshot.child("UpdateCount").getValue(Integer.class);

                                firebaseDatabase.getReference().child("cctv/PhotoLink/name/" + sName + "/" + "UpdateCount").setValue(UpdateCount + i - 1);
                                firebaseDatabase.getReference().child("cctv/PhotoLink/realname/"+sName).setValue(UpdateCount + i - 1);
                            }else{
                                DownCount=0;
                                UpdateCount=0;
                                oneface=0;
                                System.out.println("UpdateCount:"+UpdateCount);
                                firebaseDatabase.getReference().child("cctv/PhotoLink/name/"+sName+"/"+"UpdateCount").setValue(UpdateCount+i-1);
                                firebaseDatabase.getReference().child("cctv/PhotoLink/name/"+sName+"/"+"DownCount").setValue(DownCount);
                                firebaseDatabase.getReference().child("cctv/PhotoLink/name/"+sName+"/"+"oneface").setValue(oneface);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    childreference.addListenerForSingleValueEvent(valueEventListener);

                    i = 1;

                    for (Uri uri : selectedUriList) {
                        Log.d("pics", "" + uri);
                        String filename;
                        String urlname;

                        Date now = new Date();
                        urlname = formatter.format(now) + "_" + i;

                        final String imagedata = "/cctv/Photo/" + sName + "/" + i + ".png";
                        StorageReference data = storageRef.child(imagedata);

                        storageRef = storage.getReferenceFromUrl("gs://homesitter-54d69.appspot.com").child(imagedata);

                        storageRef.putFile(uri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Toast.makeText(getApplicationContext(), "업로드 완료!"+"("+j+"/"+selectedUriList.size()+")", Toast.LENGTH_SHORT).show();
                                        j++;

                                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Log.d("★★★★★★★★11111", uri.toString());
                                                String imgurl=uri.toString();
                                               // firebaseDatabase.getReference().child("/cctv/PhotoLink/name/"+sName+"/"+urlname).setValue(imgurl);
                                                firebaseDatabase.getReference().child("/cctv/PhotoLink/name/"+sName+"/"+urlname).setValue(imgurl);
                                                firebaseDatabase.getReference().child("/cctv/PhotoLink/realname/"+sName).setValue(imgurl);

                                            }
                                        }).toString();

                                    }

                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");

                                    }
                                });
                        i++;
                    }
                    progressDialog.dismiss();

                    Intent intent = new Intent();
                    intent.putExtra("name", sName);
                    setResult(RESULT_OK, intent);
                    finish();


                    for(int i = 1; i < 50; i++) {
                        // "cctv/Photo/각각의 폴더/~.png"에서 각각의 폴더에 몇 개의 사진이 있는지 몰라서 임의로 50까지 설정함.
                        // -> 이 값을 어떻게 얻어오지?
                        storageRefe.child(rname + "/" + i + ".png").delete();
                    }

                }
            }
        });
            setMultiShowButton();
    }

    @SuppressLint("UseCheckPermission")
    private void setMultiShowButton() {

        ImageButton btnMultiShow = findViewById(R.id.buttonGall);
        btnMultiShow.setOnClickListener(view -> {

            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    TedBottomPicker.with(EditActivity2.this)
                            .setPeekHeight(1600)
                            .showTitle(false)
                            .setCompleteButtonText("Done")
                            .setEmptySelectionText("No Select")
                            .setSelectedUriList(selectedUriList)
                            .showMultiImage(uriList -> {
                                selectedUriList = uriList;
                                showUriList(selectedUriList);
                            });
                }

                @Override
                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                    Toast.makeText(EditActivity2.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }
            };
            checkPermission(permissionlistener);
        });
    }

    private void checkPermission(PermissionListener permissionlistener) {
        TedPermission.with(EditActivity2.this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void showUriList(List<Uri> uriList) {

        mSelectedImagesContainer.removeAllViews();

        iv_image.setVisibility(View.GONE);
        mSelectedImagesContainer.setVisibility(View.VISIBLE);

        int widthPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        int heightPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

        thumbnail = new ImageView[70];
        k=0;
        for (Uri uri : uriList) {

            View imageHolder = LayoutInflater.from(this).inflate(R.layout.image_item, null);
            thumbnail[k] = imageHolder.findViewById(R.id.media_image);
            thumbnail[k].setId(k);
            thumbnail[k].setOnClickListener(PclickListener);


            requestManager
                    .load(uri.toString())
                    .apply(new RequestOptions().fitCenter())
                    .into(thumbnail[k]);

            mSelectedImagesContainer.addView(imageHolder);
            thumbnail[k].setLayoutParams(new FrameLayout.LayoutParams(widthPixel, heightPixel));
            k++;
        }

    }

    View.OnClickListener PclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            System.out.println(v.getId());
            index =v.getId();
            Uri first = selectedUriList.get(index);
        }
    };
}