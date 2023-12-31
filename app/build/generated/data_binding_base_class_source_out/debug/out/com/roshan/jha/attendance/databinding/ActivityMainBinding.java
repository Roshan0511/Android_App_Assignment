// Generated by view binder compiler. Do not edit!
package com.roshan.jha.attendance.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.button.MaterialButton;
import com.roshan.jha.attendance.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityMainBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final TextView address;

  @NonNull
  public final TextView latitude;

  @NonNull
  public final TextView longitude;

  @NonNull
  public final MaterialButton markAttendance;

  @NonNull
  public final ProgressBar progressBar;

  @NonNull
  public final AppCompatEditText remarks;

  @NonNull
  public final CircleImageView selectedImage;

  @NonNull
  public final ConstraintLayout uploadImage;

  private ActivityMainBinding(@NonNull RelativeLayout rootView, @NonNull TextView address,
      @NonNull TextView latitude, @NonNull TextView longitude,
      @NonNull MaterialButton markAttendance, @NonNull ProgressBar progressBar,
      @NonNull AppCompatEditText remarks, @NonNull CircleImageView selectedImage,
      @NonNull ConstraintLayout uploadImage) {
    this.rootView = rootView;
    this.address = address;
    this.latitude = latitude;
    this.longitude = longitude;
    this.markAttendance = markAttendance;
    this.progressBar = progressBar;
    this.remarks = remarks;
    this.selectedImage = selectedImage;
    this.uploadImage = uploadImage;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.address;
      TextView address = ViewBindings.findChildViewById(rootView, id);
      if (address == null) {
        break missingId;
      }

      id = R.id.latitude;
      TextView latitude = ViewBindings.findChildViewById(rootView, id);
      if (latitude == null) {
        break missingId;
      }

      id = R.id.longitude;
      TextView longitude = ViewBindings.findChildViewById(rootView, id);
      if (longitude == null) {
        break missingId;
      }

      id = R.id.markAttendance;
      MaterialButton markAttendance = ViewBindings.findChildViewById(rootView, id);
      if (markAttendance == null) {
        break missingId;
      }

      id = R.id.progressBar;
      ProgressBar progressBar = ViewBindings.findChildViewById(rootView, id);
      if (progressBar == null) {
        break missingId;
      }

      id = R.id.remarks;
      AppCompatEditText remarks = ViewBindings.findChildViewById(rootView, id);
      if (remarks == null) {
        break missingId;
      }

      id = R.id.selectedImage;
      CircleImageView selectedImage = ViewBindings.findChildViewById(rootView, id);
      if (selectedImage == null) {
        break missingId;
      }

      id = R.id.uploadImage;
      ConstraintLayout uploadImage = ViewBindings.findChildViewById(rootView, id);
      if (uploadImage == null) {
        break missingId;
      }

      return new ActivityMainBinding((RelativeLayout) rootView, address, latitude, longitude,
          markAttendance, progressBar, remarks, selectedImage, uploadImage);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
