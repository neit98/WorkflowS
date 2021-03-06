package com.example.workflow_s.ui.home;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workflow_s.R;
import com.example.workflow_s.model.Checklist;
import com.example.workflow_s.model.ChecklistMember;
import com.example.workflow_s.model.Task;
import com.example.workflow_s.ui.activity.ActivityFragment;
import com.example.workflow_s.ui.checklist.ChecklistFragment;
import com.example.workflow_s.ui.checklist.adapter.SwipeToDeleteCallBack;
import com.example.workflow_s.ui.home.adapter.ChecklistProgressAdapter;
import com.example.workflow_s.ui.home.adapter.TodayTaskAdapter;
import com.example.workflow_s.ui.notification.NotificationFragment;
import com.example.workflow_s.ui.template.TemplateFragment;
import com.example.workflow_s.utils.CommonUtils;
import com.example.workflow_s.utils.SharedPreferenceUtils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Workflow_S
 * Created by TinhPV on 2019-06-26
 * Copyright © 2019 TinhPV. All rights reserved
 **/


public class HomeFragment extends Fragment implements View.OnClickListener,
        HomeContract.HomeView, ChecklistProgressAdapter.MenuListener {

    View view;
    private Button btnTemplate, btnChecklist, btnActivity, btnViewAllChecklist, btnViewAllActivities;

    private RecyclerView checklistProgressRecyclerView, todayTaskRecyclerView;
    private ChecklistProgressAdapter mChecklistProgressAdapter;
    private TodayTaskAdapter mTodayTaskAdapter;
    private RecyclerView.LayoutManager checklistProgressLayoutManager, todayTaskLayoutManager;

    private TextView tvCompletedChecklist, tvInProgressChecklist, tvOverdueChecklist;

    private HomeContract.HomePresenter mPresenter;

    private ShimmerFrameLayout mChecklistShimmerFrameLayout, mTaskShimmerFrameLayout;
    private LinearLayout mCheckListDataStatusMessage, mTaskDataStatusMessage;

    private String userId, orgId;
    private ArrayList<Checklist> checklists;
    private int change, completedChecklistNum, progressChecklistNum, overdueChecklistNum;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        if (change == 1) {
            inflater.inflate(R.menu.menu_new_noti, menu);
        } else {
            inflater.inflate(R.menu.menu_home, menu);
        }
       // getActivity().setTitle("Home");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notif:
                SharedPreferenceUtils.saveNotificationChange(getContext(), 0);
                CommonUtils.replaceFragments(getContext(), NotificationFragment.class, null, false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#E9E9E9")));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mChecklistShimmerFrameLayout = view.findViewById(R.id.checklist_shimmer_view_container);
        mTaskShimmerFrameLayout = view.findViewById(R.id.task_shimmer_view_container);
        mCheckListDataStatusMessage = view.findViewById(R.id.checklist_data_notfound_message);
        mTaskDataStatusMessage = view.findViewById(R.id.task_data_notfound_message);

        tvCompletedChecklist = view.findViewById(R.id.tv_completed_checklist_number);
        tvInProgressChecklist = view.findViewById(R.id.tv_progress_checklist_number);
        tvOverdueChecklist = view.findViewById(R.id.tv_overdue_checklist_number);

        makeDashboardButtonLookGood();
        setupChecklistRV();
        setupTaskRV();
        initData();
        onClickChecklistProgress();

        String token = FirebaseInstanceId.getInstance().getToken();
        if (token == null) {
            Log.i("Token", "Token is null");
        } else {
            Log.i("Token", token);
            //Log.i("UserToken", SharedPreferenceUtils.retrieveData(view.getContext(), view.getContext().getString(R.string.pref_token)));
        }

    }

    private void onClickChecklistProgress() {
        tvCompletedChecklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt("status_checklist", 1);
                CommonUtils.replaceFragments(view.getContext(), ChecklistFragment.class, args, true);
            }
        });

        tvInProgressChecklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt("status_checklist", 2);
                CommonUtils.replaceFragments(view.getContext(), ChecklistFragment.class, args, true);
            }
        });

        tvOverdueChecklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt("status_checklist", 3);
                CommonUtils.replaceFragments(view.getContext(), ChecklistFragment.class, args, true);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        mChecklistShimmerFrameLayout.setVisibility(View.VISIBLE);
        mChecklistShimmerFrameLayout.startShimmerAnimation();
        mTaskShimmerFrameLayout.startShimmerAnimation();
    }

    @Override
    public void onClick(View v) {
        Bundle args = new Bundle();
        args.putInt("status_checklist", 0);
        switch (v.getId()) {
            case R.id.btn_activity:
                CommonUtils.replaceFragments(getContext(), ActivityFragment.class, null, true);
                getActivity().setTitle("Activity");
                break;

            case R.id.btn_checklist:

                CommonUtils.replaceFragments(getContext(), ChecklistFragment.class, args, true);
                getActivity().setTitle("Active checklist");
                break;

            case R.id.btn_template:
                CommonUtils.replaceFragments(getContext(), TemplateFragment.class, null, true);
                getActivity().setTitle("Template");
                break;

            case R.id.bt_view_all_checklist:
                CommonUtils.replaceFragments(getContext(), ChecklistFragment.class, args, true);
                getActivity().setTitle("Active checklist");
                break;

            case R.id.bt_view_all_task:
                CommonUtils.replaceFragments(getContext(), ActivityFragment.class, null, true);
                getActivity().setTitle("Activity");
                break;
        } // end switch

    }

    private void initData() {
        completedChecklistNum = 0;
        progressChecklistNum = 0;
        overdueChecklistNum = 0;

        mPresenter = new HomePresenterImpl(this, new HomeInteractor());

        userId = SharedPreferenceUtils.retrieveData(getContext(), getString(R.string.pref_userId));
        orgId = SharedPreferenceUtils.retrieveData(getContext(), getString(R.string.pref_orgId));
        mPresenter.loadRunningChecklists(orgId);
        mPresenter.loadDueTasks(orgId, userId);

        //get notification change
        change = SharedPreferenceUtils.retrieveDataInt(getContext(), getString(R.string.notify));
    }

    private void setupTaskRV() {
        todayTaskRecyclerView = view.findViewById(R.id.rv_today_task);
        todayTaskRecyclerView.setHasFixedSize(true);
        todayTaskLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        todayTaskRecyclerView.setLayoutManager(todayTaskLayoutManager);

        mTodayTaskAdapter = new TodayTaskAdapter();
        todayTaskRecyclerView.setAdapter(mTodayTaskAdapter);
    }

    private void setupChecklistRV() {
        checklistProgressRecyclerView = view.findViewById(R.id.rv_checklist_progress);
        checklistProgressRecyclerView.setHasFixedSize(true);
        checklistProgressLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        checklistProgressRecyclerView.setLayoutManager(checklistProgressLayoutManager);

        mChecklistProgressAdapter = new ChecklistProgressAdapter(this, getContext());
        checklistProgressRecyclerView.setAdapter(mChecklistProgressAdapter);
    }

    private void makeDashboardButtonLookGood() {
        btnTemplate = view.findViewById(R.id.btn_template);
        btnChecklist = view.findViewById(R.id.btn_checklist);
        btnActivity = view.findViewById(R.id.btn_activity);
        btnViewAllChecklist = view.findViewById(R.id.bt_view_all_checklist);
        btnViewAllActivities = view.findViewById(R.id.bt_view_all_task);

        btnTemplate.setOnClickListener(this);
        btnChecklist.setOnClickListener(this);
        btnActivity.setOnClickListener(this);
        btnViewAllChecklist.setOnClickListener(this);
        btnViewAllActivities.setOnClickListener(this);

        btnTemplate.setBackgroundResource(0);
        btnChecklist.setBackgroundResource(0);
        btnActivity.setBackgroundResource(0);
    }

    private void countChecklistByType(Checklist checklist) {
        if (checklist.getTemplateStatus().equals("Done")) {
            completedChecklistNum++;
        } else if (checklist.getExpired()) {
            overdueChecklistNum++;
        } else {
            progressChecklistNum++;
        }
    }

    private void filterDueTimeofChecklist(Checklist checklist) {
        String time;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (checklist.getDueTime() != null) {
            String dateSelected = checklist.getDueTime().split("T")[0];
            String timeSelected = checklist.getDueTime().split("T")[1];
            Date currentTime = Calendar.getInstance().getTime();
            String dueTime = dateSelected + " " + timeSelected;
            try {
                Date overdue = sdf.parse(dueTime);
                long totalTime = overdue.getTime() - currentTime.getTime();
                time = String.format("%dh",
                        TimeUnit.MILLISECONDS.toHours(totalTime));
                Log.i("checlistDueTime", time);
                if (Integer.parseInt(time.split("h")[0]) == 0) {
                    time = String.format("%dm",
                            TimeUnit.MILLISECONDS.toMinutes(totalTime));
                    if (Integer.parseInt(time.split("m")[0]) <= 0) {
                        checklist.setExpired(true);
                    } else {
                        checklist.setExpired(false);
                    }
                }  else if (Integer.parseInt(time.split("h")[0]) <= 0){
                    checklist.setExpired(true);
                } else {
                    checklist.setExpired(false);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setDataToChecklistRecyclerView(ArrayList<Checklist> datasource) {
        completedChecklistNum = 0;
        progressChecklistNum = 0;
        overdueChecklistNum = 0;

        mChecklistShimmerFrameLayout.stopShimmerAnimation();
        mChecklistShimmerFrameLayout.setVisibility(View.GONE);

        if (datasource.size() == 0) {
            mCheckListDataStatusMessage.setVisibility(View.VISIBLE);
        } else {
            checklists = new ArrayList<>();
            for (Checklist checklist : datasource) {
                filterDueTimeofChecklist(checklist);

                if (!checklist.getExpired() && !checklist.getTemplateStatus().equals("Done")) {
                    if (checklist.getUserId().equals(userId)) {
                        checklists.add(checklist);
                    }
                    else {
                        List<ChecklistMember> listMember = checklist.getChecklistMembers();
                        if (listMember != null) {

                            for (ChecklistMember member : listMember) {
                                if (member.getUserId().equals(userId)) {
                                    checklists.add(checklist);
                                } // end if
                            } // end for
                        } // end if
                    }
                }
            }

            if (checklists.size() != 0) {
                mChecklistProgressAdapter.setChecklists(checklists);
            } else {
                mCheckListDataStatusMessage.setVisibility(View.VISIBLE);
            }

            // categorize
            for (Checklist checklist : datasource) {
                if (checklist.getUserId().equals(userId)) {
                    countChecklistByType(checklist);
                } else {
                    List<ChecklistMember> listMember = checklist.getChecklistMembers();
                    if (listMember != null) {

                        for (ChecklistMember member : listMember) {
                            if (member.getUserId().equals(userId)) {
                                countChecklistByType(checklist);
                            } // end if
                        } // end for
                    } // end if
                }
            }

            ValueAnimator animator = ValueAnimator.ofInt(0, completedChecklistNum);
            animator.setDuration(200);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    tvCompletedChecklist.setText(animation.getAnimatedValue().toString());
                }
            });

            animator.start();
            animator = ValueAnimator.ofInt(0, overdueChecklistNum);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    tvOverdueChecklist.setText(animation.getAnimatedValue().toString());
                }
            });

            animator.start();

            animator = ValueAnimator.ofInt(0, progressChecklistNum);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    tvInProgressChecklist.setText(animation.getAnimatedValue().toString());
                }
            });

            animator.start();
        }

    }

    @Override
    public void setDataToTasksRecyclerView(ArrayList<Task> datasource) {
        mTaskShimmerFrameLayout.stopShimmerAnimation();
        mTaskShimmerFrameLayout.setVisibility(View.INVISIBLE);
        if (datasource.size() == 0) {
            mTaskDataStatusMessage.setVisibility(View.VISIBLE);
        } else {
            //tasks = new ArrayList<>();
            mTodayTaskAdapter.setTasks(datasource);
        }
    }

    @Override
    public void onFailGetChecklist() {
        mChecklistShimmerFrameLayout.stopShimmerAnimation();
        mChecklistShimmerFrameLayout.setVisibility(View.INVISIBLE);
        mCheckListDataStatusMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailGetTask() {
        mTaskShimmerFrameLayout.stopShimmerAnimation();
        mTaskShimmerFrameLayout.setVisibility(View.INVISIBLE);
        mTaskDataStatusMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishDeleteChecklist() {
        //mPresenter.loadRunningChecklists(orgId);
    }


    @Override
    public void onClickMenu(int checklistId, String checklistName, String checklistUserId, String action, int position) {
        if (action.equals("delete")) {
            handleShowConfirmDialog(checklistId, checklistUserId, position);
        } else { // rename
            prepareShowRenameDialog(checklistId, checklistName, checklistUserId, position);
        }
    }

    private void prepareShowRenameDialog(final int checklistId, String checklistName, String checklistUserId, final int position) {
        if (userId.equals(checklistUserId)) {
            final Dialog changeDialog = new Dialog(getContext());
            changeDialog.setContentView(R.layout.dialog_edit_checklist_name);
            changeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            final EditText edtChecklistName = changeDialog.findViewById(R.id.edt_name);
            edtChecklistName.setText(checklistName);

            Button saveName = changeDialog.findViewById(R.id.bt_save);
            saveName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newName = edtChecklistName.getText().toString().trim();
                    if (newName.length() == 0) {
                        Animation shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_animation);
                        edtChecklistName.startAnimation(shakeAnimation);
                    } else {
                        checklists.get(position).setName(newName);
                        mChecklistProgressAdapter.notifyDataSetChanged();
                        mPresenter.setNameOfChecklist(checklistId, newName);
                        changeDialog.dismiss();
                    }
                }
            });

            Button cancel = changeDialog.findViewById(R.id.bt_cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeDialog.dismiss();
                }
            });

            changeDialog.show();
        } else {
            final Dialog errorDialog = new Dialog(getContext());
            errorDialog.setContentView(R.layout.dialog_error_task);
            errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView msg = errorDialog.findViewById(R.id.tv_error_message);
            msg.setText("You're not the owner, so cannot delete!");

            Button btnOk = errorDialog.findViewById(R.id.btn_ok);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorDialog.cancel();
                }
            });
            errorDialog.show();
        }

    }

    private void handleShowConfirmDialog(final int deletedChecklistId, String checklistUserId, final int position) {
        if (userId.equals(checklistUserId)) {
            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.dialog_confirm_delete_checklist);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            Button confirmButton = dialog.findViewById(R.id.btn_confirm);
            Button cancelButton = dialog.findViewById(R.id.btn_cancel);

            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v2) {
                    mPresenter.deleteChecklist(deletedChecklistId, userId);
                    checklists.remove(position);
                    mChecklistProgressAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mChecklistProgressAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
        } else {
            final Dialog errorDialog = new Dialog(getContext());
            errorDialog.setContentView(R.layout.dialog_error_task);
            errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView msg = errorDialog.findViewById(R.id.tv_error_message);
            msg.setText("You're not the owner, so cannot delete!");

            Button btnOk = errorDialog.findViewById(R.id.btn_ok);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorDialog.cancel();
                }
            });
            errorDialog.show();
        }
    }
}
