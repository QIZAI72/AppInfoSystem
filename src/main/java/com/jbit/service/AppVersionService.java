package com.jbit.service;

import com.jbit.mapper.AppVersionMapper;
import com.jbit.pojo.AppVersion;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AppVersionService {
    @Resource
    private AppVersionMapper appVersionMapper;

    @Resource
    private DataDictionaryService dataDictionaryService;

    @Resource
    private AppInfoService appInfoService;

    /**
     * AppId查询
     * @param id
     * @return
     */
    public List<AppVersion> queryByAppId(Long id){
        Example example = new Example(AppVersion.class);
        // 排序
        example.orderBy("modifydate").desc();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("appid",id);
        List<AppVersion> appVersions = appVersionMapper.selectByExample(example);
        appVersions.forEach(app->{
            // 处理AppName
            app.setAppname(appInfoService.queryById(app.getAppid()).getSoftwarename());
            // 处理发布状态
            app.setPublishstatusname(dataDictionaryService.queryData("PUBLISH_STATUS",app.getPublishstatus()).getValuename());
        });
        return appVersions;
    }

    /**
     * Id查询
     * @param id
     * @return
     */
    public AppVersion queryById(Long id){
        return appVersionMapper.selectByPrimaryKey(id);
    }

    /**
     * 添加AppVersion
     * @param appVersion
     */
    public void save(AppVersion appVersion) {
        appVersionMapper.insertSelective(appVersion);
    }

    /**
     * 修改appVersion
     * @param appVersion
     */
    public void update(AppVersion appVersion){
        appVersionMapper.updateByPrimaryKeySelective(appVersion);
    }

    public int deleteByAppId(Long appId){
        AppVersion appVersion = new AppVersion();
        appVersion.setAppid(appId);
        return appVersionMapper.delete(appVersion);
    }
}