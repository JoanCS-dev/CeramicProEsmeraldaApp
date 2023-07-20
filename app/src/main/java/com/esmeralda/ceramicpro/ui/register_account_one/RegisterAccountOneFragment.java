package com.esmeralda.ceramicpro.ui.register_account_one;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.esmeralda.ceramicpro.HomeActivity;
import com.esmeralda.ceramicpro.R;
import com.esmeralda.ceramicpro.ui.register_account_two.RegisterAccountTwoFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterAccountOneFragment extends Fragment {

    private Button B_Continuar;
    private ImageButton Back;
    private TextInputEditText txt_Name, txt_Lastname, txt_Phone, txt_Email, txt_Pass, txt_Confirm_Pass;
    private String txt_NameParse, txt_LastnameParse, txt_PhoneParse, txt_EmailParse, txt_PassParse;
    private View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register_account_one, container, false);
        B_Continuar = view.findViewById(R.id.Btn_Continuar);
        Back = view.findViewById(R.id.Btn_Back_F);
        txt_Name = view.findViewById(R.id.txt_Name);
        txt_Lastname = view.findViewById(R.id.txt_Lastname);
        txt_Phone = view.findViewById(R.id.txt_Phone);
        txt_Email = view.findViewById(R.id.txt_Email);
        txt_Pass = view.findViewById(R.id.txt_Pass);
        txt_Confirm_Pass = view.findViewById(R.id.txt_Confirm_Pass);
        validateBack();
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), HomeActivity.class));
            }
        });

        B_Continuar.setOnClickListener(view -> {
            String __txt_Name = txt_Name.getText().toString();
            String __txt_Lastname = txt_Lastname.getText().toString();
            String __txt_Phone = txt_Phone.getText().toString();
            String __txt_Email = txt_Email.getText().toString();
            String __txt_Pass = txt_Pass.getText().toString();
            String __txt_Confirm_Pass = txt_Confirm_Pass.getText().toString();
            if(__txt_Name.equals("") || __txt_Lastname.equals("") || __txt_Phone.equals("") || __txt_Email.equals("") || __txt_Pass.equals("") || __txt_Confirm_Pass.equals("")){
                if(__txt_Name.equals("")){
                    Message("Información", "Por favor escribe tu(s) nombre(s)");
                }else if(__txt_Lastname.equals("")){
                    Message("Información", "Por favor escribe tu(s) apellido(s)");
                }else if(__txt_Phone.equals("")){
                    Message("Información", "Por favor escribe tu télefono");
                }else if(__txt_Email.equals("")){
                    Message("Información", "Por favor escribe el correo electrónico");
                }else if(!ValidEmail(txt_Email.getText().toString())){
                    Message("Información", "Por favor escribe un correo electrónico valido");
                }else if(__txt_Pass.equals("")){
                    Message("Información", "Por favor escribe la contraseña");
                }
            }else {
                if(!__txt_Pass.equals(__txt_Confirm_Pass)){
                    Message("Información", "Las contraseñas no coinciden");
                }else{
                    Bundle result = new Bundle();
                    result.putString("txt_Name", txt_Name.getText().toString());
                    result.putString("txt_Lastname", txt_Lastname.getText().toString());
                    result.putString("txt_Phone", txt_Phone.getText().toString());
                    result.putString("txt_Email", txt_Email.getText().toString());
                    result.putString("txt_Pass", txt_Pass.getText().toString());
                    getParentFragmentManager().setFragmentResult("requestKey", result);
                    Fragment fragment = new RegisterAccountTwoFragment();
                    FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                    fm.replace(R.id.container, fragment).commit();
                }
            }
        });

        return view;
    }
    private void Message(String Title, String Message) {
        MaterialAlertDialogBuilder Builder = new MaterialAlertDialogBuilder(view.getContext());
        Builder.setTitle(Title)
                .setMessage(Message)
                .setPositiveButton("Ok", null).show();
    }
    private boolean ValidEmail(String email){
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher mather = pattern.matcher(email);
        return mather.find();
    }

    private void validateBack() {
        getParentFragmentManager().setFragmentResultListener("databack", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                txt_NameParse = bundle.getString("txt_NameParse");
                txt_LastnameParse = bundle.getString("txt_LastnameParse");
                txt_PhoneParse = bundle.getString("txt_PhoneParse");
                txt_EmailParse = bundle.getString("txt_EmailParse");
                txt_PassParse = bundle.getString("txt_PassParse");
                // Do something with the result...

                txt_Name.setText(txt_NameParse);
                txt_Lastname.setText(txt_LastnameParse);
                txt_Phone.setText(txt_PhoneParse);
                txt_Email.setText(txt_EmailParse);
                txt_Pass.setText(txt_PassParse);
                txt_Confirm_Pass.setText("");
            }
        });
    }
}