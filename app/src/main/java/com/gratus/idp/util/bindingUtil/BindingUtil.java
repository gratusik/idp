package com.gratus.idp.util.bindingUtil;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gratus.idp.R;
import com.gratus.idp.model.common.CyclePathNew;
import com.gratus.idp.util.constants.AppConstants;
import com.gratus.idp.view.adapter.ReportListAdapter;
import com.jakewharton.rxbinding3.widget.RxTextView;

import java.util.List;

import static com.gratus.idp.util.constants.AppConstants.FEMALE;
import static com.gratus.idp.util.constants.AppConstants.MALE;

public class BindingUtil {
    @BindingAdapter("error")
    public static void setError(EditText editText, Object strOrResId) {
        if (strOrResId instanceof Integer) {
            editText.setError(
                    editText.getContext().getString((Integer) strOrResId));
        } else {
            editText.setError((String) strOrResId);
        }
    }
    @BindingAdapter("errorTextView")
    public static void setError(TextView textView, Object strOrResId) {
        if (strOrResId instanceof Integer) {
            textView.setError(
                    textView.getContext().getString((Integer) strOrResId));
        } else {
            textView.setError((String) strOrResId);
        }
    }
    @BindingAdapter("hint")
    public static void setHint(TextView textView, Object strOrResId) {
        if (strOrResId instanceof Integer) {
            textView.setHint(
                    textView.getContext().getString((Integer) strOrResId));
        } else {
            textView.setHint((String) strOrResId);
        }
    }
    @BindingAdapter("errorText")
    public static void setErrorMessage(TextInputLayout textIputLayout, Object strOrResId) {
        if (strOrResId instanceof Integer) {
            textIputLayout.setError(
                    textIputLayout.getContext().getString((Integer) strOrResId));
        } else {
            textIputLayout.setError((String) strOrResId);
        }
    }
    @BindingAdapter("bHD")
    public static void setBackgroundHD(TextView view,boolean b) {
        Context context = view.getContext();
        if(b) {
            view.setBackground(context.getDrawable(R.color.grey_sub_text));
        }
        else{
            view.setBackground(context.getDrawable(R.color.grey));
        }

    }

    @BindingAdapter("srcMale")
    public static void setMaleDrawable(ImageView view,String gender) {
        Context context = view.getContext();
        if(gender!=null) {
            if(gender.equalsIgnoreCase(MALE)){
                view.setBackground(context.getDrawable(R.drawable.buttonchecked));
                view.setEnabled(false);
            }
            else{
                view.setBackground(context.getDrawable(R.drawable.buttonunchecked));
                view.setEnabled(true);
            }
        }
        else{
            view.setBackground(context.getDrawable(R.drawable.buttonunchecked));
            view.setEnabled(true);
        }

    }
    @BindingAdapter("srcFemale")
    public static void setFemaleDrawable(ImageView view, String gender) {
        Context context = view.getContext();
        if(gender!=null) {
            if(gender.equalsIgnoreCase(FEMALE)){
                view.setBackground(context.getDrawable(R.drawable.buttonchecked));
                view.setEnabled(false);
            }
            else{
                view.setBackground(context.getDrawable(R.drawable.buttonunchecked));
                view.setEnabled(true);
            }
        }
        else{
            view.setBackground(context.getDrawable(R.drawable.buttonunchecked));
            view.setEnabled(true);
        }
    }

    @BindingAdapter("textChangedListener")
    public static void bindTextWatcher(TextInputEditText editText, TextWatcher textWatcher) {
        editText.addTextChangedListener(textWatcher);
    }
    @BindingAdapter("textenabled")
    public static void setTextEnabled(TextInputEditText editText, boolean enabled) {
        editText.setEnabled(enabled);
    }

    @BindingAdapter("visibilityProgressBar")
    public static void setVisibilityProgressBar(ProgressBar progressBar, boolean visibility) {
        if (visibility) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
    @BindingAdapter("visibilityButton")
    public static void setVisibilityButton(MaterialButton button, boolean visibility) {
        if (visibility) {
            button.setVisibility(View.VISIBLE);
        } else {
            button.setVisibility(View.GONE);
        }
    }
    @BindingAdapter("visibilityRelative")
    public static void setVisibilityRelative(RelativeLayout relativeLayout, boolean visibility) {
        if (visibility) {
            relativeLayout.setVisibility(View.VISIBLE);
        } else {
            relativeLayout.setVisibility(View.GONE);
        }
    }
    @BindingAdapter("buttonAnimation")
    public static void setButtonAnimation(MaterialButton button, boolean animationButton) {
        if(animationButton) {
            int cx = button.getMeasuredWidth() / 2;
            int cy = button.getMeasuredHeight() / 2;

            // get the initial radius for the clipping circle
            int initialRadius = button.getWidth() / 2;

            // create the animation (the final radius is zero)
            Animator anim =
                    null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                anim = ViewAnimationUtils.createCircularReveal(button, cx, cy, (float) initialRadius, (float) cy);
            }
            anim.setDuration(1000L);

            // make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    button.setVisibility(View.GONE);
                }
            });

            // start the animation
            anim.start();
        }
    }
    @BindingAdapter("bottomSheetBehaviorState")
    public static void setState(View v, int bottomSheetBehaviorState) {
        BottomSheetBehavior<View> viewBottomSheetBehavior = BottomSheetBehavior.from(v);
        viewBottomSheetBehavior.setHideable(true);
        viewBottomSheetBehavior.setState(bottomSheetBehaviorState);
    }


    @BindingAdapter({"reportListAdapter"})
    public static void addBlogItems(RecyclerView recyclerView, List<CyclePathNew> cyclePathNews) {
        ReportListAdapter adapter = (ReportListAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            if(cyclePathNews.size()>0) {
                adapter.clearItems();
            }
            if(cyclePathNews.size()>0) {
                adapter.addItems(cyclePathNews);
            }
        }
    }
}
