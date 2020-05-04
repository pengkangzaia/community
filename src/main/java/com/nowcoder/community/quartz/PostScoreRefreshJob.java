package com.nowcoder.community.quartz;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostScoreRefreshJob implements Job, CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    // 根据计算公式，我们需要知道：帖子评论数，赞数，是否加精
    // 调用下列service方法，查询帖子,点赞数量等相关信息
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private LikeService likeService;

    // 把计算score之后更新的帖子，重新放到elastic search中
    @Autowired
    private ElasticsearchService elasticsearchService;

    // 牛客纪元，系统开始的最初时间。帖子创建时间-牛客纪元 = 一个权重
    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化牛客纪元失败", e);
        }

    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        // 有许多个操作
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        // 先判断redisKey里面有没有值，如果没人访问，说明帖子分数没有变化
        // 那么就不用计算
        if (operations.size() == null) {
            logger.info("[任务取消] 没有要刷新的帖子!");
            return;
        }

        logger.info("任务开始，刷新帖子分数: " + operations.size());
        while (operations.size() > 0) {
            this.refresh((Integer) operations.pop());
        }
        logger.info("任务结束，帖子分数刷新完毕~");
    }

    private void refresh(int postId) {
        DiscussPost post = discussPostService.findDiscussPostById(postId);

        if (post == null) {
            logger.error("该帖子不存在: " + postId);
            return;
        }
        // 判断帖子是否加精
        boolean wonderful = post.getStatus() == 1;
        // 评论数量
        int commentCount = post.getCommentCount();
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        // 计算score
        // log的底数，w
        double w = (wonderful ? 75 : 0) + commentCount * 2 + likeCount * 2;
        double score = Math.log10(Math.max(w, 1)) +
                (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);

        // 更新帖子分数
        discussPostService.updateScore(postId, score);
        // 同步es的搜索数据
        post.setScore(score);
        elasticsearchService.saveDiscussPost(post);
    }
}
