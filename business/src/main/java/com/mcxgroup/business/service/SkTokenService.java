package com.mcxgroup.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mcxgroup.business.enums.RedisKeyPreEnum;
import com.mcxgroup.business.mapper.cust.SkTokenMapperCust;
import com.mcxgroup.common.resp.PageResp;
import com.mcxgroup.common.util.SnowUtil;
import com.mcxgroup.business.domain.SkToken;
import com.mcxgroup.business.domain.SkTokenExample;
import com.mcxgroup.business.mapper.SkTokenMapper;
import com.mcxgroup.business.req.SkTokenQueryReq;
import com.mcxgroup.business.req.SkTokenSaveReq;
import com.mcxgroup.business.resp.SkTokenQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author MCXEN
 * @project Train
 */
@Service
public class SkTokenService {
    private static final Logger LOG = LoggerFactory.getLogger(SkTokenService.class);

    @Resource
    private SkTokenMapper skTokenMapper;
    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @Resource
    private DailyTrainStationService dailyTrainStationService;

    @Resource
    private SkTokenMapperCust skTokenMapperCust;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Value("${spring.profiles.active}")
    private String env;
    /**
     * 初始化
     */
    public void genDaily(Date date, String trainCode) {
        LOG.info("删除日期【{}】车次【{}】的令牌记录", DateUtil.formatDate(date), trainCode);
        SkTokenExample skTokenExample = new SkTokenExample();
        skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        skTokenMapper.deleteByExample(skTokenExample);

        DateTime now = DateTime.now();
        SkToken skToken = new SkToken();
        skToken.setDate(date);
        skToken.setTrainCode(trainCode);
        skToken.setId(SnowUtil.getSnowflakeNextId());
        skToken.setCreateTime(now);
        skToken.setUpdateTime(now);

        int seatCount = dailyTrainSeatService.countSeat(date, trainCode);
        LOG.info("车次【{}】座位数：{}", trainCode, seatCount);

        long stationCount = dailyTrainStationService.countByTrainCode(date, trainCode);
        LOG.info("车次【{}】到站数：{}", trainCode, stationCount);

        // 3/4需要根据实际卖票比例来定，一趟火车最多可以卖（seatCount * stationCount）张火车票
        int count = (int) (seatCount * stationCount ); // * 3/4);
        LOG.info("车次【{}】初始生成令牌数：{}", trainCode, count);
        skToken.setCount(count);

        skTokenMapper.insert(skToken);
    }

    public void save(SkTokenSaveReq req) {
        DateTime now = DateTime.now();
        SkToken skToken = BeanUtil.copyProperties(req, SkToken.class);
        if (ObjectUtil.isNull(skToken.getId())) {
            skToken.setId(SnowUtil.getSnowflakeNextId());
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);
            skTokenMapper.insert(skToken);
        } else {
            skToken.setUpdateTime(now);
            skTokenMapper.updateByPrimaryKey(skToken);
        }
    }

    public PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq req) {
        SkTokenExample example = new SkTokenExample();
        example.setOrderByClause("id desc");
        SkTokenExample.Criteria criteria = example.createCriteria();
        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        //获取查询结果：
        List<SkToken> skTokenList = skTokenMapper.selectByExample(example);
        PageInfo<SkToken> pageInfo = new PageInfo<>(skTokenList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<SkTokenQueryResp> list = BeanUtil.copyToList(skTokenList, SkTokenQueryResp.class);
        PageResp<SkTokenQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    public void delete(Long id) {
        skTokenMapper.deleteByPrimaryKey(id);
    }

    public boolean validSkToken(Date date,String trainCode,Long memberId){
        LOG.info("会员「{}」获取日期「{}」车次「{}」的令牌开始",memberId,DateUtil.formatDate(date),trainCode);

        //非开发环境才开启
        if (!env.equals("dev")){
            //基于RedisTemplate的分布式锁来防止刷票
            String key = RedisKeyPreEnum.SK_TOKEN+"-"+DateUtil.formatDate(date)+"-"+trainCode+memberId;
            Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(key, key, 1, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(setIfAbsent)){
                LOG.info("恭喜，在令牌校验防止刷票函数中抢到了锁");
            }else {
                LOG.info("遗憾，在令牌校验防止刷票函数没有抢到锁，稍后重试");
                return false;
            }

        }
        String skTokenCountKey = RedisKeyPreEnum.SK_TOKEN_COUNT+"-"+DateUtil.formatDate(date)+"-"+trainCode;
        Object skTokenCount = redisTemplate.opsForValue().get(skTokenCountKey);
        if (skTokenCount!=null){
            //如果redis缓存里面有count，
            LOG.info("缓存中有该车次令牌大闸的key：{}",skTokenCountKey);
            LOG.info("skTokenCount:{}",skTokenCount);
            String s = skTokenCount.toString();
            Integer.parseInt(s);
            Long count = redisTemplate.opsForValue().decrement(skTokenCountKey, 1);//减1，操作缓存的key
            if (count<0L){
                LOG.error("令牌数目小于0，失败");
                return false;
            }else {
                LOG.info("获取令牌后，令牌余数：{}",count);
                redisTemplate.expire(skTokenCountKey,60,TimeUnit.SECONDS);
                if (count%5==0){//每次获取5个令牌去更新数据库
                    skTokenMapperCust.decrease(date,trainCode,5);
                }
                return true;
            }
        }else {
            LOG.info("缓存中不存在有该车次令牌大闸的key：{}",skTokenCountKey);
            SkTokenExample example = new SkTokenExample();
            example.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
            List<SkToken> skTokens = skTokenMapper.selectByExample(example);
            if (CollUtil.isEmpty(skTokens)){
                LOG.info("日期「{}」车次「{}」的令牌在数据库中找不到",DateUtil.formatDate(date),trainCode);
                return false;
            }
            SkToken skToken = skTokens.get(0);
            if (skToken.getCount()<=0){
                LOG.info("日期「{}」车次「{}」的令牌余量为0",DateUtil.formatDate(date),trainCode);
                return false;
            }
            int count = skToken.getCount() - 1;
            skToken.setCount(count);
            LOG.info("将该车次令牌大闸放入缓存中，key: {}， count: {}",skTokenCountKey,count);
            redisTemplate.opsForValue().set(skTokenCountKey,String.valueOf(count),60,TimeUnit.SECONDS);
//            skTokenMapper.updateByPrimaryKey(skToken);//更新对应的车次的令牌的数量。
            return true;
        }

        // 由于每次都去访问数据库，删减令牌，效率不高，修改
//        //令牌约等于库存，令牌没有了，就不再卖票，不需要再进入购票主流程去判断库存，判断令牌肯定比判断库存快
//        int updateCount = skTokenMapperCust.decrease(date, trainCode);
//        if (updateCount>0){
//            return true;
//        }else {
//            return false;
//        }
    }
}
