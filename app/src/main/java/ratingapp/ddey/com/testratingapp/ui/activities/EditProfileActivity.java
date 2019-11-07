package ratingapp.ddey.com.testratingapp.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.utils.others.PhotoUtils;
import ratingapp.ddey.com.testratingapp.utils.others.async.AsyncTaskListener;
import ratingapp.ddey.com.testratingapp.utils.others.async.ImageSendingAsync;
import ratingapp.ddey.com.testratingapp.utils.others.async.ImageSendingWrapper;
import ratingapp.ddey.com.testratingapp.utils.remote.ConnectionStatus;
import ratingapp.ddey.com.testratingapp.utils.database.DatabaseHelper;
import ratingapp.ddey.com.testratingapp.utils.database.FirebaseHelper;
import ratingapp.ddey.com.testratingapp.utils.others.Session;
import ratingapp.ddey.com.testratingapp.models.User;
import ratingapp.ddey.com.testratingapp.utils.others.Constants;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";
    protected static final int REQUEST_CHOOSE_PHOTO_GALLERY = 3;
    protected static final int REQUEST_TAKE_PHOTO = 2;
    private static final int REQUEST_PERMISSIONS = 12;
    private static final String STORAGE_PATH = "images/";


    private TextInputEditText tieEmail;
    private TextInputEditText tieBirthdate;
    private TextInputEditText tiePassword;
    private TextInputEditText tieConfirmPassword;
    private TextInputEditText tieOldPassword;

    private RadioButton profile_radioButtonM;
    private RadioButton profile_radioButtonF;

    private Spinner spnCountries;
    private Spinner spnFamilyType;

    private CircleImageView ivProfile;
    private Switch swPrivacy;
    private TextView tvPrivacy;

    private FirebaseUser user;
    private ProgressBar progressBar;

    private Session mSession;
    private DatabaseHelper mDb;
    private FirebaseHelper mFirebaseHelper;
    private User mUser;
    private String storageImageToken;

    private String currentPhotoPath = null;
    protected boolean hasFileAccess;
    protected boolean hasCameraAccess;
    protected Uri selectedImage;

    private Target targetCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        this.setTitle("Edit profile");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeComponents();
    }

    public void initializeComponents() {
        mSession = new Session(this);
        mDb = new DatabaseHelper(this);
        mFirebaseHelper = FirebaseHelper.getInstance();
        mFirebaseHelper.openConnection();
        progressBar = findViewById(R.id.profile_progressBar);
        ivProfile = findViewById(R.id.edit_profile_iv_profile_picture);

        tieEmail = findViewById(R.id.profile_tie_email);
        tieEmail.setFocusable(false);
        tieBirthdate = findViewById(R.id.profile_tie_date_of_birth);

        tiePassword = findViewById(R.id.edit_tie_password);
        tieConfirmPassword = findViewById(R.id.edit_tie_confirm_password);
        tieOldPassword = findViewById(R.id.tie_old_password);

        profile_radioButtonM = findViewById(R.id.radioButtonM);
        profile_radioButtonF = findViewById(R.id.radioButtonF);

        spnCountries = findViewById(R.id.profile_spinner_country);
        spnFamilyType = findViewById(R.id.profile_spinner_family);

        swPrivacy = findViewById(R.id.editprofile_switch_privacy);
        tvPrivacy = findViewById(R.id.editprofile_tv_privacy);

        profile_radioButtonM.setChecked(true);

        ArrayAdapter<CharSequence> adapterCountry = ArrayAdapter.createFromResource(getApplicationContext(), R.array.countries_array, R.layout.support_simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> adapterFamily = ArrayAdapter.createFromResource(getApplicationContext(), R.array.family_array, R.layout.support_simple_spinner_dropdown_item);

        spnCountries.setAdapter(adapterCountry);
        spnFamilyType.setAdapter(adapterFamily);

        Button buttonSave = findViewById(R.id.profile_buttonSave);
        buttonSave.setOnClickListener(saveEvent());

        swPrivacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tvPrivacy.setText("Public");
                    if (mUser != null) {
                        mUser.setProfilePublic(true);
                    }
                } else {
                    tvPrivacy.setText("Private");
                    if (mUser != null) {
                        mUser.setProfilePublic(false);
                    }
                }
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        loadUsers();
        initTargetCallback();
    }

    private void takePhoto() {
        if (!haveAllPermissions()) {
            requestMissingPermissions();
            return;
        }
        showChoosePictureDialog();
    }

    protected void showChoosePictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle(getString(R.string.select_action));
        String[] pictureDialogItems = {
                getString(R.string.select_photo_gallery),
                getString(R.string.capture_photo_camera)};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    protected void choosePhotoFromGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_CHOOSE_PHOTO_GALLERY);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    protected void takePhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                        "ratingapp.ddey.com.testratingapp.FileProvider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }
        }
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
    }


    protected boolean haveAllPermissions() {
        updatePermissions();
        return hasFileAccess && hasCameraAccess;
    }

    private void updatePermissions() {
        hasFileAccess = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        hasCameraAccess = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    protected void requestMissingPermissions() {
        List<String> request = new ArrayList<>();
        if (!hasFileAccess) {
            request.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            request.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!hasCameraAccess) {
            request.add(Manifest.permission.CAMERA);
        }

        this.requestPermissions(
                request.toArray(new String[0]),
                REQUEST_PERMISSIONS
        );
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (haveAllPermissions()) {
            showChoosePictureDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    onCameraResult();
                    break;
                case REQUEST_CHOOSE_PHOTO_GALLERY:
                    onGalleryResult(data);
                    break;
                default:
                    break;
            }
        }
    }

    private void onGalleryResult(Intent data) {
        selectedImage = data.getData();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            if (bitmap != null) {
                ivProfile.setImageBitmap(bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onCameraResult() {
        ImageSendingAsync asyncImageSending = new ImageSendingAsync();
        asyncImageSending.setListenerReference(new AsyncTaskListener<Bitmap>() {
            @Override
            public void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    selectedImage = PhotoUtils.getImageUri(getApplicationContext(), bitmap);
                    if (selectedImage != null) {
                        ivProfile.setImageBitmap(bitmap);
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        asyncImageSending.execute(new ImageSendingWrapper(currentPhotoPath, width, height));
    }

    public String getImageExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // getDownloadUrl era deprecated -> solutie: https://stackoverflow.com/questions/50467814/tasksnapshot-getdownloadurl-is-deprecated
    public void uploadImageToStorage() {
        if (selectedImage != null) {
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference ref;
            if (mUser.getStorageImageToken() != null) {
                ref = mFirebaseHelper.getStorage()
                        .getReference()
                        .child(STORAGE_PATH + mUser.getStorageImageToken() + "." + getImageExtension(selectedImage));
                ref.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: deleted the file for future replacement");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: failed to delete the file");
                    }
                });
                storageImageToken = mUser.getStorageImageToken();
            } else {
                UUID nr = UUID.randomUUID();
                storageImageToken = nr.toString();
                ref = mFirebaseHelper.getStorage()
                        .getReference()
                        .child(STORAGE_PATH + nr + "." + getImageExtension(selectedImage));
            }

            UploadTask uploadTask = ref.putFile(selectedImage);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String downloadURL = downloadUri.toString();
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Data uploaded", Toast.LENGTH_SHORT).show();
                        mUser.setStorageImageToken(storageImageToken);
                        mUser.setProfilePictureURL(downloadURL);
                        mFirebaseHelper.getUsersReference().child(mUser.getFirebaseToken()).setValue(mUser);
                        returnResultOKIntent();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            returnResultOKIntent();
        }
    }

    public View.OnClickListener saveEvent() {

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    if (verifyConnection()) {
                        updateUser();
                    } else {
                        Toast.makeText(getApplicationContext(), "Lost connection! Retry with a working internet connection!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            returnResultCanceledIntent();
        }

        return super.onOptionsItemSelected(item);
    }


    private void updateUser() {
        if (isValid()) {
            User localUser = mDb.retrieveUser(mSession);
            mUser.setId(localUser.getId());
            mUser.setEmail(tieEmail.getText().toString());
            try {
                mUser.setBirthDate(Constants.simpleDateFormat.parse(tieBirthdate.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (profile_radioButtonM.isChecked())
                mUser.setGender("Male");
            else if (profile_radioButtonF.isChecked())
                mUser.setGender("Female");
            mUser.setCountry(spnCountries.getSelectedItem().toString());
            mUser.setTravelGroup(spnFamilyType.getSelectedItem().toString());
            if (!tiePassword.getText().toString().trim().isEmpty() && tiePassword.getText() != null)
                mUser.setPassword(tiePassword.getText().toString());
            if (swPrivacy.isChecked())
                mUser.setProfilePublic(true);
            else
                mUser.setProfilePublic(false);
            mDb.updateUser(mUser);
            updateFirebaseUserInformation(mUser);
        }
    }

    private void updateFirebaseUserInformation(final User user) {
        if (user != null) {
            progressBar.setVisibility(View.VISIBLE);
            mFirebaseHelper.openConnection();
            mFirebaseHelper.getUsersReference().child(user.getFirebaseToken()).setValue(user);
            mFirebaseHelper.getUsersReference().child(user.getFirebaseToken()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User temp = dataSnapshot.getValue(User.class);
                    if (temp != null) {
                        Log.i("FireBaseUserUpdate", "User is updated " + temp.toString());
                        updateFirebaseAuthInformation(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("FireBaseUserUpdate: ", "User is not updated");
                }
            });
        }
    }

    public void updateFirebaseAuthInformation(final User updatedUser) {
        if (!tieOldPassword.getText().toString().trim().isEmpty() && tieOldPassword.getText() != null) {
            String oldPassword = tieOldPassword.getText().toString();
            progressBar.setVisibility(View.VISIBLE);
            mFirebaseHelper.openConnection();
            user = mFirebaseHelper.getAuth().getCurrentUser();
            String email = user.getEmail();

            AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(updatedUser.getPassword()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mFirebaseHelper.getUsersReference()
                                            .child(mFirebaseHelper.getAuth().getCurrentUser().getUid())
                                            .setValue(updatedUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mDb.updateUser(updatedUser);
                                            }
                                        }
                                    });
                                    Toast.makeText(getApplicationContext(), "Account modified", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Something went wrong. Please try again later", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Old password incorrect", Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            Log.d("FirebaseAuthUpdate: ", "Insert old password if you want to change your Auth data");
        }
        uploadImageToStorage();
    }

    public void returnResultOKIntent() {
        Intent returnIntent = getIntent();
        if (returnIntent != null) {
            setResult(RESULT_OK, returnIntent);
            this.finish();
        }
    }

    public void returnResultCanceledIntent() {
        Intent returnIntent = getIntent();
        if (returnIntent != null) {
            setResult(RESULT_CANCELED, returnIntent);
            this.finish();
        }
    }

    public void loadUsers() {
        if (verifyConnection()) {
            loadFromFirebase();
        } else {
            loadFromLocal();
        }
    }

    public void loadFromFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        mFirebaseHelper.openConnection();
        mFirebaseHelper.getUsersReference().child(mFirebaseHelper.getAuth().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                loadUserData(user);
                mUser = user;
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadFromLocal() {
        User user = mDb.retrieveUser(mSession);
        loadUserData(user);
        progressBar.setVisibility(View.GONE);
        mUser = user;
    }

    private void loadUserData(User user) {
        if (user != null) {
            if (user.getEmail() != null) {
                tieEmail.setText(user.getEmail());
            }
            if (user.getBirthDate() != null) {
                tieBirthdate.setText(Constants.simpleDateFormat.format(user.getBirthDate()));
            }
            if (user.getGender() != null) {
                if (user.getGender().equals("Male"))
                    profile_radioButtonM.setChecked(true);
                else
                    profile_radioButtonF.setChecked(true);
            }
            if (user.getCountry() != null) {
                selectSpinnerChoice(user.getCountry(), spnCountries);
            }
            if (user.getTravelGroup() != null) {
                selectSpinnerChoice(user.getTravelGroup(), spnFamilyType);
            }

            if (user.isProfilePublic()) {
                swPrivacy.setChecked(true);
                tvPrivacy.setText("Public");

            } else {
                swPrivacy.setChecked(false);
                tvPrivacy.setText("Private");
            }

            if (user.getProfilePictureURL() != null) {
                progressBar.setVisibility(View.VISIBLE);
                ivProfile.setBorderWidth(1);
                Picasso.with(this)
                        .load(user.getProfilePictureURL())
                        .placeholder(R.drawable.ic_profile_120)
                        .error(R.drawable.ic_error_120)
                        .into(targetCallback);
            } else {
                ivProfile.setImageResource(R.drawable.ic_profile_120);
                progressBar.setVisibility(View.GONE);
                ivProfile.setBorderWidth(0);
            }
        }
    }

    private void initTargetCallback() {
        targetCallback = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                ivProfile.setImageBitmap(bitmap);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                ivProfile.setBorderWidth(1);
                ivProfile.setImageResource(R.drawable.ic_profile_120);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
    }

    private void selectSpinnerChoice(String selection, Spinner currentSpinner) {
        Adapter adapter = currentSpinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equals(selection)) {
                currentSpinner.setSelection(i);
                break;
            }
        }
    }

    public boolean isValid() {
        if (tieEmail.getText() == null || tieEmail.getText().toString().trim().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(tieEmail.getText().toString()).matches()) {
            tieEmail.setError(getString(R.string.profile_activity_email_error));
            return false;
        } else if (tiePassword.getText() != null && !tiePassword.getText().toString().trim().isEmpty()) {
            return isValidPasswordData();
        }
        return true;
    }

    public boolean isValidPasswordData() {
        if (tiePassword.getText().toString().length() < 6) {
            tiePassword.setError("Password must have at least 6 digits!");
            tiePassword.requestFocus();
            return false;
        } else if (!tiePassword.getText().toString().equals(tieConfirmPassword.getText().toString())) {
            tieConfirmPassword.setError("Passwords must match!");
            tieConfirmPassword.requestFocus();
            return false;
        }
        if (!tieOldPassword.getText().toString().equals(mSession.getPrefs().getString(Session.KEY_PASS, null))) {
            tieOldPassword.setError(" Password doesn't match with the current one");
            tieOldPassword.requestFocus();
            return false;
        }

        return true;
    }

    public boolean verifyConnection() {
        ConnectionStatus connection = ConnectionStatus.getInstance(getApplicationContext());
        return connection.isOnline();
    }
}
