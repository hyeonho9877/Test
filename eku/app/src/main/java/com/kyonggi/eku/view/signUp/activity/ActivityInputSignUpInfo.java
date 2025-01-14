package com.kyonggi.eku.view.signUp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kyonggi.eku.R;
import com.kyonggi.eku.databinding.ActivityInputSignupInfoBinding;
import com.kyonggi.eku.databinding.FargmentSignupInfoBinding;
import com.kyonggi.eku.model.OCRForm;
import com.kyonggi.eku.model.SignUpForm;
import com.kyonggi.eku.presenter.signUp.SignUpInfoPresenter;
import com.kyonggi.eku.utils.callbacks.OnSignUpConfirmedListener;
import com.kyonggi.eku.utils.exceptions.NoExtraDataException;
import com.kyonggi.eku.view.signUp.dialog.SignUpErrorDialogFragment;
import com.kyonggi.eku.view.signUp.fragment.FragmentSignUpEnd;
import com.kyonggi.eku.view.signUp.fragment.FragmentSignUpProgressFirst;
import com.kyonggi.eku.view.signUp.fragment.FragmentSignUpProgressSecond;
import com.kyonggi.eku.view.signUp.fragment.FragmentSignupInfo;

import java.util.List;
import java.util.Optional;

public class ActivityInputSignUpInfo extends AppCompatActivity implements OnSignUpConfirmedListener {

    private static final String TAG = "ActivityInputSignUpInfo";
    private ActivityInputSignupInfoBinding binding;
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private SignUpInfoPresenter presenter;
    private ProgressDialog dialog;
    private FragmentSignupInfo fragmentSignupInfo;
    private List<String> deptList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInputSignupInfoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        presenter = new SignUpInfoPresenter(this, this);
        presenter.getDept();

        fragmentSignupInfo = new FragmentSignupInfo();
        FragmentSignUpProgressFirst fragmentSignUpProgressFirst = new FragmentSignUpProgressFirst();

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        int downSpeed = capabilities.getLinkDownstreamBandwidthKbps()/1000;
        int delays = (2*downSpeed+300)/downSpeed*100;

        new Handler().postDelayed(()->{
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.progress_bar_section, fragmentSignUpProgressFirst);
            fragmentTransaction.add(R.id.fragment_user_interaction, fragmentSignupInfo, "INPUT_FRAGMENT");
            fragmentTransaction.commit();
        }, delays);
    }


    @Override
    public void onConfirmed(SignUpForm form) {
        dialog = new ProgressDialog(this);
        dialog.setVolumeControlStream(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("잠시만 기다려 주세요..");
        dialog.show();
        presenter.signUp(form);
    }

    @Override
    public void onSignUpEnd() {
        finish();
    }

    public OCRForm getForm() throws NoExtraDataException {
        Optional<OCRForm> form = Optional.ofNullable((OCRForm) getIntent().getSerializableExtra("studentInfo"));
        OCRForm ocrForm = form.orElseThrow(NoExtraDataException::new);
        presenter.processForm(ocrForm, deptList);
        return ocrForm;
    }

    public int isAllFieldsAppropriate(FargmentSignupInfoBinding binding) {
        return presenter.isAllFieldsAppropriate(binding);
    }

    public void onSignUpResponseSuccess(){
        dialog.dismiss();
        FragmentSignUpProgressSecond fragmentSignUpProgressSecond = new FragmentSignUpProgressSecond();
        FragmentSignUpEnd fragmentSignUpEnd = new FragmentSignUpEnd();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction
                .setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
                .replace(R.id.progress_bar_section, fragmentSignUpProgressSecond);
        fragmentTransaction
                .setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
                .replace(R.id.fragment_user_interaction, fragmentSignUpEnd);
        fragmentTransaction.commit();
    }

    public void onSignUpResponseFailed() {
        dialog.dismiss();
        new SignUpErrorDialogFragment(SignUpErrorDialogFragment.DUPLICATED_ACCOUNT).show(getSupportFragmentManager(), "error");
    }

    public void onDeptResponseSuccess(List<String> deptList){
        this.deptList = deptList;
    }

    public List<String> getDeptList() {
        return deptList;
    }
}
