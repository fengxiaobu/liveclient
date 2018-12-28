/**
 * 文件名：CameraLiveServiceImpl.java
 * 描述：实现监控实时视频发布
 * 修改人：eguid
 * 修改时间：2016年6月30日
 * 修改内容：
 */
package com.dykj.live.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.dykj.live.dao.LiveInfoEntityRepository;
import com.dykj.live.entity.LiveInfoEntity;
import com.dykj.live.entity.ResultData;
import com.dykj.livepush.PushManager;
import com.dykj.util.CommandUtil;
import com.dykj.util.PtzUtil;
import com.dykj.util.ResultDataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 实现实时监控视频发布
 *
 * @author eguid
 * @version 2016年6月30日
 * @see CameraLiveServiceImpl
 * @since jdk1.7
 */
@Service("cameraLiveService")
public class CameraLiveServiceImpl implements CameraLiveService {
    private static Logger log = LoggerFactory.getLogger(CameraLiveServiceImpl.class);

    @Autowired
    LiveInfoEntityRepository liveInfoEntityRepository;
    /**
     * 引用push管理器，用于push直播流到rtmp服务器
     */
    @Autowired
    private PushManager pusher;
    @Autowired
    private CommandUtil commandUtil;
    @Resource
    PtzUtil ptzUtil;

    @Override
    public ResultData add(LiveInfoEntity liveInfo) {
        ResultData result = new ResultData();
        String appName = null;
        if (liveInfo != null && liveInfo.getAppName() != null && liveInfo.getInput() != null
                && liveInfo.getOutput() != null) {
            if (ResultDataUtil.checkURLNoCN(liveInfo.getInput())
                    && ResultDataUtil.checkURLNoCN(liveInfo.getOutput())) {
                if (StrUtil.equals("ONVIF", liveInfo.getProtocol()) && (StrUtil.isBlank(liveInfo.getIp()) || StrUtil.isBlank(liveInfo.getUsername()) || StrUtil.isBlank(liveInfo.getPassword()))) {
                    ResultDataUtil.setData(result, "3", "发布应用失败：应用为ONVIF的时候IP,用户名,密码不能为空!", appName);
                } else {
                    Map<String, Object> map = commandUtil.getMap4LiveInfo(liveInfo);
                    //开启推送处理器
                    pusher.push(map);
                    // 存放信息
                    liveInfo.setCdnid(getCdnId(liveInfo.getOutput()));
                    liveInfo.setCreate_time(new Date());
                    liveInfo.setUpdate_time(new Date());
                    liveInfoEntityRepository.saveAndFlush(liveInfo);
                    ResultDataUtil.setData(result, "0", "成功发布应用", map);
                }
            } else {
                ResultDataUtil.setData(result, "3", "发布应用失败：应用非地址禁止使用特殊符号，空格，中文字符；地址可以包含/:@.?&=特殊字符", appName);
            }
        } else {
            ResultDataUtil.setData(result, "1", "发布应用失败", appName);
        }
        return result;
    }

    /**
     * 获取CdnID
     *
     * @param output
     * @return
     */
    public String getCdnId(String output) {
        int index = output.indexOf("?");
        String cdnid;
        if (index == -1) {
            cdnid = output.substring(output.lastIndexOf("/") + 1);
        } else {
            cdnid = output.substring(output.lastIndexOf("/") + 1, output.indexOf("?"));
        }
        return cdnid;
    }

    @Override
    public ResultData remove(String pushId) {
        ResultData result = new ResultData();
        LiveInfoEntity infoEntity = liveInfoEntityRepository.findById(Long.valueOf(pushId)).get();
        if (pushId != null && infoEntity != null) {
            pusher.closePush(pushId);
            ptzUtil.removeCameraPtzServer(infoEntity.getCdnid());
            liveInfoEntityRepository.deleteById(Long.valueOf(pushId));
            ResultDataUtil.setData(result, "0", "删除成功", "");
        } else {
            ResultDataUtil.setData(result, "2", "非法操作或删除失败", "");
        }
        return result;
    }

    @Override
    public List<LiveInfoEntity> viewAll() {
        return liveInfoEntityRepository.findAll();
    }

    @Override
    public ResultData view(String pushId) {
        ResultData result = new ResultData();
        if (pushId != null && liveInfoEntityRepository.existsById(Long.valueOf(pushId))) {
            Optional<LiveInfoEntity> liveInfo = liveInfoEntityRepository.findById(Long.valueOf(pushId));
            ResultDataUtil.setData(result, "0", "获取详细信息成功", liveInfo.get());
        } else {
            ResultDataUtil.setData(result, "1", "获取失败", "");
        }
        return result;
    }

    @Override
    public ResultData stop(String pushId) {
        ResultData result = new ResultData();
        if (pushId != null && liveInfoEntityRepository.existsById(Long.valueOf(pushId))) {
            //如果原先是开启的则关闭 关闭的开启
            LiveInfoEntity liveInfo = liveInfoEntityRepository.findById(Long.valueOf(pushId)).get();
            if (!liveInfo.getOpen()) {
                liveInfo.setOpen(!liveInfo.getOpen());
                pusher.push(BeanUtil.beanToMap(liveInfo));
            } else {
                liveInfo.setOpen(!liveInfo.getOpen());
                pusher.closePush(pushId);
            }
            liveInfo.setUpdate_time(new Date());
            liveInfoEntityRepository.saveAndFlush(liveInfo);
            ResultDataUtil.setData(result, "0", "成功", pushId);
        } else {
            ResultDataUtil.setData(result, "2", "非法操作暂停失败", pushId);
        }
        return result;
    }
}
