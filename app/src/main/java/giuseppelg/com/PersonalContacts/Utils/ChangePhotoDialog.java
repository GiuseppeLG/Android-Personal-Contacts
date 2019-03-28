package giuseppelg.com.PersonalContacts.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import java.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import giuseppelg.com.PersonalContacts.R;



public class ChangePhotoDialog extends DialogFragment{

    private static final String TAG = "ChangePhotoDialog";

    public interface OnPhotoReceivedListener{
        public void getBitmapImage(Bitmap bitmap);
        public void getImagePath(String imagePath);
    }

    OnPhotoReceivedListener mOnPhotoReceived;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_changephoto, container, false);

        //initalize the textview for starting the camera
        TextView takePhoto = (TextView) view.findViewById(R.id.dialogTakePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: starting camera.");
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, Init.CAMERA_REQUEST_CODE);
            }
        });

        //Initialize the textview for choosing an image from memory
        TextView selectPhoto = (TextView) view.findViewById(R.id.dialogChoosePhoto);
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: accessing phones memory.");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, Init.PICKFILE_REQUEST_CODE);
            }
        });

        // Cancel button for closing the dialog
        TextView cancelDialog = (TextView) view.findViewById(R.id.dialogCancel);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing dialog.");
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnPhotoReceived = (OnPhotoReceivedListener) getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage() );
        }
    }

    private String salvaBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String filename2 = dateFormat.format(new Date()) + ".jpg";
        String filename = "IMG_" + filename2;

        File StorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File outputfile=new File(StorageDir+"/Camera/", filename);


        FileOutputStream fo;

        try {
            outputfile.createNewFile();
            fo = new FileOutputStream(outputfile);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputfile.getAbsolutePath();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
        REsults when taking a new image with camera
         */
        if(requestCode == Init.CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Log.d(TAG, "onActivityResult: done taking a picture.");

            //get the new image bitmap
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Log.d(TAG, "onActivityResult: receieved bitmap: " + bitmap);

            String pathBitmap = salvaBitmap(bitmap);

            //send the bitmap and fragment to the interface
            mOnPhotoReceived.getImagePath("file:/" + pathBitmap);
            getDialog().dismiss();
        }

        /*
        Results when selecting new image from phone memory
         */
        if(requestCode == Init.PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri selectedImageUri = data.getData();
            File file = new File(selectedImageUri.toString());
            Log.d(TAG, "onActivityResult: images: " + file.getPath());


            //send the bitmap and fragment to the interface
            mOnPhotoReceived.getImagePath(file.getPath());
            getDialog().dismiss();

        }
    }

}

















