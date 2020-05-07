package com.example.appnotepad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomNoteAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private ArrayList<Note> listNote;
    private ArrayList<Note> getNote;
    private CustomFilter filter;


    public CustomNoteAdapter(Context context, ArrayList<Note> listNote) {
        this.context = context;
        this.listNote = listNote;
        this.getNote = listNote;
    }


    @Override
    public int getCount() {
        return listNote.size();
    }

    @Override
    public Object getItem(int position) {
        return listNote.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.one_row_note, null);
        }
        Note note = (Note) getItem(position);

        if (note != null) {
            TextView txtTitle = (TextView) view.findViewById(R.id.textView_title);
            txtTitle.setText(note.getNoteTitle());
            TextView txtDate = (TextView) view.findViewById(R.id.textView_date);
            txtDate.setText(note.getNoteDate().toString());

        }
        return view;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CustomFilter();
        }
        return filter;
    }

    public class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                constraint = constraint.toString().toUpperCase();
                ArrayList<Note> filterNote = new ArrayList<>();
                for (int i = 0; i < getNote.size(); i++) {
                    if (getNote.get(i).getNoteContent().toUpperCase().contains(constraint)) {
                        Note note = new Note(getNote.get(i).getNoteId(), getNote.get(i).getNoteTitle(), getNote.get(i).getNoteContent(), getNote.get(i).getNoteDate());
                        filterNote.add(note);
                    }
                }
                results.count = filterNote.size();
                results.values = filterNote;
            } else {
                results.count = getNote.size();
                results.values = getNote;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listNote = (ArrayList<Note>) results.values;
            notifyDataSetChanged();
        }
    }

}
