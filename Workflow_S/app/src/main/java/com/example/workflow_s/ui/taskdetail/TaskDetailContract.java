package com.example.workflow_s.ui.taskdetail;

import com.example.workflow_s.model.Comment;
import com.example.workflow_s.model.ContentDetail;
import com.example.workflow_s.model.Task;
import com.example.workflow_s.model.TaskMember;
import com.example.workflow_s.ui.task.TaskContract;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;

public interface  TaskDetailContract {

    //view
    interface  TaskDetailView {
        void setDataToView(ArrayList<ContentDetail> datasource);
        void finishedSaveContent();
        void finishedGetTaskDetail(Task task);
        void finishedCompleteTask();
        void finishedUploadImage(int orderContent);
        void finishGetTaskMember(List<TaskMember> memberList);
        void finishLoadComment(List<Comment> commentList);
    }

    //presenter
    interface TaskDetailPresenter {
        void onDestroy();
        void loadComment(int taskId);
        void loadDetails(int taskId);
        void getTask(int taskId);
        void getTaskMember(int taskId);
        void completeTask(String userId, int taskId, String taskStatus);
        void uploadImage(int contentId, int orderContent, MultipartBody.Part photo);
        void updateTaskDetail(List<ContentDetail> detailList);
    }

    //model
    interface GetTaskDetailDataInteractor {

        interface OnFinishedGetTaskDetailListener {
            void onFinishedGetTaskDetail (ArrayList<ContentDetail> taskDetailArrayList);
            void onFailure(Throwable t);
        }

        interface OnFinishedGetTaskListener {
            void onFinishedGetTask(Task task);
            void onFailure(Throwable t);
        }


        interface OnFinishedSaveContentListener {
            void onFinishedSave();
            void onFailure(Throwable t);
        }

        interface OnFinishedChangeTaskStatusListener {
            void onFinishedChangeTaskStatus();
            void onFailure(Throwable t);
        }

        interface OnFinishedUploadImageListener {
            void onFinishedUploadImage(int orderContent);
            void onFailure(Throwable t);
        }

        interface OnFinishedGetTaskMemberListener {
            void onFinishedGetTaskMembers(List<TaskMember> taskMemberList);
            void onFailure(Throwable t);
        }

        interface OnFinishedLoadCommentListener {
            void onFinishedGetComments(List<Comment> commentList);
            void onFailure(Throwable t);
        }

        void getAllComments(int taskId, OnFinishedLoadCommentListener listener);
        void getTaskMember(int taskId, OnFinishedGetTaskMemberListener listener);
        void getTaskById(int taskId, OnFinishedGetTaskListener listener);
        void uploadImage(int contentId, int orderContent, MultipartBody.Part photo, OnFinishedUploadImageListener listener);
        void completeTask(String userId, int taskId, String taskStatus, OnFinishedChangeTaskStatusListener listener);
        void getContentDetail(int taskId, OnFinishedGetTaskDetailListener onFinishedListener);
        void saveDetail(List<ContentDetail> list, OnFinishedSaveContentListener saveContentListener);
    }
}
