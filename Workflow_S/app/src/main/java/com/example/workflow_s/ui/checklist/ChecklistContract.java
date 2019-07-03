package com.example.workflow_s.ui.checklist;

import com.example.workflow_s.model.Checklist;

import java.util.ArrayList;

/**
 * Workflow_S
 * Created by TinhPV on 2019-06-30
 * Copyright © 2019 TinhPV. All rights reserved
 **/


public interface ChecklistContract {

    //presenter
    interface ChecklistPresenter {
        void onDestroy();
        void loadAllChecklist(String organizationId);
    }

    //view
    interface ChecklistView {
        void setDataToChecklistRecyclerView(ArrayList<Checklist> datasource);
    }

    //model
    interface GetChecklistsDataInteractor {
        interface OnFinishedGetChecklistListener {
            void onFinishedGetChecklist(ArrayList<Checklist> checklistArrayList);
            void onFailure(Throwable t);
        }

        void getAllChecklist(String organizationId, OnFinishedGetChecklistListener onFinishedGetChecklistListener);
    }
}
