package com.example1.springh2test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

@Slf4j
@Service
public class DataBaseHandle {
    public DataBaseHandle(DataSource dataSource) throws SQLException {
        this.dataSource = dataSource;
        System.out.println("TEST1");
        System.out.println(dataSource.getConnection().getMetaData().getURL());
        log.info("TEST2");
    }

    DataSource dataSource;

    public void createTable() throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        String sql = QueryString.createTable;
        statement.executeUpdate(sql);
    }
    public void insertTuple(String strCode, String strName) throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        String sql = String.format("insert into stock_code (code, name) values('%s', '%s')", strCode, strName);
        statement.executeUpdate(sql);
    }
    public long insertPost(PostVo postVo) throws SQLException {
        long seq = 0;
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        String sql = String.format("insert into post (member_seq, type_code, title, content, create_time, ip) values('%s', '%s', '%s', '%s', '%s', '%s')",
                String.valueOf(postVo.getMember_seq()), postVo.getType_code(), postVo.getTitle(), postVo.getContent(), postVo.getCreate_time(), postVo.getIp());
        log.info(sql);
        statement.executeUpdate(sql);
        sql = String.format("select max(seq) from post");

        ResultSet resultSet = statement.executeQuery(sql);
        log.info(sql);
        if(resultSet.next()) {
            seq = resultSet.getLong("max(seq)");
        }
        connection.close();
        statement.close();
        resultSet.close();
        return seq;
    }
    public long modifyPost(PostVo postVo) throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        String sql = String.format("update post set type_code='%s', title='%s', content='%s', modify_time='%s', ip='%s' where seq=%s",
                postVo.getType_code(), postVo.getTitle(), postVo.getContent(), postVo.getModify_time(), postVo.getIp(), postVo.getSeq());

        log.info(sql);
        statement.executeUpdate(sql);
        connection.close();
        statement.close();
        return postVo.getSeq();
    }
    public long getPostPageNum(String typecode) throws SQLException {
        long post_num = 0;
        String sql = new String();

        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();

        if(typecode.equals("ALL")) {
            sql = String.format("select count(*) from post ", typecode);
        }
        else {
            sql = String.format("select count(*) from post where type_code = '%s'", typecode);
        }
        ResultSet resultSet = statement.executeQuery(sql);
        log.info(sql);

        if(resultSet.next()) {
            post_num = resultSet.getLong("count(*)");
        }
        statement.close();
        resultSet.close();
        return post_num;
    }

    public PostVo getPostBySeq(String seq) throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        PostVo postVo = new PostVo();
        String sql = String.format("select * from post where seq = '%s'", seq);
        log.info(sql);
        ResultSet resultSet = statement.executeQuery(sql);

        if(resultSet.next()) {
            postVo.setSeq(resultSet.getLong("seq"));
            postVo.setMember_seq(resultSet.getLong("member_seq"));
            postVo.setType_code(resultSet.getString("type_code"));
            postVo.setTitle(resultSet.getString("title"));
            postVo.setContent(resultSet.getString("content"));
            postVo.setModify_time(resultSet.getString("modify_time"));
            postVo.setCreate_time(resultSet.getString("create_time"));
            postVo.setCount_review(resultSet.getInt("count_review"));
            postVo.setIp(resultSet.getString("ip"));
        }
        connection.close();
        statement.close();
        resultSet.close();
        return postVo;
    }

    public void getPageListAbstract(String type_code, int page_num,
                                    LinkedList<PostAbstractVo> postAbstractVoLinkedList) throws SQLException {
        long post_num = 0;
        String sql = new String();
        PostAbstractVo post = new PostAbstractVo();

        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();

        if(type_code.equals("ALL")) {
            sql = String.format("select * from post ", type_code);
        }
        else {
            sql = String.format("select * from post where type_code = '%s'", type_code);
        }
        ResultSet resultSet = statement.executeQuery(sql);
        log.info(sql);

        while(resultSet.next()) {
            post.setType_code(resultSet.getString("type_code"));
            post.setTitle(resultSet.getString("title"));
            post.setView_count(resultSet.getInt("count_review"));
            post.setMember_seq(resultSet.getLong("member_seq"));
            post.setCreate_time(resultSet.getString("create_time"));

            /* comment count & id 값은 외부에서 다시 처리 */

            postAbstractVoLinkedList.add(post);
            post = new PostAbstractVo();
        }
        statement.close();
        resultSet.close();
    }
    public void getCommentInfoByCommentSeq(String seq, String type, LinkedList<CommentVo> commentVoLinkedList) throws SQLException {
        long post_num = 0;
        String sql = new String();
        String orig_type = new String();
        CommentVo commentVo = new CommentVo();

        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        log.info("START getCommentInfoByCommentSeq");
        sql = String.format("select * from comment where orig_type = '%s' and orig_seq = '%s'", type, seq);

        ResultSet resultSet = statement.executeQuery(sql);
        log.info(sql);
        while(resultSet.next()) {
            log.info("COPY COMMENT attribute");
            commentVo.setSeq(resultSet.getLong("seq"));
            commentVo.setMember_seq(resultSet.getLong("member_seq"));
            commentVo.setContent(resultSet.getString("content"));
            commentVo.setOrig_seq(resultSet.getLong("orig_seq"));
            commentVo.setIp(resultSet.getString("ip"));
            commentVo.setOrig_type(resultSet.getString("orig_type"));
            orig_type = resultSet.getString("orig_type");
            commentVo.setOrig_type(orig_type);

            if(orig_type.equals("COMMENT")) {
                log.info("EQUAL COMMENT");
                this.getCommentInfoByCommentSeq("COMMENT", String.valueOf(commentVo.getOrig_seq()), commentVoLinkedList);
            }
            log.info("ADD List(getCommentInfoByCommentSeq)");
            log.info(String.valueOf(commentVo.getOrig_seq()));
            commentVoLinkedList.add(commentVo);
        }

        resultSet.close();
        statement.close();
    }
    public void getAllCommentInfoByPostSeq(String seq, LinkedList<CommentVo> commentLinkedList) throws SQLException {
        long post_num = 0;
        String sql = new String();
        String orig_type = new String();
        CommentVo commentVo = new CommentVo();

        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();

        sql = String.format("select * from comment where post_seq = '%s'", seq);

        ResultSet resultSet = statement.executeQuery(sql);

        log.info(sql);
        log.info("START getAllCommentInfoByPostSeq");

        while (resultSet.next()) {
            commentVo.setSeq(resultSet.getLong("seq"));
            commentVo.setMember_seq(resultSet.getLong("member_seq"));
            commentVo.setContent(resultSet.getString("content"));
            commentVo.setOrig_seq(resultSet.getLong("orig_seq"));
            commentVo.setIp(resultSet.getString("ip"));
            orig_type = resultSet.getString("orig_type");
            commentVo.setOrig_type(orig_type);
            commentVo.setPost_seq(resultSet.getLong("post_seq"));
            commentVo.setCommentLikeCount(this.getLikeCount(commentVo.getSeq(), "comment_like"));
            commentLinkedList.add(commentVo);
            commentVo = new CommentVo();
        }
        resultSet.close();
        statement.close();
    }

    public MemberVo getMemberInfobyMemberseq(long member_seq) throws SQLException {
        long seq = 0;
        MemberVo memberVo = new MemberVo();
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        String sql = String.format("select * from member where member_seq = '%s'", member_seq);
        log.info(sql);
        ResultSet resultSet = statement.executeQuery(sql);

        if(resultSet.next()) {
            memberVo.setId(resultSet.getString("id"));
            memberVo.setName(resultSet.getString("name"));
        }
        connection.close();
        statement.close();
        resultSet.close();
        return memberVo;
    }
    public void increaseLike(Long seq, Long memberseq, String type) throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        String sql = String.format("insert into %s (seq, member_seq) values('%s', '%s')", type, seq, memberseq);
        log.info(sql);
        statement.executeUpdate(sql);
        connection.close();
        statement.close();
    }
    public void updatePostReviewCount(PostVo postVo) throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        String sql = String.format("update post set count_review='%s' where seq='%s'", postVo.getCount_review(), postVo.getSeq());
        log.info(sql);
        statement.executeUpdate(sql);
        connection.close();
        statement.close();
    }
    public long insertComment(CommentVo commentVo) throws SQLException {
        long seq = 0;
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        String sql = String.format("insert into comment (member_seq, content, ip, orig_seq, orig_type, post_seq) values('%s', '%s', '%s', '%s', '%s', '%s')",
                String.valueOf(commentVo.getMember_seq()), commentVo.getContent(), commentVo.getIp(), commentVo.getOrig_seq(), commentVo.getOrig_type(), commentVo.getPost_seq());
        log.info(sql);
        statement.executeUpdate(sql);
        sql = String.format("select max(seq) from comment");

        ResultSet resultSet = statement.executeQuery(sql);
        log.info(sql);
        if(resultSet.next()) {
            seq = resultSet.getLong("max(seq)");
        }
        connection.close();
        statement.close();
        resultSet.close();
        return seq;
    }
    public void setPostCountReviewIncrease(long seq, PostVo postVo) throws SQLException {
        long countReview = 0;
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        String sql = String.format("select * from post where seq = '%s'", seq);
        log.info(sql);
        ResultSet resultSet = statement.executeQuery(sql);
        if(resultSet.next()) {
            int count_review = resultSet.getInt("count_review");
            postVo.setCount_review(++count_review);
            updatePostReviewCount(postVo);
        }
        connection.close();
        statement.close();
        resultSet.close();
    }

    public long getLikeCount(long seq, String type) throws SQLException {
        long count = 0;
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        String sql = String.format("select count(*) from %s where seq='%s'", type, seq);

        log.info(sql);
        ResultSet resultSet = statement.executeQuery(sql);
        if(resultSet.next()) {
            count = resultSet.getInt("count(*)");
        }
        connection.close();
        statement.close();
        resultSet.close();
        return count;
    }
}
