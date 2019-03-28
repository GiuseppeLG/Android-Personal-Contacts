package giuseppelg.com.PersonalContacts;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import giuseppelg.com.PersonalContacts.Utils.ChangePhotoDialog;
import giuseppelg.com.PersonalContacts.Utils.DatabaseHelper;
import giuseppelg.com.PersonalContacts.Utils.Init;
import giuseppelg.com.PersonalContacts.Utils.UniversalImageLoader;
import giuseppelg.com.PersonalContacts.models.Contact;
import de.hdodenhof.circleimageview.CircleImageView;



public class AddContactFragment extends Fragment implements ChangePhotoDialog.OnPhotoReceivedListener {

    private static final String TAG = "AddContactFragment";

   // private Contact mContact;
    private EditText mPhoneNumber, mName, mEmail, mAddress;
    private CircleImageView mContactImage;
    private ImageView mBusinessCard;
    private Spinner mSelectDevice;
    private Toolbar toolbar;
    private String mSelectedImagePath;
    private String mSelectedBusinessCardPath;
    private Integer controlImageSelector;
    private int mPreviousKeyStroke;

    private ArrayList<String> ArrayNumers = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addcontact, container, false);
        mPhoneNumber = (EditText) view.findViewById(R.id.etContactPhone);
        mName = (EditText) view.findViewById(R.id.etContactName);
        mEmail = (EditText) view.findViewById(R.id.etContactEmail);
        mContactImage = (CircleImageView) view.findViewById(R.id.contactImage);
        mSelectDevice = (Spinner) view.findViewById(R.id.selectDevice);
        mAddress = (EditText) view.findViewById(R.id.etAddress);
        mBusinessCard = (ImageView) view.findViewById(R.id.contactBusinessCard);
        toolbar = (Toolbar) view.findViewById(R.id.editContactToolbar);
        Log.d(TAG, "onCreateView: started.");

        mSelectedImagePath = null;
        mSelectedBusinessCardPath = null;

        //load the default images by causing an error
        UniversalImageLoader.setImage(null, mContactImage, null, "");
        UniversalImageLoader.setImage(null, mBusinessCard, null, "");

        //set the heading the for the toolbar
        TextView heading = (TextView) view.findViewById(R.id.textContactToolbar);
        heading.setText(getString(R.string.add_contact));

        //required for setting up the toolbar
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        //navigation for the backarrow
        ImageView ivBackArrow = (ImageView) view.findViewById(R.id.ivBackArrow);
        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked back arrow.");
                //remove previous fragment from the backstack (therefore navigating back)
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        // initiate the dialog box for choosing an image
        ImageView ivCamera = (ImageView) view.findViewById(R.id.ivCamera);
        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Make sure all permissions have been verified before opening the dialog
                 */
                for(int i = 0; i < Init.PERMISSIONS.length; i++){
                    String[] permission = {Init.PERMISSIONS[i]};
                    if(((MainActivity)getActivity()).checkPermission(permission)){
                        if(i == Init.PERMISSIONS.length - 1){
                            Log.d(TAG, "onClick: opening the 'image selection dialog box'.");
                            controlImageSelector = 1;
                            ChangePhotoDialog dialog = new ChangePhotoDialog();
                            dialog.show(getFragmentManager(), getString(R.string.change_photo_dialog));
                            dialog.setTargetFragment(AddContactFragment.this, 0);
                        }
                    }else{
                        ((MainActivity)getActivity()).verifyPermissions(permission);
                    }
                }
            }
        });

        // initiate the dialog box for choosing an image
        ImageView ivCamera2 = (ImageView) view.findViewById(R.id.ivCamera2);
        ivCamera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Make sure all permissions have been verified before opening the dialog
                 */
                for(int i = 0; i < Init.PERMISSIONS.length; i++){
                    String[] permission = {Init.PERMISSIONS[i]};
                    if(((MainActivity)getActivity()).checkPermission(permission)){
                        if(i == Init.PERMISSIONS.length - 1){
                            Log.d(TAG, "onClick: box image 2'.");
                            controlImageSelector = 2;
                            ChangePhotoDialog dialog = new ChangePhotoDialog();
                            dialog.show(getFragmentManager(), getString(R.string.change_photo_dialog));
                            dialog.setTargetFragment(AddContactFragment.this, 0);
                        }
                    }else{
                        ((MainActivity)getActivity()).verifyPermissions(permission);
                    }
                }
            }
        });

        //set onclicklistenre to the 'checkmar' icon for saving a contact
        ImageView confirmNewContact = (ImageView) view.findViewById(R.id.ivCheckMark);
        confirmNewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save new contact.");
                if(checkStringIfNull(mName.getText().toString())){
                    Log.d(TAG, "onClick: saving new contact. " + mName.getText().toString() );

                    DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
                    Contact contact = new Contact(mName.getText().toString(),
                            mPhoneNumber.getText().toString(),
                            mSelectDevice.getSelectedItem().toString(),
                            mEmail.getText().toString(),
                            mSelectedImagePath,
                            mAddress.getText().toString(),
                            mSelectedBusinessCardPath);
                    if(databaseHelper.addContact(contact)){
                        Toast.makeText(getActivity(), "Contact Saved", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                      //  Log.d(TAG, "" + mName);
                    }else{
                        Toast.makeText(getActivity(), "Error Saving", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


        return view;
    }


    private boolean checkStringIfNull(String string){
        if(string.equals("")){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.contact_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuitem_delete:
                Log.d(TAG, "onOptionsItemSelected: deleting contact.");
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Retrieves the selected image from the bundle (coming from ChangePhotoDialog)
     * @param bitmap
     */
    @Override
    public void getBitmapImage(Bitmap bitmap) {
        Log.d(TAG, "getBitmapImage: got the bitmap: " + bitmap);
        //get the bitmap from 'ChangePhotoDialog'
        if(bitmap != null) {
            //compress the image (if you like)
            ((MainActivity)getActivity()).compressBitmap(bitmap, 70);
            if(controlImageSelector == 1){
                mContactImage.setImageBitmap(bitmap);
            }
            else if(controlImageSelector == 2){
                mBusinessCard.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void getImagePath(String imagePath) {
        Log.d(TAG, "getImagePath: got the image path: " + imagePath);

        if( !imagePath.equals("")){

            Log.d(TAG, "" + controlImageSelector);
            if(controlImageSelector == 1) {
                imagePath = imagePath.replace(":/", "://");
                mSelectedImagePath = imagePath;
                UniversalImageLoader.setImage(imagePath, mContactImage, null, "");
            }
            else if(controlImageSelector == 2){
                imagePath = imagePath.replace(":/", "://");
                mSelectedBusinessCardPath = imagePath;
                UniversalImageLoader.setImage(imagePath, mBusinessCard, null, "");
            }

        }
    }


}
