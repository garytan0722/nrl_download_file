package com.download.nrl_download_file.fragment;

import android.animation.ValueAnimator;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.download.nrl_download_file.R;
import com.poliveira.parallaxrecyclerview.ParallaxRecyclerAdapter;
import com.shinelw.library.ColorArcProgressBar;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_home.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_home extends Fragment {
    private RecyclerView mRecyclerView;
    private OnFragmentInteractionListener mListener;
    private ColorArcProgressBar bar;
    public Fragment_home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_home.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_home newInstance(String param1, String param2) {
        Fragment_home fragment = new Fragment_home();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root_view=inflater.inflate(R.layout.fragment_home,container,false);
        mRecyclerView = (RecyclerView) root_view.findViewById(R.id.recyclerView);
        createAdapter(mRecyclerView);

        return root_view;
    }
    private void createAdapter(RecyclerView recyclerView) {
        final List<String> content = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            content.add("item " + i);
        }

        final ParallaxRecyclerAdapter<String> adapter = new ParallaxRecyclerAdapter<String>(content) {
            @Override
            public void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, ParallaxRecyclerAdapter<String> adapter, int i) {
                ((ViewHolder) viewHolder).textView.setText(adapter.getData().get(i));
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolderImpl(ViewGroup viewGroup, final ParallaxRecyclerAdapter<String> adapter, int i) {
                return new ViewHolder(getActivity().getLayoutInflater().inflate(R.layout.fragment_home_item, viewGroup, false));
            }

            @Override
            public int getItemCountImpl(ParallaxRecyclerAdapter<String> adapter) {
                return content.size();
            }
        };

        adapter.setOnClickEvent(new ParallaxRecyclerAdapter.OnClickEvent() {
            @Override
            public void onClick(View v, int position) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        View headerview = getActivity().getLayoutInflater().inflate(R.layout.fragment_home_header, recyclerView, false);
        bar=(ColorArcProgressBar)headerview.findViewById(R.id.bar);
        bar.setCurrentValues(88);
        bar.setMaxValues(88);
        adapter.setParallaxHeader(headerview, recyclerView);
        adapter.setData(content);
        recyclerView.setAdapter(adapter);

    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
