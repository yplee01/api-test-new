package com.example1.springh2test;

public class QueryString {
    public static String createTable= "create table stock_code\n" +
            "(\n" +
            "    no int not null auto_increment\n" +
            "        primary key,\n" +
            "    code  char(6) not null,\n" +
            "    name        varchar(32) not null\n" +
            ")";

    public static String insertTuple = "insert into stock_code (code, name) values(?, ?)";
    public static String selectAllTuple = "select * form stock_code";
    public static String selectTupleByCode = "select * from stock_code where code = ?";
    public static String selectTupleByName = "select * from stock_code where name = ?";
    public static String DeleteTupleByCode = "delete from stock_code where code = ?";
    public static String DeleteTupleByName = "delete from stock_code where name = ?";

    public static String insertPost = "insert into post (member_seq, type_code, title, content, modify_time, create_time, count_review, ip) values(?, ?, ?, ?, ?, ?, ?, ?)";

}
