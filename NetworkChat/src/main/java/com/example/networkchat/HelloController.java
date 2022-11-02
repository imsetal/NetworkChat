package com.example.networkchat;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class HelloController {
    static final String address="43.138.134.225:3306";
    //43.138.134.225:3306
    //localhost:3306
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String userinfo_DB_URL = "jdbc:mysql://"+address+"/userinfo?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String friends_list_DB_URL = "jdbc:mysql://"+address+"/friends_list?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String group_list_DB_URL = "jdbc:mysql://"+address+"/group_list?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String chat_DB_URL = "jdbc:mysql://"+address+"/chat?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "Aa928531";
    //Aa928531
    //Aa123456

    public String User;
    public int nowTalking=-1;

    @FXML
    private Label info;
    @FXML
    private Label add_info;
    @FXML
    private Label add_group_info;
    @FXML
    private TextField user_name;
    @FXML
    private TextField user_password;
    @FXML
    private TextField add_user_name;
    @FXML
    private  TextField add_group_name;
    @FXML
    private ListView friends_list;
    @FXML
    private ListView group_list;
    @FXML
    private ListView chat_window;
    @FXML
    private TextArea chat_area;
    @FXML
    private Button send_button;
    @FXML
    private Label chat_info;
    ObservableList<String> old_items = FXCollections.observableArrayList ();

    public HelloController(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Connection conn = null;
                Statement stmt = null;

                try {
                    // 注册 JDBC 驱动
                    Class.forName(JDBC_DRIVER);

                    // 打开链接
                    System.out.println("连接数据库...");
                    conn = DriverManager.getConnection(chat_DB_URL, USER, PASS);

                    // 执行查询
                    String sql;
                    System.out.println(" 实例化Statement对象...");
                    stmt = conn.createStatement();

                    ResultSet rs;

                    while(true){
                        System.out.println("::"+User+"::"+nowTalking);
                        if (nowTalking >0) {
                            ObservableList<String> items = FXCollections.observableArrayList ();
                            sql = "SELECT * FROM chat_"+nowTalking+" order by id desc;";
                            rs = stmt.executeQuery(sql);

                            int num=0;

                            while(rs.next()&&num<255){
                                int id=rs.getInt("id");
                                String send_time=rs.getString("send_time");
                                String send_user=rs.getString("send_user");
                                String send_content=rs.getString("send_content");

                                items.add(send_time+" "+send_user+":"+send_content);
                                num++;
                            }

                            if(!old_items.equals(items)){
                                chat_window.getItems().clear();
                                chat_window.setItems(items);
                                old_items=items;
                            }

                            System.out.println("tick");
                        }
                        TimeUnit.SECONDS.sleep(1);
                    }
                } catch (SQLException se) {
                    // 处理 JDBC 错误
                    se.printStackTrace();
                } catch (Exception e) {
                    // 处理 Class.forName 错误
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @FXML
    public void onloginButtonClick() throws IOException {
        String u_name=user_name.getText();
        String u_password=user_password.getText();

        info.setText("注册中");

        if(u_name==""||u_password==""){
            info.setText("你是不是少填了什么");
        }else{
            info.setText("");
            if(isUser(u_name)==true){
                info.setText("似乎已经注册了 点击登录吧");
            }else{



                getUser(u_name);
                add_user(u_name,u_password);

                // 像这样关闭舞台会绕过 onCloseRequest 事件处理程序（如果有的话）
                Stage stage = (Stage) info.getScene().getWindow();
                // 关闭前通知一下事件处理 stage.getOnCloseRequest().handle(null);
                stage.hide();
                mainView(stage,u_name);


                System.out.println("Using now:"+User);
            }
        }
    }
    @FXML
    public void onsignInButtonClick() throws IOException {
        String u_name=user_name.getText();
        String u_password=user_password.getText();

        System.out.println(u_name+"\n"+u_password);

        info.setText("登入中");

        //System.out.println(u_name);

        if(u_name==""||u_password==""){
            info.setText("你是不是少填了什么");
        }else{
            info.setText("");
            if(isUser(u_name)==false){
                info.setText("似乎是新用户 点击新用户以此信息注册");
            }else{

                if(checkPassword(u_name,u_password)==true){
                    info.setText("登入成功");
                    System.out.println(u_name);
                    getUser(u_name);
                    // 像这样关闭舞台会绕过 onCloseRequest 事件处理程序（如果有的话）
                    Stage stage = (Stage) info.getScene().getWindow();
                    // 关闭前通知一下事件处理 stage.getOnCloseRequest().handle(null);
                    stage.hide();

                    mainView(stage,u_name);


                    System.out.println("Using now:"+User);


                }else{
                    info.setText("密码不对喔");
                }
            }
        }
    }

    public void getUser(String user_name){
        User=user_name;
        System.out.println(user_name);
        System.out.println(User);
    }

    public void mainView(Stage stage,String userName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
        fxmlLoader.setControllerFactory((Class<?> param) -> {return this;});
        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);
        stage.setTitle("LINE");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        User=userName;

        friends_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(newValue);
                        changeChatUser((String) newValue);
                    }
                }).start();
            }
        });

        group_list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(newValue);
                        changeChatGroup((String) newValue);
                    }
                }).start();
            }
        });
    }

    public void changeChatUser(String name){
        //chat_window.getItems().clear();

        Connection conn = null;
        Statement stmt = null;
        ObservableList<String> items = FXCollections.observableArrayList ();
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(friends_list_DB_URL, USER, PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql = "select chat_num from "+User+" where friends_name='"+name+"';";
            ResultSet rs = stmt.executeQuery(sql);

            int chat_num=-1;

            // 展开结果集数据库

            while (rs.next()) {
                // 通过字段检索
                chat_num = rs.getInt("chat_num");
            }

            if(chat_num!=-1)
            nowTalking=chat_num;

            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }// 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public void changeChatGroup(String name){
        //chat_window.getItems().clear();

        Connection conn = null;
        Statement stmt = null;
        ObservableList<String> items = FXCollections.observableArrayList ();
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(group_list_DB_URL, USER, PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql = "select chat_num from user_"+User+" where group_name='"+name+"';";
            ResultSet rs = stmt.executeQuery(sql);

            int chat_num=-1;

            // 展开结果集数据库

            while (rs.next()) {
                // 通过字段检索
                chat_num = rs.getInt("chat_num");
            }

            if(chat_num!=-1)
            nowTalking=chat_num;

            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }// 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public void changeChatWindows(){


        Connection conn = null;
        Statement stmt = null;
        ObservableList<String> items = FXCollections.observableArrayList ();
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(chat_DB_URL, USER, PASS);

            // 执行查询
            String sql;
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            sql = "SELECT * FROM chat_"+nowTalking+" order by id desc;";
            ResultSet rs = stmt.executeQuery(sql);

            int num=0;

            while(rs.next()&&num<255){
                int id=rs.getInt("id");
                String send_time=rs.getString("send_time");
                String send_user=rs.getString("send_user");
                String send_content=rs.getString("send_content");

                items.add(send_time+" "+send_user+":"+send_content);
            }


            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }// 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        if(!old_items.equals(items)){
            chat_window.getItems().clear();
            chat_window.setItems(items);
            old_items=items;
        }

    }

    public static boolean isUser(String userName) {
        Connection conn = null;
        Statement stmt = null;
        boolean isUser=false;
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(userinfo_DB_URL, USER, PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql = "select * from user_info where user_name='"+userName+"';";
            ResultSet rs = stmt.executeQuery(sql);



            // 展开结果集数据库

            while (rs.next()) {
                // 通过字段检索
                int id = rs.getInt("id");
                String user_name = rs.getString("user_name");
                String user_password = rs.getString("user_password");
                String user_group = rs.getString("user_group");

                // 输出数据
                System.out.print("ID: " + id);
                System.out.print(", 用户名: " + user_name);
                System.out.print(", 密码: " + user_password);
                System.out.print(", 用户组: " + user_group);
                System.out.print("\n");
                isUser=true;

            }
            System.out.println("is:"+isUser);

            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
            //return isUser;
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }// 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return isUser;
    }

    public boolean isFriend(String myName, String friendName) {
        System.out.println("isfriend:"+myName);
        Connection conn = null;
        Statement stmt = null;
        boolean isUser=false;
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(friends_list_DB_URL, USER, PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql = "select * from "+myName+" where friends_name='"+friendName+"';";
            ResultSet rs = stmt.executeQuery(sql);





            // 展开结果集数据库

            while (rs.next()) {
                // 通过字段检索
                int id = rs.getInt("id");
                String friends_name = rs.getString("friends_name");
                String friends_group = rs.getString("friends_group");
                //int chat_num = rs.getString("chat_num");

                // 输出数据
                System.out.print("ID: " + id);
                System.out.print(", 用户名: " + friends_name);
                System.out.print(", 密码: " + friends_group);
                //System.out.print(", 用户组: " + chat_num);
                System.out.print("\n");
                isUser=true;

            }
            System.out.println("is:"+isUser);

            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
            //return isUser;
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }// 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return isUser;
    }

    public static boolean checkPassword(String userName,String password){
        Connection conn = null;
        Statement stmt = null;
        String user_password=null;
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(userinfo_DB_URL, USER, PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql = "select user_password from user_info where user_name='"+userName+"';";
            ResultSet rs = stmt.executeQuery(sql);

            // 展开结果集数据库
            while (rs.next()) {
                // 通过字段检索
                user_password = rs.getString("user_password");

                // 输出数据
                System.out.print(user_password);
                System.out.print("\n");
            }

            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
            //return isUser;
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }// 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        if(password.equals(user_password))return true;
        else {
            System.out.println(password);
            System.out.println(user_password);
            System.out.println(Arrays.toString(password.getBytes()));
            System.out.println(Arrays.toString(user_password.getBytes()));
            System.out.println(password.equals(user_password));
            return false;
        }
    }

    public static void select_user() {
        Connection conn = null;
        Statement stmt = null;
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(userinfo_DB_URL, USER, PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql = "select * from user_info;";
            ResultSet rs = stmt.executeQuery(sql);

            // 展开结果集数据库

            while (rs.next()) {
                // 通过字段检索
                int id = rs.getInt("id");
                String user_name = rs.getString("user_name");
                String user_password = rs.getString("user_password");
                String user_group = rs.getString("user_group");

                // 输出数据
                System.out.print("ID: " + id);
                System.out.print(", 用户名: " + user_name);
                System.out.print(", 密码: " + user_password);
                System.out.print(", 用户组: " + user_group);
                System.out.print("\n");
            }

            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }// 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void add_user(String userName,String userPassword) {
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement ps = null;

        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(userinfo_DB_URL, USER, PASS);

            // 执行删除
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql = "insert into user_info(user_name,user_password,user_group)values ('" + userName + "','" + userPassword + "','visitor');";
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();

            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(friends_list_DB_URL, USER, PASS);

            // 执行删除
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            sql = "create table "+userName+"(id int(11) NOT NULL auto_increment,primary key(id),friends_name varchar(255),friends_group varchar(255),chat_num int);";
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();

            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(group_list_DB_URL, USER, PASS);

            // 执行删除
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            sql = "create table user_"+userName+"(id int NOT NULL AUTO_INCREMENT,primary key(id),group_name varchar(255),chat_num int);";
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();

            // 完成后关闭
            ps.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }// 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public void add_friend(String myName,String friendName) {
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        String sql;

        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(chat_DB_URL, USER, PASS);

            // 执行删除
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            sql = "insert into chat_num(chat_type)value ('friends_"+myName+"');";
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();

            sql = "select chat_num from chat_num where chat_type='friends_"+myName+"';";
            ResultSet rs = stmt.executeQuery(sql);
            int chat_num=0;

            // 展开结果集数据库

            while (rs.next()) {
                // 通过字段检索
                chat_num = rs.getInt("chat_num");
                // 输出数据
                System.out.print("ID: " + chat_num);
            }

            sql = "create table chat_"+chat_num+"(id int(11) NOT NULL auto_increment,primary key(id),send_time datetime,send_user varchar(255),send_content varchar(255));";
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();

            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(friends_list_DB_URL, USER, PASS);

            // 执行删除
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();

            sql = "insert into "+myName+"(friends_name,friends_group,chat_num)values ('" + friendName + "','default',"+chat_num+");";
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();

            sql = "insert into "+friendName+"(friends_name,friends_group,chat_num)values ('" + myName + "','default',"+chat_num+");";
            ps = conn.prepareStatement(sql);
            ps.executeUpdate();

            // 完成后关闭
            ps.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }// 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    private void joinInGroup(String groupName) {
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        String sql;
        boolean isUser=false;
        
        int chat_num = 0;

        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(group_list_DB_URL, USER, PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            sql = "select * from user_"+User+" where group_name='"+groupName+"';";
            ResultSet rs = stmt.executeQuery(sql);
            // 展开结果集数据库
            while (rs.next()) {
                // 通过字段检索
                String group_name = rs.getString("group_name");

                isUser=true;

            }
            System.out.println("is:"+isUser);

            if(isUser==false){
                // 执行查询
                System.out.println(" 实例化Statement对象...");
                stmt = conn.createStatement();
                sql = "select chat_num from group_info where group_name='"+groupName+"';";
                rs = stmt.executeQuery(sql);
                
                while (rs.next()){
                    chat_num=rs.getInt("chat_num");
                }

                sql = "insert into user_"+User+"(group_name,chat_num)values ('" + groupName + "',"+chat_num+");";
                ps = conn.prepareStatement(sql);
                ps.executeUpdate();

                sql = "insert into "+groupName+"(user_name)value ('" + User + "');";
                ps = conn.prepareStatement(sql);
                ps.executeUpdate();
                
                
            }else{
                add_info.setText("已经加入的群聊");
            }
            
            // 完成后关闭
            ps.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }// 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
    @FXML
    public void onSearchButtonClick() {
        System.out.println("Using now:"+this.User);
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        boolean isUser=false;
        String userName=add_user_name.getText();
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(userinfo_DB_URL, USER, PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql = "select * from user_info where user_name='"+userName+"';";
            ResultSet rs = stmt.executeQuery(sql);



            // 展开结果集数据库

            while (rs.next()) {
                // 通过字段检索
                int id = rs.getInt("id");
                String user_name = rs.getString("user_name");
                String user_password = rs.getString("user_password");
                String user_group = rs.getString("user_group");

                // 输出数据
                System.out.print("ID: " + id);
                System.out.print(", 用户名: " + user_name);
                System.out.print(", 密码: " + user_password);
                System.out.print(", 用户组: " + user_group);
                System.out.print("\n");
                isUser=true;

            }
            System.out.println("is:"+isUser);

            if(isUser==true){
                if(userName.equals(User)){
                    add_info.setText("你的朋友只有自己吗 真可怜");
                }else{
                    if(isFriend(User,userName)==false){
                        add_info.setText("正在查找"+userName);
                        System.out.println(User);
                        add_friend(User,userName);
                        add_info.setText("已将"+userName+"添加至好友列表");
                    }else{
                        add_info.setText("已在好友列表中");
                    }
                }
            }else{
                add_info.setText("查无此人");
            }

            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
            //return isUser;
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }// 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
    @FXML
    public void showFriendsList() {
        Connection conn = null;
        Statement stmt = null;
        ObservableList<String> items = FXCollections.observableArrayList ();
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(friends_list_DB_URL, USER, PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql = "select friends_name from "+User+";";
            ResultSet rs = stmt.executeQuery(sql);

            // 展开结果集数据库

            while (rs.next()) {
                // 通过字段检索
                String friend_name = rs.getString("friends_name");
                items.add(friend_name);
            }

            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }// 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }


        friends_list.setItems(items);
    }
    @FXML
    public void sendButtonClick() {
        String content=chat_area.getText();
        if(content==""){
            chat_info.setText("在说空气呢");
        }else{
            chat_info.setText("");
            chat_area.setText("");

            Connection conn = null;
            Statement stmt = null;
            PreparedStatement ps = null;
            try {
                // 注册 JDBC 驱动
                Class.forName(JDBC_DRIVER);

                // 打开链接
                System.out.println("连接数据库...");
                conn = DriverManager.getConnection(chat_DB_URL, USER, PASS);

                // 执行查询
                System.out.println(" 实例化Statement对象...");
                stmt = conn.createStatement();
                String sql;
                sql = "insert into chat_"+nowTalking+"(send_time,send_user,send_content)value (now(),'"+User+"','"+content+"');";
                ps = conn.prepareStatement(sql);
                ps.executeUpdate();

                // 完成后关闭
                ps.close();
                stmt.close();
                conn.close();
            } catch (SQLException se) {
                // 处理 JDBC 错误
                se.printStackTrace();
            } catch (Exception e) {
                // 处理 Class.forName 错误
                e.printStackTrace();
            } finally {
                // 关闭资源
                try {
                    if (stmt != null) stmt.close();
                } catch (SQLException se2) {
                }// 什么都不做
                try {
                    if (conn != null) conn.close();
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }
        }
        changeChatWindows();
    }
    @FXML
    public void onSearchGroupButtonClick() {
        //System.out.println("Using now:"+this.User);
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        boolean isUser=false;
        String groupName=add_group_name.getText();
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(group_list_DB_URL, USER, PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql = "select * from group_info where group_name='"+groupName+"';";
            ResultSet rs = stmt.executeQuery(sql);



            // 展开结果集数据库

            while (rs.next()) {
                // 通过字段检索
                String group_name = rs.getString("group_name");

                isUser=true;

            }
            System.out.println("is:"+isUser);

            if(isUser==true){
                joinInGroup(groupName);
            }else{
                add_info.setText("查无此群");
            }

            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
            //return isUser;
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }// 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
    @FXML
    public void onCreateGroupButtonClick() throws IOException {
        TextInputDialog dialog = new TextInputDialog("walter");
        dialog.setTitle("");
        dialog.setHeaderText("创建群聊");
        dialog.setContentText("为群聊起个名字吧");
        dialog.initStyle(StageStyle.UTILITY);

// Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        String group_name = null;
        if (result.isPresent()){
            group_name=result.get();
            System.out.println("Your name: " + group_name);
        }
        CreateGroup(group_name);
    }

    public void CreateGroup(String groupName){
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        String sql;
        boolean isGroup=false;

        int chat_num = 0;

        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(group_list_DB_URL, USER, PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            sql = "select * from group_info where group_name='"+groupName+"';";
            ResultSet rs = stmt.executeQuery(sql);
            // 展开结果集数据库
            while (rs.next()) {
                // 通过字段检索
                String group_name = rs.getString("group_name");

                isGroup=true;

            }
            System.out.println("is:"+isGroup);

            if(isGroup==false){

                // 注册 JDBC 驱动
                Class.forName(JDBC_DRIVER);

                // 打开链接
                System.out.println("连接数据库...");
                conn = DriverManager.getConnection(chat_DB_URL, USER, PASS);

                // 执行删除
                System.out.println(" 实例化Statement对象...");
                stmt = conn.createStatement();
                sql = "insert into chat_num(chat_type)value ('group_"+groupName+"');";
                ps = conn.prepareStatement(sql);
                ps.executeUpdate();

                sql = "select chat_num from chat_num where chat_type='group_"+groupName+"';";
                rs = stmt.executeQuery(sql);
                //chat_num=0;

                // 展开结果集数据库

                while (rs.next()) {
                    // 通过字段检索
                    chat_num = rs.getInt("chat_num");
                    // 输出数据
                    System.out.print("ID: " + chat_num);
                }

                sql = "create table chat_"+chat_num+"(id int(11) NOT NULL auto_increment,primary key(id),send_time datetime,send_user varchar(255),send_content varchar(255));";
                ps = conn.prepareStatement(sql);
                ps.executeUpdate();

                // 注册 JDBC 驱动
                Class.forName(JDBC_DRIVER);

                System.out.println("连接数据库...");
                conn = DriverManager.getConnection(group_list_DB_URL, USER, PASS);

                sql = "insert into group_info(group_name,chat_num)values ('" + groupName + "',"+chat_num+");";
                ps = conn.prepareStatement(sql);
                ps.executeUpdate();

                sql = "insert into user_"+User+"(group_name,chat_num)values ('" + groupName + "',"+chat_num+");";
                ps = conn.prepareStatement(sql);
                ps.executeUpdate();

                sql = "create table "+ groupName +"(id int(11) NOT NULL auto_increment,primary key(id),user_name varchar(255));";
                ps = conn.prepareStatement(sql);
                ps.executeUpdate();

                sql = "insert into "+groupName+"(user_name)value ('" + User + "');";
                ps = conn.prepareStatement(sql);
                ps.executeUpdate();


            }else{
                add_info.setText("已经存在该群聊");
            }

            // 完成后关闭
            ps.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }// 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public void showGroupList() {
        Connection conn = null;
        Statement stmt = null;
        ObservableList<String> items = FXCollections.observableArrayList ();
        try {
            // 注册 JDBC 驱动
            Class.forName(JDBC_DRIVER);

            // 打开链接
            System.out.println("连接数据库...");
            conn = DriverManager.getConnection(group_list_DB_URL, USER, PASS);

            // 执行查询
            System.out.println(" 实例化Statement对象...");
            stmt = conn.createStatement();
            String sql;
            sql = "select group_name from user_"+User+";";
            ResultSet rs = stmt.executeQuery(sql);

            // 展开结果集数据库

            while (rs.next()) {
                // 通过字段检索
                String friend_name = rs.getString("group_name");
                items.add(friend_name);
            }

            // 完成后关闭
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) {
            // 处理 JDBC 错误
            se.printStackTrace();
        } catch (Exception e) {
            // 处理 Class.forName 错误
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
            }// 什么都不做
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }


        group_list.setItems(items);
    }
}