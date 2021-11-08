package com.example.homesitter;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Person;
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


public class EditActivity<Disposable> extends AppCompatActivity {
    public EditText mEditName;
    private int mItem = -1;
    private ImageButton mButton;
    ImageView btnSave;
    private static final String TAG = "MultiImageActivity";

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
    StorageReference storageRef = storage.getReference().child("cctv/Photo/");

    FriendActivity friend = new FriendActivity();
    private ArrayList<String> listlist = friend.getList();
    int selectedPosition2 = friend.getPosition();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mButton = findViewById(R.id.buttonGall);
        mEditName = findViewById(R.id.editTextName);
      //  buttonDel = findViewById(R.id.buttonDel);

        FirebaseApp.initializeApp(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        iv_image = findViewById(R.id.iv_image);
        mSelectedImagesContainer = findViewById(R.id.selected_photos_container);
        requestManager = Glide.with(this);
        String name = mEditName.getText().toString();
        formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");


        Intent intent = getIntent();
        if (intent != null) {
            mItem = intent.getIntExtra("item", -1);

            if (mItem != -1) {
                mEditName.setText(intent.getStringExtra("name"));
            }
        }


     /*   buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String origin=listlist.get(selectedPosition2);

                //선택되어 있는 항목 storage에서 모든 사진 제거
                childreference10.child(origin).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                            String key = messageData.getKey() + ".png";

                            //String file = String.valueOf(messageData.child("name").getValue()+".png");
                            storageRef.child(origin).child(key).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EditActivity.this, "선택 인물 삭제", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {            }
                });

                //선택되어 있는 항목 db에서 제거
                childreference100.child(origin).removeValue();
                childreference10.child(origin).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {                    }
                });

                finish();

            }


        });

      */


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
                    finish();
                }
                if(selectedUriList.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "사진을 선택해주세요", Toast.LENGTH_SHORT).show();
                    finish();
                }

                if (selectedUriList!= null&&mEditName!=null) {
                    final ProgressDialog progressDialog = new ProgressDialog(EditActivity.this);
                    progressDialog.setTitle("업로드 중");
                    progressDialog.show();
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef;
                    storageRef = FirebaseStorage.getInstance().getReference();
                    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
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
                    intent.putExtra("item", mItem);
                    intent.putExtra("name", sName);
                    setResult(RESULT_OK, intent);
                    finish();

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
                    TedBottomPicker.with(EditActivity.this)
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
                    Toast.makeText(EditActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }
            };
            checkPermission(permissionlistener);
        });
    }

    private void checkPermission(PermissionListener permissionlistener) {
        TedPermission.with(EditActivity.this)
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