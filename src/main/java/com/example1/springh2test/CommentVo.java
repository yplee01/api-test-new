package com.example1.springh2test;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentVo {
    private long seq;
    private long member_seq;
    private String content;
    private String ip;
    private long orig_seq;
    private long post_seq;
    private String orig_type;      // POST or COMMENT
    private long CommentLikeCount;
}
