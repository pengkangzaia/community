package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    /**
     * 查询每个用户的会话列表，返回每个会话的最新的那条消息
     * @param userId 待查询的用户id
     * @param offset 用于分页，表示当前页的起始行
     * @param limit  用于分页，表示每页展示的数量多少
     * @return
     */
    List<Message> selectConversations(int userId, int offset, int limit);

    /**
     * 查询每个用户有多少个会话
     * @param userId 待查询的用户id
     * @return
     */
    int selectConversationCount(int userId);

    /**
     * 查询每个会话下的具体消息
     * @param conversationId 会话id
     * @param offset 用于分页，表示当前页的起始行
     * @param limit  用于分页，表示每页展示的数量多少
     * @return
     */
    List<Message> selectLetters(String conversationId, int offset, int limit);

    /**
     * 查询每个会话有多少个消息
     * @param conversationId 会话id
     * @return
     */
    int selectLetterCount(String conversationId);

    /**
     * 查询未读私信的数量
     * @param userId 待查询的用户id
     * @param conversationId 会话id
     * @return
     */
    int selectUnreadLetterCount(int userId, String conversationId);

    /**
     * 添加消息
     * @param message
     * @return
     */
    int insertMessage(Message message);

    /**
     * 修改消息的状态
     * @param ids 多个消息的id
     * @param status 状态
     * @return
     */
    int updateStatus(List<Integer> ids, int status);

    // 查询某类通知的最新通知
    Message selectLatestNotice(int userId, String topic);

    // 查询某主题所包含的消息的数量
    int selectNoticeCount(int userId, String topic);

    // 查询某类通知未读的数量
    int selectUnreadNoticeCount(int userId, String topic);

    // 查询某个主题下包含的通知列表
    List<Message> selectNotices(int userId, String topic, int offset, int limit);


}
