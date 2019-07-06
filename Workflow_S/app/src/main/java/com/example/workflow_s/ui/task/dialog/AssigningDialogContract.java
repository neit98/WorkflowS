package com.example.workflow_s.ui.task.dialog;

import com.example.workflow_s.model.TaskMember;
import com.example.workflow_s.model.User;
import com.example.workflow_s.ui.organization.OrganizationContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Workflow_S
 * Created by TinhPV on 2019-07-06
 * Copyright © 2019 TinhPV. All rights reserved
 **/


public interface AssigningDialogContract {
    interface AssigningDialogPresenter {
        void getOrgMember(int orgId);
        void assignUser(TaskMember taskMember);
        void unassignUser(int memberId);
    }

    interface AssigningDialogView {
        void finishedGetMember(List<User> userList);
        void finishedAssignMember();
        void finishedUnassignMember();
    }

    interface GetDataAssignInteractor {
        interface OnFinishedGetMembersListener {
            void onFinishedGetMembers(ArrayList<User> users);
            void onFailure(Throwable t);
        }

        interface OnFinishedAssignMemberListener {
            void onFinishedAssigning();
            void onFailure(Throwable t);
        }

        interface OnFinishedUnassignMemberListener {
            void onFinishedUnassigning();
            void onFailure(Throwable t);
        }


        void getAllMember(int orgId, OnFinishedGetMembersListener onFinishedListener);
        void assignTaskMember(TaskMember member, OnFinishedAssignMemberListener listener);
        void unassignTaskMember(int memberId, OnFinishedUnassignMemberListener listener);

    }
}
