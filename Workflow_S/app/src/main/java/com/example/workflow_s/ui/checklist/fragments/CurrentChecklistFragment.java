package com.example.workflow_s.ui.checklist.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import com.example.workflow_s.R;
import com.example.workflow_s.model.Checklist;
import com.example.workflow_s.model.Task;
import com.example.workflow_s.model.TaskMember;
import com.example.workflow_s.model.User;
import com.example.workflow_s.ui.checklist.ChecklistContract;
import com.example.workflow_s.ui.checklist.ChecklistInteractor;
import com.example.workflow_s.ui.checklist.ChecklistPresenterImpl;
import com.example.workflow_s.ui.checklist.adapter.CurrentChecklistAdapter;
import com.example.workflow_s.utils.SharedPreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class CurrentChecklistFragment extends Fragment implements ChecklistContract.ChecklistView {

    private static final String NAME_ARG = "CurrentChecklist";

    View view;
    private CurrentChecklistAdapter mCurrentChecklistAdapter;
    private RecyclerView checklistRecyclerView;
    private RecyclerView.LayoutManager checklistLayoutManager;

    private ArrayList<Checklist> currentChecklist;
    private String userId, orgId;

    private ChecklistContract.ChecklistPresenter mPresenter;
    //private LinearLayout mChecklistDataStatusMessage;

    public CurrentChecklistFragment() {}

    //static constructor
    public static CurrentChecklistFragment newInstance() {
        final CurrentChecklistFragment currentChecklistFragment = new CurrentChecklistFragment();
        // The 1 below is an optimization, being the number of arguments that will
        // be added to this bundle.  If you know the number of arguments you will add
        // to the bundle it stops additional allocations of the backing map.  If
        // unsure, you can construct Bundle without any arguments
        //final Bundle args = new Bundle(1);
        // This stores the argument as an argument in the bundle.  Note that even if
        // the 'name' parameter is NULL then this will work, so you should consider
        // at this point if the parameter is mandatory and if so check for NULL and
        // throw an appropriate error if so
        return currentChecklistFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_current_checklists, container, false);

        return view;
    }


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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mCurrentChecklistAdapter.getFilter().filter(s);
                return false;
            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //mChecklistDataStatusMessage = view.findViewById(R.id.checklist_data_notfound_message);
            setupChecklistRV();
            initData();
    }

    private void initData() {
        mPresenter = new ChecklistPresenterImpl(this, new ChecklistInteractor());
        userId = SharedPreferenceUtils.retrieveData(getActivity(), getString(R.string.pref_userId));
        orgId = SharedPreferenceUtils.retrieveData(getActivity(), getString(R.string.pref_orgId));
        mPresenter.loadAllChecklist(orgId);
    }

    private void setupChecklistRV() {
        checklistRecyclerView = view.findViewById(R.id.rv_checklist);
        checklistRecyclerView.setHasFixedSize(true);
        checklistLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        checklistRecyclerView.setLayoutManager(checklistLayoutManager);

        mCurrentChecklistAdapter = new CurrentChecklistAdapter();
        checklistRecyclerView.setAdapter(mCurrentChecklistAdapter);
    }

    @Override
    public void setDataToChecklistRecyclerView(ArrayList<Checklist> datasource) {
        if (null != datasource) {
            currentChecklist = new ArrayList<>();
            for (Checklist checklist : datasource) {
                if (checklist.getUserId().equals(userId)) {
                    currentChecklist.add(checklist);
                } else {
                    mPresenter.loadFirstTaskFromChecklist(checklist.getId(), checklist);
                } // end if
            } // end for
        } // end if null
        mCurrentChecklistAdapter.setChecklists(currentChecklist);
    }

    @Override
    public void finishFirstTaskFromChecklist(Task task, Checklist parentChecklistOfThisTask) {
        if (task != null) {
            List<TaskMember> taskMemberList = new ArrayList<>(task.getTaskMemberList());
            if (!taskMemberList.isEmpty()) {
                for (int i = 0; i < taskMemberList.size(); i++) {
                    if (taskMemberList.get(i).getUserId().equals(userId)) {
                        currentChecklist.add(parentChecklistOfThisTask);
                    }
                }
            }
            mCurrentChecklistAdapter.setChecklists(currentChecklist);
        } // end if
    }

//


}
