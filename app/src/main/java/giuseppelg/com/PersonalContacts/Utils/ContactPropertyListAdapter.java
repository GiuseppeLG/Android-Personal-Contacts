package giuseppelg.com.PersonalContacts.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import giuseppelg.com.PersonalContacts.MainActivity;
import giuseppelg.com.PersonalContacts.R;
import giuseppelg.com.PersonalContacts.models.Contact;
import de.hdodenhof.circleimageview.CircleImageView;



public class ContactPropertyListAdapter extends ArrayAdapter<String> {

    private static final String TAG = "ContactPropertyListAdap";

    private LayoutInflater mInflater;
    private List<String> mProperties = null;
    private int layoutResource;
    private Context mContext;
    private String mAppend;

    public ContactPropertyListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> properties) {
        super(context, resource, properties);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResource = resource;
        this.mContext = context;
        this.mProperties = properties;
    }

    //---------------------------Stuff to change--------------------------------------------
    private static class ViewHolder{
        TextView property;
        ImageView rightIcon;
        ImageView leftIcon;
    }
    //--------------------------------------------------------------------------------------

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        /*
        ************ ViewHolder Build Pattern Start ************
         */
        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder();

            //---------------------------Stuff to change--------------------------------------------
            holder.property = (TextView) convertView.findViewById(R.id.tvMiddleCardView);
            holder.rightIcon = (ImageView) convertView.findViewById(R.id.iconRightCardView);
            holder.leftIcon = (ImageView) convertView.findViewById(R.id.iconLeftCardView);
            //--------------------------------------------------------------------------------------

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        //---------------------------Stuff to change--------------------------------------------
        final String[] property = {getItem(position)};
        holder.property.setText(property[0]);

        //check if it's an email or a phone number or a location
        //email
        if(property[0].contains("@")){
            holder.leftIcon.setImageResource(mContext.getResources().getIdentifier("@drawable/ic_email", null, mContext.getPackageName()));
            holder.leftIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: opening email.");
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.setType("plain/text");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {property[0]});
                    mContext.startActivity(emailIntent);
                }
            });
        }

        else if (property[0].matches("^[0-9]*$") && property[0].length() > 5) {
            //Phone call
            holder.leftIcon.setImageResource(mContext.getResources().getIdentifier("@drawable/ic_phone", null, mContext.getPackageName()));
            holder.leftIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((MainActivity) mContext).checkPermission(Init.PHONE_PERMISSIONS)) {
                        Log.d(TAG, "onClick: initiating phone call...");
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", property[0], null));
                        mContext.startActivity(callIntent);
                    } else {
                        ((MainActivity) mContext).verifyPermissions(Init.PHONE_PERMISSIONS);
                    }


                }
            });
            //setup the icon for sending text messages
            holder.rightIcon.setImageResource(mContext.getResources().getIdentifier("@drawable/ic_message", null, mContext.getPackageName()));
            holder.rightIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: initiating text message....");

                    //The number that we want to send SMS
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", property[0], null));
                    mContext.startActivity(smsIntent);
                }
            });
        }
        else if((property[0].length() != 0)){

            holder.leftIcon.setImageResource(mContext.getResources().getIdentifier("@drawable/address", null, mContext.getPackageName()));
            holder.leftIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: initiating maps....");

                    //Elaborate position like as common intents
                    property[0] = property[0].replace(' ', '+');
                    property[0] = "geo:0,0?q=" + property[0];
                    Log.d(TAG, "search of: " + property[0]);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(property[0]));
                    mapIntent.setPackage("com.google.android.apps.maps");
                    mContext.startActivity(mapIntent);
                }
            });

        }


        //--------------------------------------------------------------------------------------

        return convertView;
    }
}
















