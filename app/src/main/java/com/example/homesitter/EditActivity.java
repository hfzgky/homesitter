package com.example.homesitter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import gun0912.tedbottompicker.TedBottomPicker;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class EditActivity<Disposable> extends AppCompatActivity {
    private EditText mEditName;
//    private ImageButton mImagePerson;
    private int mItem = -1; //인덱스
    private Button mButton;
    ImageView btnSave;
//    ImageButton imagePerson;

    private static final String TAG = "MultiImageActivity";
    ArrayList<Uri> uriList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체
    RecyclerView recyclerView;  // 이미지를 보여줄 리사이클러뷰
    MultiImageAdapter adapter;  // 리사이클러뷰에 적용시킬 어댑터


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



    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mButton = findViewById(R.id.buttonGall);
        mEditName = findViewById(R.id.editTextName);
//        mImagePerson = findViewById(R.id.imagePerson);

        FirebaseApp.initializeApp(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        iv_image = findViewById(R.id.iv_image);
        mSelectedImagesContainer = findViewById(R.id.selected_photos_container);
        requestManager = Glide.with(this);
        String name = mEditName.getText().toString();
        formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");

        Intent intent = getIntent();    //intent객체를 가져옴
        if (intent != null) {    //추가
            mItem = intent.getIntExtra("item", -1);

            if (mItem != -1) {   //수정
                //값을 가져와서 보여줌
                mEditName.setText(intent.getStringExtra("name"));
            }
        }

        //이미지를 클릭하면 갤러리에서 이미지를 가져옴
//        imagePerson = findViewById(R.id.imagePerson); // 이미지 객체를 얻어옴
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

        //취소버튼
        ImageButton btnBack;
        btnBack = findViewById(R.id.buttonBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //저장버튼
        btnSave = findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sName = mEditName.getText().toString();
                if(sName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                    finish();
                }

                if (selectedUriList!= null&&mEditName!=null) {
                    final ProgressDialog progressDialog = new ProgressDialog(EditActivity.this);
                    progressDialog.setTitle("업로드 중");
                    progressDialog.show();
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef;
//                    String sName = mEditName.getText().toString();
                    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                    DatabaseReference childreference=firebaseDatabase.getReference().child("cctv/PhotoLink/"+name);
                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.child("UpdateCount").getValue()!=null) {
                                UpdateCount = dataSnapshot.child("UpdateCount").getValue(Integer.class);
                                firebaseDatabase.getReference().child("cctv/PhotoLink/" + name + "/" + "UpdateCount").setValue(UpdateCount + i - 1);
                            }else{
                                DownCount=0;
                                UpdateCount=0;
                                oneface=0;
                                //UpdateCount = dataSnapshot.getValue(Integer.class);
                                System.out.println("UpdateCount:"+UpdateCount);
                                firebaseDatabase.getReference().child("cctv/PhotoLink/"+name+"/"+"UpdateCount").setValue(UpdateCount+i-1);
                                firebaseDatabase.getReference().child("cctv/PhotoLink/"+name+"/"+"DownCount").setValue(DownCount);
                                firebaseDatabase.getReference().child("cctv/PhotoLink/"+name+"/"+"oneface").setValue(oneface);

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
                        //FirebaseStorage storage = FirebaseStorage.getInstance();
                        //Unique한 파일명을 만들자.

                        Date now = new Date();
                        filename = formatter.format(now) + "_" + i + ".png";
                        urlname = formatter.format(now) + "_" + i;

                        //storage 주소와 폴더 파일명을 지정해 준다.
                        /*storageRef = storage.getReferenceFromUrl("gs://aicctv-8f5ac.appspot.com").child("/00gpwls00/Photo/"+name+"/"+filename);*/
                        storageRef = storage.getReferenceFromUrl("gs://homesitter-54d69.appspot.com").child("/cctv/Photo/"+name+"/"+filename);

                        storageRef.putFile(uri)
                                //성공시
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
                                                firebaseDatabase.getReference().child("/cctv/PhotoLink/"+name+"/"+urlname).setValue(imgurl);

                                            }
                                        }).toString();

                                    }

                                })
                                //실패시
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                //진행중
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        @SuppressWarnings("VisibleForTests") //
                                        double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        //dialog에 진행률을 퍼센트로 출력해 준다
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


/*            public void onClick(View v) {
                String sName = mEditName.getText().toString().trim();

                if(sName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra("item", mItem);
                intent.putExtra("name", sName);
                setResult(RESULT_OK, intent);
                finish();
            }

 */

    }

    @SuppressLint("UseCheckPermission")
    private void setMultiShowButton() {

        Button btnMultiShow = findViewById(R.id.buttonGall);
        btnMultiShow.setOnClickListener(view -> {

            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    TedBottomPicker.with(EditActivity.this)
                            //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
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
            //startCrop(first);

        }
    };



/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2222) {
            if(data == null){   // 어떤 이미지도 선택하지 않은 경우
                Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                //이걸 스낵바로 바꾸고 싶은데... 어케해야돼....개빢쳐

            }
            else{   // 이미지를 하나라도 선택한 경우
                if(data.getClipData() == null){     // 이미지를 하나만 선택한 경우
                    Log.e("single choice: ", String.valueOf(data.getData()));
                    Uri imageUri = data.getData();
                    uriList.add(imageUri);

                    adapter = new MultiImageAdapter(uriList, getApplicationContext());
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
                }
                else{      // 이미지를 여러장 선택한 경우
                    ClipData clipData = data.getClipData();
                    Log.e("clipData", String.valueOf(clipData.getItemCount()));

                    if(clipData.getItemCount() > 11){   // 선택한 이미지가 11장 이상인 경우
                        Toast.makeText(getApplicationContext(), "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                    }
                    else {   // 선택한 이미지가 1장 이상 10장 이하인 경우
                        Log.e(TAG, "multiple choice");

                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                            try {
                                uriList.add(imageUri);  //uri를 list에 담는다.

                            } catch (Exception e) {
                                Log.e(TAG, "File select error", e);
                            }
                        }

                        adapter = new MultiImageAdapter(uriList, getApplicationContext());
                        recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));     // 리사이클러뷰 수평 스크롤 적용
                    }
                }
            }
        }
    }

 */
}