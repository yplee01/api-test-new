package com.example1.springh2test;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.LinkedList;

@Slf4j
@RestController

public class ApiPost {
    public static long post_seq = 0;
    DataBaseHandle databasehandle;

    public ApiPost(DataBaseHandle databasehandle) {
        this.databasehandle = databasehandle;
    }

    /* 게시물 작성(post) */
    @PostMapping("/api/post/create")
    public long createPost(@RequestBody PostVo postVo) throws SQLException {
        Date nowDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        postVo.setCreate_time(simpleDateFormat.format(nowDate));
        postVo.setSeq(++post_seq);
        log.info(postVo.getTitle());
        log.info(postVo.getContent());
        log.info(postVo.getType_code());
        log.info(String.valueOf(postVo.getMember_seq()));
        log.info(postVo.getCreate_time());
        log.info(postVo.getIp());
        log.info(String.valueOf(postVo.getSeq()));
        postVo.setSeq(databasehandle.insertPost(postVo));
        log.info(String.valueOf(postVo.getSeq()));
        return postVo.getSeq();
    }

    /* 게시물 수정(post) */
    @PutMapping("/api/post/modify")
    public long postModify(@RequestParam String seq, @RequestBody PostVo postVo) {
        Date nowDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        postVo.setModify_time(simpleDateFormat.format(nowDate));
        postVo.setSeq(Long.parseLong(seq));

        log.info(String.valueOf(postVo.getSeq()));
        log.info(postVo.getTitle());
        log.info(postVo.getContent());
        log.info(postVo.getType_code());
        log.info(String.valueOf(postVo.getMember_seq()));
        log.info(postVo.getModify_time());
        log.info(postVo.getIp());
        try {
            return databasehandle.modifyPost(postVo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /* page 목록 조회(몇 page 까지 있는지?)
     - Argument : type_code, page_limit
     - return 최대 Page 번호
   */
    @GetMapping("/api/get/pagenum")
    public long getPageNum(@RequestParam String typecode, @RequestParam int pagelimit) {
        log.info(typecode);
        log.info(String.valueOf(pagelimit));
        long postnum = 0;
        try {
            postnum = databasehandle.getPostPageNum(typecode);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        log.info(String.valueOf(postnum));
        long pagenum = (postnum / pagelimit) + 1;
        log.info(String.valueOf(pagenum));
        return pagenum;
    }

    /* 게시글 목록 조회(GET)
     * Page 맨위 앞단에 보이는 요약된 게시글 정보 조회
     * Argument : type_code, page_num
     * Return : 게시글 요약 정보 list
     */
    @GetMapping("api/get/pagelist")
    public LinkedList<PostAbstractVo> getPageList(String typecode, int pagelimit) {
        LinkedList<PostAbstractVo> postAbstractVoLinkedList = new LinkedList<>();
        log.info(typecode);
        log.info(String.valueOf(pagelimit));
        int i = 0;
        try {
            databasehandle.getPageListAbstract(typecode, pagelimit, postAbstractVoLinkedList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (PostAbstractVo post : postAbstractVoLinkedList) {
            log.info(String.valueOf(post.getMember_seq()));
            log.info(String.valueOf(i));
            log.info(post.getTitle());
            log.info(post.getType_code());
            log.info(String.valueOf(post.getView_count()));
            log.info(post.getCreate_time());
            try {
                MemberVo memberVo = new MemberVo();
                memberVo = databasehandle.getMemberInfobyMemberseq(post.getMember_seq());
                post.setId(memberVo.getId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            log.info(post.getId());
        }
        return postAbstractVoLinkedList;
    }
    /* 게시물 조회(get) -> map 작성*/
    /* Argument : Seq
     * return : 게시물 등록 정보
     * 댓글 등록 정보
     */
    @GetMapping("api/get/post")
    public PostNCommentVo getPost(@RequestParam String seq) throws SQLException {
        log.info("START GET POST");
        PostNCommentVo postNCommentVo = new PostNCommentVo();
        PostVo postVo = new PostVo();
        MemberVo memberVo = new MemberVo();
        LinkedList<CommentVo> commentVoLinkedList = new LinkedList<>();
        postVo = databasehandle.getPostBySeq(seq);
        memberVo = databasehandle.getMemberInfobyMemberseq(postVo.getMember_seq());
        postVo.setId(memberVo.getId());
        databasehandle.setPostCountReviewIncrease(Long.valueOf(seq), postVo);
        postNCommentVo.setPostVo(postVo);
        postNCommentVo.setPostLikeCount(databasehandle.getLikeCount(postVo.getSeq(), "post_like"));
        databasehandle.getAllCommentInfoByPostSeq(String.valueOf(postVo.getSeq()), commentVoLinkedList);
        postNCommentVo.setCommentLinkedList(commentVoLinkedList);
        /* List 처리 필요 */
        return postNCommentVo;
    }
    @PostMapping("/api/post/comment")
    public void createComment(@RequestBody CommentVo commentVo) {
        log.info(commentVo.getContent());
        log.info(commentVo.getIp());
        log.info(String.valueOf(commentVo.getMember_seq()));
        log.info(String.valueOf(commentVo.getOrig_seq()));
        log.info(commentVo.getOrig_type());
        long seq = 0;
        try {
            seq = databasehandle.insertComment(commentVo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        log.info(String.valueOf(seq));
    }
    @PutMapping("/api/post/like")
    public void addPostLike(@RequestParam String seq, @RequestParam String memberseq) {
        log.info(String.valueOf(seq));
        log.info(String.valueOf(memberseq));
        try {
            databasehandle.increaseLike(Long.valueOf(seq), Long.valueOf(memberseq), "post_like");
        } catch (SQLException e) {
            e.printStackTrace();
            /* 질문.... 이 경우에 500 Error 전송이 필요한데... */
        }
    }
    @PutMapping("/api/comment/like")
    public void addCommentLike(@RequestParam String seq, @RequestParam String memberseq) {
        log.info(String.valueOf(seq));
        log.info(String.valueOf(memberseq));
        try {
            databasehandle.increaseLike(Long.valueOf(seq), Long.valueOf(memberseq), "comment_like");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
    /* Commment 추가
     *
     *
     */

    /*
    @RequestMapping("/api/reply")
    public ResponseEntity<Boolean> createReply(@RequestParam String like_type, )

     */





