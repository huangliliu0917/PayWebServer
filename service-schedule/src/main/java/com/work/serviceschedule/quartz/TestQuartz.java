package com.work.serviceschedule.quartz;

import com.work.general.annotations.ScheduledLock;
import com.work.general.pub.PubClz;
import com.work.general.service.redisservice.RedisLockService;
import com.work.general.service.environment.EnvironmentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TestQuartz  extends PubClz {

    @Autowired
    RedisLockService redisLockService;
    @Autowired
    EnvironmentUtil environmentUtil;


    /**
     * 分布式锁定时任务竞争获取锁
     * @return
     */
    //    @Scheduled(fixedRate = 10000)
    @Scheduled(cron = "0 0/1 * * * ?")
    @ScheduledLock(lockName = "testFixRate" ,desc = "定时器",expireTime = 30000L)
    public String testFixRate() {
//        System.out.println("我每隔10秒冒泡一次");
//        String msg = "ip:" + environmentUtil.getIP() + " 端口:" + environmentUtil.getPort();
//        boolean b = redisLockService.lockOnce("queryInitOrder", 30000);
//        logger.info(msg + "获取锁结果：" + b);
//        if (!b) {
//            return null;
//        }
        logger.info("执行了核心代码");
        return "";
    }
}
