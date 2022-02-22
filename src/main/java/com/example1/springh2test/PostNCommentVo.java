package com.example1.springh2test;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;

@Getter
@Setter
public class PostNCommentVo {
    private PostVo postVo;
    private long postLikeCount;
    private LinkedList<CommentVo> commentLinkedList;
}
