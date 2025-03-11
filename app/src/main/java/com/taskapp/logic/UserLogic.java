package com.taskapp.logic;

import com.taskapp.dataaccess.UserDataAccess;
import com.taskapp.model.User;
import com.taskapp.exception.AppException;

public class UserLogic {
    private final UserDataAccess userDataAccess;

    public UserLogic() {
        userDataAccess = new UserDataAccess();
    }

    /**
     * 自動採点用に必要なコンストラクタのため、皆さんはこのコンストラクタを利用・削除はしないでください
     * @param userDataAccess
     */
    public UserLogic(UserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    /**
     * ユーザーのログイン処理を行います。
     *
     * @see com.taskapp.dataaccess.UserDataAccess#findByEmailAndPassword(String, String)
     * @param email ユーザーのメールアドレス
     * @param password ユーザーのパスワード
     * @return ログインしたユーザーの情報
     * @throws AppException メールアドレスとパスワードが一致するユーザーが存在しない場合にスローされます
     */
    public User login(String email, String password) throws AppException {
        User loginUser = userDataAccess.findByEmailAndPassword(email,password);
        
        if (loginUser == null) {
            throw new AppException("既に登録されているメールアドレス、パスワードを入力してください");
        }

        System.out.println("ユーザ名：" + loginUser.getName() + "でログインしました。");
        return loginUser;
    }
}