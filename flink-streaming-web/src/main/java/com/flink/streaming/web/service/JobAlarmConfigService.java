package com.flink.streaming.web.service;

import com.flink.streaming.web.enums.AlarmTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2021/2/27
 * @time 17:47
 */
public interface JobAlarmConfigService {

    /**
     * 批量新增/修改
     *
     * @author zhuhuipei
     * @date 2021/2/27
     * @time 17:49
     */
    void upSetBatchJobAlarmConfig(List<AlarmTypeEnum> alarmTypeEnumList, Long jobId);


    /**
     * 按jobId查询
     *
     * @author zhuhuipei
     * @date 2021/2/27
     * @time 17:53
     */
    List<AlarmTypeEnum>  findByJobId(Long jobId);


    /**
     *
     * @author zhuhuipei
     */
    Map<Long ,List<AlarmTypeEnum>> findByJobIdList(List<Long> jobIdList);


}
