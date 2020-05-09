package org.mkab.chatapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.mkab.chatapp.R;
import org.mkab.chatapp.model.Information;
import org.mkab.chatapp.service.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 5/11/2016.
 */
public class GridViewAdapter extends ArrayAdapter {

    private Context context;
    private int layoutResourceId;
    //private ArrayList data = new ArrayList();
    private List<Information> data = new ArrayList<>();

    public GridViewAdapter(Context context, int layoutResourceId, List<Information> informationList) {
        super(context, layoutResourceId, informationList);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = informationList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Information information = (Information) getItem(position);

       // Picasso.with(context).load(R.drawable.surgeon).placeholder(R.drawable.dp_placeholder).into(holder.image);
        Picasso.get()
                .load(R.drawable.surgeon)
                .networkPolicy(Tools.isNetworkAvailable(context) ? NetworkPolicy.NO_CACHE : NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.mkab_transparent)
                .error(R.drawable.mkab_transparent)
                .into(holder.image);
        // holder.image.setImageResource(R.drawable.medicine);
        holder.imageTitle.setText(information.getTitle());

        //   holder.imageTitle.setText(item.getTitle());
        /*if (item.getTitle().equals("Medicine")) {
            Picasso.with(context).load(R.drawable.medicine).placeholder(R.drawable.progress_animation).into(holder.image);
            // holder.image.setImageResource(R.drawable.medicine);
            holder.imageTitle.setText(item.getTitle());
        } else if (item.getTitle().equals("Gynaecologists")) {
            Picasso.with(context).load(R.drawable.gune).placeholder(R.drawable.progress_animation).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        } else if (item.getTitle().equals("Cardiologists")) {
            Picasso.with(context).load(R.drawable.cardiologist).placeholder(R.drawable.progress_animation).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        } else if (item.getTitle().equals("Dentists")) {
            Picasso.with(context).load(R.drawable.dentist).placeholder(R.drawable.progress_animation).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        } else if (item.getTitle().equals("Urologist")) {
            Picasso.with(context).load(R.drawable.urologist).placeholder(R.drawable.progress_animation).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        } else if (item.getTitle().equals("Dermatologist")) {
            Picasso.with(context).load(R.drawable.dermatologist).placeholder(R.drawable.progress_animation).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        } else if (item.getTitle().equals("Endocrinology")) {
            Picasso.with(context).load(R.drawable.endocrinology).placeholder(R.drawable.progress_animation).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        } else if (item.getTitle().equals("ENT Specialist")) {
            Picasso.with(context).load(R.drawable.ent_specialist).placeholder(R.drawable.progress_animation).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        } else if (item.getTitle().equals("Gastroenterologist")) {
            Picasso.with(context).load(R.drawable.gastroenterologist).placeholder(R.drawable.progress_animation).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        } else if (item.getTitle().equals("General Physician")) {
            Picasso.with(context).load(R.drawable.general_physician).placeholder(R.drawable.progress_animation).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        } else if (item.getTitle().equals("Neurology")) {
            Picasso.with(context).load(R.drawable.neurology).placeholder(R.drawable.progress_animation).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        } else if (item.getTitle().equals("Nutritionlist")) {
            Picasso.with(context).load(R.drawable.nutritionlist).placeholder(R.drawable.progress_animation).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        } else if (item.getTitle().equals("Opthalmologist")) {
            Picasso.with(context).load(R.drawable.opthalmologist).placeholder(R.drawable.progress_animation).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        } else if (item.getTitle().equals("Pediatrician")) {
            Picasso.with(context).load(R.drawable.pediatrician).placeholder(R.drawable.progress_animation).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        } else if (item.getTitle().equals("Psychology")) {
            Picasso.with(context).load(R.drawable.psychology).placeholder(R.drawable.progress_animation).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        }*/
        // holder.image.setImageBitmap(item.getImage());

        /*if (item.getTitle().equals("Prescriptions")) {
            Picasso.with(context).load(R.drawable.surgeon).placeholder(R.drawable.dp_placeholder).into(holder.image);
            // holder.image.setImageResource(R.drawable.medicine);
            holder.imageTitle.setText(item.getTitle());
        } else if (item.getTitle().equals("Hujurs Instructions")) {
            Picasso.with(context).load(R.drawable.surgeon).placeholder(R.drawable.progress_animation).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        } else if (item.getTitle().equals("General Information")) {
            Picasso.with(context).load(R.drawable.dp_placeholder).placeholder(R.drawable.dp_placeholder).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        } else if (item.getTitle().equals("FAQ")) {
            Picasso.with(context).load(R.drawable.dp_placeholder).placeholder(R.drawable.dp_placeholder).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        }else if (item.getTitle().equals("PPE")) {
            Picasso.with(context).load(R.drawable.surgeon).placeholder(R.drawable.dp_placeholder).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        }else if (item.getTitle().equals("Home Quarantine")) {
            Picasso.with(context).load(R.drawable.surgeon).placeholder(R.drawable.dp_placeholder).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        }else if (item.getTitle().equals("Isolation")) {
            Picasso.with(context).load(R.drawable.dp_placeholder).placeholder(R.drawable.dp_placeholder).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        }else if (item.getTitle().equals("Corona Info")) {
            Picasso.with(context).load(R.drawable.dp_placeholder).placeholder(R.drawable.dp_placeholder).into(holder.image);
            holder.imageTitle.setText(item.getTitle());
        }*/

        return row;
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }
}
