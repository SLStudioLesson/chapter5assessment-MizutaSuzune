package com.taskapp.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.taskapp.logic.TaskLogic;
import com.taskapp.logic.UserLogic;
import com.taskapp.model.User;
import com.taskapp.exception.AppException;

public class TaskUI {
    private final BufferedReader reader;

    private final UserLogic userLogic;

    private final TaskLogic taskLogic;

    private User loginUser;

    public TaskUI() {
        reader = new BufferedReader(new InputStreamReader(System.in));
        userLogic = new UserLogic();
        taskLogic = new TaskLogic();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param reader
     * @param userLogic
     * @param taskLogic
     */
    public TaskUI(BufferedReader reader, UserLogic userLogic, TaskLogic taskLogic) {
        this.reader = reader;
        this.userLogic = userLogic;
        this.taskLogic = taskLogic;
    }

    /**
     * メニューを表示し、ユーザーの入力に基づいてアクションを実行します。
     *
     * @see #inputLogin()
     * @see com.taskapp.logic.TaskLogic#showAll(User)
     * @see #selectSubMenu()
     * @see #inputNewInformation()
     */
    public void displayMenu() {
        System.out.println("タスク管理アプリケーションにようこそ!!");
        inputLogin();

        // メインメニュー
        boolean flg = true;
        while (flg) {
            try {
                System.out.println("以下1~3のメニューから好きな選択肢を選んでください。");
                System.out.println("1. タスク一覧, 2. タスク新規登録, 3. ログアウト");
                System.out.print("選択肢：");
                String selectMenu = reader.readLine();

                System.out.println();

                switch (selectMenu) {
                    case "1":
                        taskLogic.showAll(loginUser);
                        selectSubMenu();
                        break;
                    case "2":
                        inputNewInformation();
                        break;
                    case "3":
                        System.out.println("ログアウトしました。");
                        flg = false;
                        break;
                    default:
                        System.out.println("選択肢が誤っています。1~3の中から選択してください。");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }

    /**
     * ユーザーからのログイン情報を受け取り、ログイン処理を行います。
     * 設問1
     * メールアドレス・パスワードの出力を受け付ける
     * 入力された値をUserlogic(login)へ設定
     * Userlogic(login)からUserDataAccess(findByEmailAndPassword)へ入力値を引き渡す
     * users.csvを1行ずつリードし、入力値と合致するデータがあれば値を設定してloginへ返却
     * loginにて値をチェックし、空であれば例外をスローし値が設定されていればinputloginへ返却
     * inputloginにて値をloginUserに設定し、処理を完了とする
     *
     * @see com.taskapp.logic.UserLogic#login(String, String)
     */
    public void inputLogin() {
        boolean flg = true;
        while (flg) {
            try {
                System.out.println();
                System.out.print("メールアドレスを入力してください：");
                String email = reader.readLine();
                System.out.print("パスワードを入力してください：");
                String password = reader.readLine();
                System.out.println();
                loginUser = userLogic.login(email,password);
                flg = false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * ユーザーからの新規タスク情報を受け取り、新規タスクを登録します。
     * 設問3
     * TaskUI.inputNewInformation()にて、入力値を取得
     * 入力値のチェックはTaskUI.isNumericにて行う
     * 担当するユーザコードがusers.csvに設定されていなければ、AppExceptionをスローする
     * Task.logic.save(int code, String name, int repUserCode,User loginUser)からTaskオブジェクトに設定する
     * TaskオブジェクトをTaskDataAccess.saveへわたし、ファイルへ追加する
     * Logオブジェクトへ値を設定し、LogDataAccess.saveへわたし、ファイルへ追加する
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#save(int, String, int, User)
     */
    public void inputNewInformation() {
        boolean flg = true;
        while (flg) {
                    try {
                    System.out.print("タスクコードを入力してください：");
                    String code = reader.readLine();
                    if (!isNumeric(code)) {
                        System.out.println("コードは半角の数字で入力してください");
                        System.out.println();
                        continue;
                    }
                    System.out.print("タスク名を入力してください：");
                    String name = reader.readLine();
                    if (name.length() > 10) {
                        System.out.println("タスク名は10文字以内で入力してください");
                        System.out.println();
                        continue;
                    }
                    System.out.print("担当するユーザーのコードを選択してください：");
                    String repUserCode = reader.readLine();
                    if (!isNumeric(repUserCode)) {
                        System.out.println("ユーザーのコードは半角の数字で入力してください");
                        System.out.println();
                        continue;
                    }
                    
                    taskLogic.save(Integer.parseInt(code),name,Integer.parseInt(repUserCode),loginUser);

                    flg = false;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (AppException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

    /**
     * タスクのステータス変更または削除を選択するサブメニューを表示します。
     * 設問4
     * メニューを表示し、入力値を元に1. タスクのステータス変更　であればinputChangeInformation
     * 2. メインメニューに戻る　であれば処理を終わりにする
     *
     * @see #inputChangeInformation()
     * @see #inputDeleteInformation()
     */
    public void selectSubMenu() {
        boolean flg = true;
        while (flg) {
            try {
                System.out.println();
                System.out.println("以下1~2から好きな選択肢を選んでください。");
                System.out.println("1. タスクのステータス変更, 2. メインメニューに戻る");
                System.out.print("選択肢：");
                String selectMenu = reader.readLine();

                switch (selectMenu) {
                    case "1":
                        inputChangeInformation();
                        break;
                    case "2":
                        flg = false;
                        System.out.println("メインメニューに戻ります");
                        System.out.println();
                        break;
                    default :
                        System.out.println("選択肢が誤っています。1~2の中から選択してください。");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }

    /**
     * ユーザーからのタスクステータス変更情報を受け取り、タスクのステータスを変更します。
     * 設問4
     * タスクコード・ステータスの入力を受付、バリデーションチェックを行う
     * 入力値をTaskLogic.changeStatusへ設定
     * changeStatusからTaskDataAccess.findByCode(int code)を使用し、入力されたタスクコードのデータの存在チェックを行う
     * 存在しない場合は、AppException("存在するタスクコードを入力してください")
     * タスクのステータスが変更可能か確認(入力値:1のとき、0であること。入力値:2のとき、1であること。)
     * タスクステータスエラーをAppException("ステータスは、前のステータスより1つ先のもののみを選択してください")
     * Task型のオブジェクトに変換し、TaskDataAccess.update(Task task)でファイルを更新する
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#changeStatus(int, int, User)
     */
    public void inputChangeInformation() {
        boolean flg = true;
        while (flg) {
            try {
                System.out.print("ステータスを変更するタスクコードを入力してください：");
                String code = reader.readLine();
                if (!isNumeric(code)) {
                    System.out.println("コードは半角の数字で入力してください");
                    System.out.println();
                    continue;
                }

                System.out.println("どのステータスに変更するか選択してください。");
                System.out.println("1. 着手中, 2. 完了");
                System.out.print("選択肢：");
                String status = reader.readLine();
                if (!isNumeric(status)) {
                    System.out.println("ステータスは半角の数字で入力してください");
                    System.out.println();
                    continue;
                }
                if (!(status.equals("1") || status.equals("2"))) {
                    System.out.println("ステータスは1・2の中から選択してください");
                    System.out.println();
                    continue;
                }

                taskLogic.changeStatus(Integer.parseInt(code),Integer.parseInt(status),loginUser);
                flg = false;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (AppException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * ユーザーからのタスク削除情報を受け取り、タスクを削除します。
     *
     * @see #isNumeric(String)
     * @see com.taskapp.logic.TaskLogic#delete(int)
     */
    // public void inputDeleteInformation() {
    // }

    /**
     * 指定された文字列が数値であるかどうかを判定します。
     * 負の数は判定対象外とする。
     *
     * @param inputText 判定する文字列
     * @return 数値であればtrue、そうでなければfalse
     */
    public boolean isNumeric(String inputText) {
        return inputText.chars().allMatch(c -> Character.isDigit((char) c));
    }
}