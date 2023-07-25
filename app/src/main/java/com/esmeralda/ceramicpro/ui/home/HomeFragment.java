package com.esmeralda.ceramicpro.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.esmeralda.ceramicpro.HomeActivity;
import com.esmeralda.ceramicpro.MainActivity;
import com.esmeralda.ceramicpro.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private View view;
    private BottomSheetDialog dialog;
    private ImageView teamwork, img_inversion, img_new, img_maintenance;
    private MaterialCardView cw_inversion, cw_new, cw_maintenance;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view  = inflater.inflate(R.layout.fragment_home, container, false);
        List<SlideModel> imageList = new ArrayList<>(); // Create image list
        dialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        teamwork = view.findViewById(R.id.TeamWork);

        img_inversion = view.findViewById(R.id.img_Inversion);
        img_new = view.findViewById(R.id.img_New);
        img_maintenance = view.findViewById(R.id.img_Maintenance);

        cw_inversion = view.findViewById(R.id.Inversion);
        cw_new = view.findViewById(R.id.New);
        cw_maintenance = view.findViewById(R.id.Maintenance);

        cw_inversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_inversion.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_Inversion), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_new.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_maintenance.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        });
        cw_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_inversion.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_new.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_New), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_maintenance.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        });
        cw_maintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_inversion.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_new.setColorFilter(ContextCompat.getColor(getContext(), R.color.black), android.graphics.PorterDuff.Mode.MULTIPLY);
                img_maintenance.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_Maintenance), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        });

        imageList.add(new SlideModel(R.drawable.img_porsche_suv, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_ford_focus_rs, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_infiniti, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_lamborghini_huracan, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_nissan_gtr, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_skyline_r34, ScaleTypes.FIT));

        ImageSlider imageSlider = view.findViewById(R.id.image_slider);
        imageSlider.setImageList(imageList);

        createDialog();

        teamwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        return view;

    }

    private void createDialog() {
        View view = getLayoutInflater().inflate(R.layout.request_dialog,null,false);

        Button btn_send = view.findViewById(R.id.Btn_Send);

        btn_send.setOnClickListener(view1 -> {
            dialog.dismiss();
        });

        dialog.setContentView(view);
    }


}