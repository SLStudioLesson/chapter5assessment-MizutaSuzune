package com.taskapp.logic;

import com.taskapp.dataaccess.LogDataAccess;
import com.taskapp.dataaccess.TaskDataAccess;
import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.model.User;
import com.taskapp.model.Task;
import com.taskapp.exception.AppException;
import com.taskapp.model.Log;

import java.util.List;
import java.time.LocalDate;


public class TaskLogic {
    private final TaskDataAccess taskDataAccess;
    private final LogDataAccess logDataAccess;
    private final UserDataAccess userDataAccess;


    public TaskLogic() {
        taskDataAccess = new TaskDataAccess();
        logDataAccess = new LogDataAccess();
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param taskDataAccess
     * @param logDataAccess
     * @param userDataAccess
     */
    public TaskLogic(TaskDataAccess taskDataAccess, LogDataAccess logDataAccess, UserDataAccess userDataAccess) {
        this.taskDataAccess = taskDataAccess;
        this.logDataAccess = logDataAccess;
        this.userDataAccess = userDataAccess;
    }

    /**
     * 全てのタスクを表示します。
     * 設問2
     * TaskDataAccess.findAllでtasks.csvの一覧をTask型のListで取得する
     * List1つずつに下記処理を行い、表示する
     * UserDataAccess.findByCodeへTaskListより取得したrepUserを設定し、該当するユーザ名を取得する
     * 自分以外→担当者名、自分→あなたが担当しています
     * statusを0→未着手、1→着手中、2→完了へ読み替える
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findAll()
     * @param loginUser ログインユーザー
     */
    public void showAll(User loginUser) {
        List<Task> tasklist = taskDataAccess.findAll();
        for (Task task : tasklist) {
            int code = task.getRepUser().getCode();
            User taskuser = userDataAccess.findByCode(code);
            String taskusername = taskuser.getName(); // タスクごとのユーザ名

            if (code == loginUser.getCode()) {
                taskusername = "あなた";
            }
            String status;
            if (task.getStatus() == 1) {
                status = "着手中";
            } else if (task.getStatus() == 2) {
                status = "完了";
            } else {
                status = "未着手";
            }
            System.out.println("タスク名：" + task.getName() + ", " + "担当者名：" + taskusername + "が担当しています" +
            ", ステータス：" + status);
            }
        }

    /**
     * 新しいタスクを保存します。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#save(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param name タスク名
     * @param repUserCode 担当ユーザーコード
     * @param loginUser ログインユーザー
     * @throws AppException ユーザーコードが存在しない場合にスローされます
     */
    public void save(int code, String name, int repUserCode,
                    User loginUser) throws AppException {
        User user = userDataAccess.findByCode(repUserCode);
        
        if (user == null) {
            throw new AppException("存在するユーザーコードを入力してください");
        }
        
        Task task = new Task(code,name,0,user);
        taskDataAccess.save(task);

        Log log = new Log(code,loginUser.getCode(),0,LocalDate.now());
        logDataAccess.save(log);

        System.out.println(name + "の登録が完了しました。");
        
    }

    /**
     * タスクのステータスを変更します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#update(com.taskapp.model.Task)
     * @see com.taskapp.dataaccess.LogDataAccess#save(com.taskapp.model.Log)
     * @param code タスクコード
     * @param status 新しいステータス
     * @param loginUser ログインユーザー
     * @throws AppException タスクコードが存在しない、またはステータスが前のステータスより1つ先でない場合にスローされます
     */
    public void changeStatus(int code, int status,
                            User loginUser) throws AppException {
        Task task = taskDataAccess.findByCode(code);
        
        if (task == null) {
            throw new AppException("存在するタスクコードを入力してください");
        }

        if (status - 1 != task.getStatus()) {
            throw new AppException("ステータスは、前のステータスより1つ先のもののみを選択してください");
        }

        Task updateTask = new Task(code,task.getName(),status,task.getRepUser());
        taskDataAccess.update(updateTask);

        Log log = new Log(code,loginUser.getCode(),status,LocalDate.now());
        logDataAccess.save(log);

        System.out.println("ステータスの変更が完了しました。");

    }

    /**
     * タスクを削除します。
     *
     * @see com.taskapp.dataaccess.TaskDataAccess#findByCode(int)
     * @see com.taskapp.dataaccess.TaskDataAccess#delete(int)
     * @see com.taskapp.dataaccess.LogDataAccess#deleteByTaskCode(int)
     * @param code タスクコード
     * @throws AppException タスクコードが存在しない、またはタスクのステータスが完了でない場合にスローされます
     */
    // public void delete(int code) throws AppException {
    // }
}