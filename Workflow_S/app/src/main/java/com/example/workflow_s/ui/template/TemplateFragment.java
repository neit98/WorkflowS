package com.example.workflow_s.ui.template;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.workflow_s.R;
import com.example.workflow_s.model.Template;
import com.example.workflow_s.ui.template.adapter.TemplateAdapter;
import com.example.workflow_s.ui.template.dialog_fragment.TemplateDialogFragment;
import com.example.workflow_s.utils.SharedPreferenceUtils;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Workflow_S
 * Created by TinhPV on 2019-06-27
 * Copyright © 2019 TinhPV. All rights reserved
 **/


public class TemplateFragment extends Fragment implements TemplateContract.TemplateView, TemplateDialogFragment.DataBackContract, View.OnClickListener {

    private final static String TAG = "TEMPLATE_FRAGMENT";

    View view;
    private Button categoryButton;
    private RecyclerView templateRecyclerView;
    private TemplateAdapter mAdapter;
    private RecyclerView.LayoutManager templateLayoutManager;

    private TemplateContract.TemplatePresenter mTemplatePresenter;

    private ShimmerFrameLayout mShimmerFrameLayout;
    private LinearLayout mTemplateDataStatusMessage;

    private ArrayList<String> categoryList;
    private ArrayList<Template> templateList, categorizedTemplateList;
    private String selectedCategory;

    private List<String> listSearch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search_item);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        EditText searchEditText = searchView.findViewById(R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.colorPrimaryText));

        searchEditText.setHint("Search");
        searchEditText.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));

        Typeface myFont = ResourcesCompat.getFont(getContext(), R.font.avenir_light);
        searchEditText.setTypeface(myFont);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
    }


    private void prepareShowingCategoryDialog() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("category_list", categoryList);
        bundle.putString("selected_category", selectedCategory);
        TemplateDialogFragment templateDialogFragment = TemplateDialogFragment.newInstance();
        templateDialogFragment.setTargetFragment(this, 0);
        templateDialogFragment.setArguments(bundle);
        templateDialogFragment.show(getActivity().getSupportFragmentManager(), TAG);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_template, container, false);
        getActivity().setTitle("Template");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mShimmerFrameLayout = view.findViewById(R.id.template_shimmer_view);
        mTemplateDataStatusMessage = view.findViewById(R.id.template_data_notfound_message);
        categoryButton = view.findViewById(R.id.bt_template_category);
        categoryButton.setOnClickListener(this);
        categoryButton.setText("All");
        selectedCategory = "All";
        setupTemplateRV();
        initData();
    }


    @Override
    public void onResume() {
        super.onResume();
        mShimmerFrameLayout.startShimmerAnimation();
    }

    //In Fragment
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setTitle("Home");
    }

    private void initData() {
        mTemplatePresenter = new TemplatePresenterImpl(this, new TemplateInteractor());
        String orgId = SharedPreferenceUtils.retrieveData(getActivity(), getString(R.string.pref_orgId));
        // done - HARDCODE HERE
        mTemplatePresenter.requestTemplateData(orgId);
    }

    private void setupTemplateRV() {
        templateRecyclerView = view.findViewById(R.id.rv_template);
        templateRecyclerView.setHasFixedSize(true);
        templateLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        templateRecyclerView.setLayoutManager(templateLayoutManager);
        mAdapter = new TemplateAdapter();
        templateRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void finishGetTemplates(List<Template> templateList) {
        mShimmerFrameLayout.setVisibility(View.INVISIBLE);
        mShimmerFrameLayout.stopShimmerAnimation();
        if (templateList.size() == 0) {
            mTemplateDataStatusMessage.setVisibility(View.VISIBLE);
        } else {
            collectCategoryName(templateList);
            mAdapter.setTemplateList(templateList);
        } // end if


        listSearch = new ArrayList<>();
        for (Template item : templateList) {
            listSearch.add(item.getName());
        }
    }

    private void collectCategoryName(List<Template> templateList) {

        this.templateList = (ArrayList<Template>) templateList;
        categoryList = new ArrayList<>();
        categoryList.add("All");
        for (Template template : templateList) {
            if (!categoryList.contains(template.getCategory())) {
                categoryList.add(template.getCategory());
            }
        } // end for
    }

    @Override
    public void onFinishSelectCategory(String category) {
        categoryButton.setText(category);
        categorizeTemplate(category);
    }

    private void categorizeTemplate(String category) {
        selectedCategory = category;
        if (category.equals("All")) {
            mAdapter.setTemplateList(templateList);
        } else {
            categorizedTemplateList = new ArrayList<>();
            for (Template template : this.templateList) {
                if (template.getCategory().equals(category)) {
                    categorizedTemplateList.add(template);
                } // end if
            } // end for

            mAdapter.setTemplateList(categorizedTemplateList);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_template_category) {
            prepareShowingCategoryDialog();
        }
    }
}
