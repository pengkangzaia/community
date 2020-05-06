package com.nowcoder.community;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CaffeineTest {

    @Autowired
    private DiscussPostService discussPostService;

    @Test
    public void initDataForTest() {
        for (int i = 0; i < 300000; i++) {
            DiscussPost post = new DiscussPost();
            post.setUserId(111);
            post.setTitle("找实习啊！！！冲冲冲！！！");
            post.setContent("努力努力再努力！！！passion ");
            post.setCreateTime(new Date());
            post.setScore(Math.random() * 2000);
            discussPostService.addDiscussPost(post);
        }
    }

    @Test
    public void testCache() {
        System.out.println(discussPostService.selectDiscussPosts(0, 0, 10, 1));
        System.out.println(discussPostService.selectDiscussPosts(0, 0, 10, 1));
        System.out.println(discussPostService.selectDiscussPosts(0, 0, 10, 1));
        System.out.println(discussPostService.selectDiscussPosts(0, 0, 10, 0));
    }


}
