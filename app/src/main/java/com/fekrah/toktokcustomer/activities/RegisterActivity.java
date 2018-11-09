package com.fekrah.toktokcustomer.activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.fekrah.toktokcustomer.R;
import com.fekrah.toktokcustomer.helper.Utility;
import com.fekrah.toktokcustomer.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.phonenumberui.PhoneNumberActivity;
import com.rafakob.drawme.DrawMeButton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.name)
    EditText name;

    @BindView(R.id.email)
    EditText email;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.phone)
    EditText phone;

    @BindView(R.id.register)
    DrawMeButton registerBtn;

    @BindView(R.id.logIn)
    DrawMeButton logIn;

    private int APP_REQUEST_CODE = 010;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ViewPump.init(ViewPump.builder()
//                .addInterceptor(new CalligraphyInterceptor(
//                        new CalligraphyConfig.Builder()
//                                .setDefaultFontPath("fonts/AdvertisingBold.ttf")
//                                .setFontAttrId(R.attr.fontPath)
//                                .build()))
//                .build());
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        Utility.hideSoftKeyboard(this);
        dialog = new Dialog(this);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Pattern ps = Pattern.compile(".*[0-9].*");
                Matcher firstName = ps.matcher(name.getText().toString());

                if (email.getText().toString().equals("") || email.getText().toString().equalsIgnoreCase(getString(R.string.sample_mail_id))) {
                    displaySnackbar(getString(R.string.email_validation));
                } else if (!isValidEmail(email.getText().toString())) {
                    displaySnackbar(getString(R.string.not_valid_email));
                } else if (name.getText().toString().equals("")) {
                    displaySnackbar(getString(R.string.first_name_empty));
                } else if (firstName.matches()) {
                    displaySnackbar(getString(R.string.first_name_no_number));
                } else if (password.getText().toString().equals("") || password.getText().toString().equalsIgnoreCase(getString(R.string.password_txt))) {
                    displaySnackbar(getString(R.string.password_validation));
                } else if (password.length() < 6) {
                    displaySnackbar(getString(R.string.password_size));
                } else if (phone.getText().toString().equals("")) {
                    displaySnackbar(getString(R.string.password_size));
                } else {
                    if (hasInternet()) {
                        verifyPhone(phone.getText().toString());
                    } else {
                        displaySnackbar(getString(R.string.something_went_wrong_net));
                    }
                }
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            //Toast.makeText(this, "", Toast.LENGTH_LONG).show();
            return;
        }
        if (requestCode == APP_REQUEST_CODE) {
            Utility.showProgressDialog(dialog, true);
            AuthCredential credential = EmailAuthProvider.getCredential(email.getText().toString(), password.getText().toString());

            FirebaseAuth.getInstance().getCurrentUser().linkWithCredential(credential)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                final User user = new User(
                                        name.getText().toString(),
                                        email.getText().toString(),
                                        "https://firebasestorage.googleapis.com/v0/b/repo-place.appspot.com/o/dummy_user.png?alt=media&token=d2f214ec-58ad-47f0-a393-777feffe0f33",
                                        FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),
                                        FirebaseInstanceId.getInstance().getToken(),
                                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                                );
                                FirebaseDatabase.getInstance().getReference().child("users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        finish();
                                    }
                                });

                            } else {
                                Utility.showProgressDialog(dialog, false);
                                displaySnackbar("this email registered before");
                            }
                        }
                    });
        }


    }


    public final void verifyPhone(String phone) {

        Intent intent = new Intent(this, PhoneNumberActivity.class);
        //Optionally you can add toolbar title
        intent.putExtra("TITLE", getResources().getString(R.string.app_name));
        //Optionally you can pass phone number to populate automatically.
        intent.putExtra("PHONE_NUMBER", phone);
        startActivityForResult(intent, APP_REQUEST_CODE);
    }


//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
