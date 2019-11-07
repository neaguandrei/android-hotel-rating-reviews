package ratingapp.ddey.com.testratingapp.utils.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.models.Note;
import ratingapp.ddey.com.testratingapp.utils.database.FirebaseHelper;
import ratingapp.ddey.com.testratingapp.utils.remote.ConnectionStatus;
import ratingapp.ddey.com.testratingapp.utils.database.DatabaseHelper;

public class NoteAdapter extends ArrayAdapter<Note> {
    private Context mContext;
    private int mResource;
    private List<Note> mList;
    private Activity mActivity;

    public NoteAdapter(@NonNull Context context, int resource, @NonNull List<Note> list, Activity activity) {
        super(context, resource, list);
        this.mContext = context;
        this.mResource = resource;
        this.mList = list;
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(mResource, null);
        }

        Note note = getItem(position);

        if (note != null) {
            TextView tvNote = view.findViewById(R.id.list_notes_text);
            if (note.getText() != null) {
                tvNote.setText(note.getText());
            }
        }


        ImageButton btnDelete = view.findViewById(R.id.list_notes_imgbtn);
        btnDelete.setTag(position);

        btnDelete.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (verifyConnection()) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
                    dialog.setMessage("Are you sure you want to delete this note?")
                            .setCancelable(false)
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int index = (int) v.getTag();
                                    Note note = mList.get(index);

                                    DatabaseHelper mDb = new DatabaseHelper(mActivity);
                                    FirebaseHelper mFirebaseHelper = FirebaseHelper.getInstance();
                                    mFirebaseHelper.openConnection();
                                    mDb.deleteNote(note);
                                    mFirebaseHelper.getUserNotesReference().child(note.getNoteToken()).removeValue();
                                    mList.remove(index);
                                    notifyDataSetChanged();
                                }
                            });

                    dialog.create().show();
                } else {
                    Toast.makeText(mContext, "You can't remove notes without an internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }

    public boolean verifyConnection() {
        ConnectionStatus connection = ConnectionStatus.getInstance(mContext);
        boolean isConnected = connection.isOnline();
        return isConnected;
    }
}


