package com.example1.springh2test;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PostAbstractVo {
    private String type_code;
    private String title;
    private String id;
    private int view_count;
    private long member_seq;
    private String create_time;
    private int comment_count;
}
