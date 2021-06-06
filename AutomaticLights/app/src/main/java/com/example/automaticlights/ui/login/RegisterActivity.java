package com.example.automaticlights.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.automaticlights.MainActivity;
import com.example.automaticlights.R;
import com.example.automaticlights.ui.login.LoginViewModel;
import com.example.automaticlights.ui.login.LoginViewModelFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.automaticlights.MESSAGE";

    public Boolean flagPassConfirm = false;
    public Boolean flagUserConfirm = true;
    public Boolean flagEmailConfirm = true;
    public int nUser = 0;

    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });
    }

    public void logUser(View view) {
        System.out.println("Entrou");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Users");

        flagPassConfirm = false;
        flagUserConfirm = true;
        flagEmailConfirm = true;

        Intent intent = new Intent(this, MainActivity.class);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, String> userData = new HashMap<String, String>();

                EditText editEmail = (EditText) findViewById(R.id.email);
                EditText editText = (EditText) findViewById(R.id.username);
                EditText editPass = (EditText) findViewById(R.id.password);
                EditText editPass2 = (EditText) findViewById(R.id.password2);
                TextView passErro = (TextView) findViewById(R.id.passErro);

                for ( DataSnapshot user : snapshot.getChildren()){
                    String userName = user.child("name").getValue().toString();
                    String emailName = user.child("email").getValue().toString();
                    //System.out.println(userName.equals(editText.getText().toString()));
                    if (userName.equals(editText.getText().toString())){
                        flagUserConfirm = false;
                    }
                    if (emailName.equals(editEmail.getText().toString())){
                        flagEmailConfirm = false;
                    }
                }
                //System.out.println("user " + flagUserConfirm);
                final int nUser = (int)snapshot.getChildrenCount();
                //System.out.println("nUser " + nUser);

                //System.out.println("pass um " + editPass.getText());
                //System.out.println("pass confirm " + editPass2.getText());

                if (editPass.getText().toString().equals(editPass2.getText().toString())){
                    flagPassConfirm = true;
                }

                System.out.println("pass " + flagPassConfirm);
                System.out.println("user " + flagUserConfirm);
                System.out.println("email " + flagEmailConfirm);

                String mail = editEmail.getText().toString();
                String user = editText.getText().toString();
                String pass = editPass.getText().toString();

                if (!mail.contains("@")){
                    flagEmailConfirm = false;
                }

                if (flagPassConfirm && flagUserConfirm && flagEmailConfirm) {
                    userData.put("email", mail);
                    userData.put("name", user);
                    userData.put("password", pass);

                    int newUser = nUser + 1;
                    myRef.child(String.valueOf(newUser));
                    myRef.child(String.valueOf(newUser)).setValue(userData);
                    //System.out.println(userData);
                    intent.putExtra(EXTRA_MESSAGE, editText.getText().toString());
                    startActivity(intent);
                    //System.out.println("Entrou db");
                } else {
                    if (!flagUserConfirm) {
                        passErro.setText("");
                        passErro.setText("Utilizador já registado!");
                    } else if (!flagEmailConfirm) {
                        passErro.setText("");
                        passErro.setText("Email já registado ou inválido");
                    } else if (!flagPassConfirm){
                        passErro.setText("");
                        passErro.setText("Palavras-passe não coincidem.");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}