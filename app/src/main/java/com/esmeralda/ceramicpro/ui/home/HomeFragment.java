package com.esmeralda.ceramicpro.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.esmeralda.ceramicpro.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private View view;
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

        imageList.add(new SlideModel(R.drawable.img_porsche_suv, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_ford_focus_rs, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_infiniti, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_lamborghini_huracan, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_nissan_gtr, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.img_skyline_r34, ScaleTypes.FIT));

        ImageSlider imageSlider = view.findViewById(R.id.image_slider);
        imageSlider.setImageList(imageList);
        return view;

    }

    public void Inversion(View view) {
    }

    public void New(View view) {
    }

    public void Maintenance(View view) {
    }
}