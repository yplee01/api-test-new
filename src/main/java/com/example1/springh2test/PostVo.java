package com.example1.springh2test;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostVo {
    private long seq;
    private long member_seq;
    private String id;
    private String type_code;
    private String title;
    private String content;
    private String modify_time;
    private String create_time;
    private long count_review;
    private String ip;
}
