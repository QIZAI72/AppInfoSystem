package com.jbit.web;

import com.jbit.pojo.AppInfo;
import com.jbit.service.AppCategoryService;
import com.jbit.service.AppInfoService;
import com.jbit.service.AppVersionService;
import com.jbit.service.DataDictionaryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("backend/app")
public class BackAppInfoController {

    @Resource
    private AppInfoService appInfoService;

    @Resource
    private DataDictionaryService dataDictionaryService;

    @Resource
    private AppCategoryService appCategoryService;

    @Resource
    private AppVersionService appVersionService;

    /**
     * 实现审核
     * @param appInfo
     * @return
     */
    @PostMapping("checksave")
    public String checksave(AppInfo appInfo){
        appInfoService.update(appInfo);
        return "redirect:/backend/app/list";
    }

    /**
     * 审核App
     * @param model
     * @param vid
     * @param aid
     * @return
     */
    @GetMapping("check")
    public String check(Model model,Long vid,Long aid){
        // 查询所有版本信息
        model.addAttribute("appInfo",appInfoService.queryById(aid));
        // 查询最新的版本信息
        model.addAttribute("appVersion",appVersionService.queryById(vid));
        return "backend/appcheck";
    }

    /**
     * App信息列表
     * @param session
     * @param model
     * @param pagenum
     * @param querySoftwareName
     * @param queryFlatformId
     * @param queryCategoryLevel1
     * @param queryCategoryLevel2
     * @param queryCategoryLevel3
     * @return
     */
    @RequestMapping("/list")
    public String list(HttpSession session, Model model,
                       @RequestParam(defaultValue = "1",value = "pageIndex") Integer pagenum,
                       String querySoftwareName,
                       Long queryFlatformId,
                       Long queryCategoryLevel1,
                       Long queryCategoryLevel2,
                       Long queryCategoryLevel3
    ){
        // 防止session丢失
        model.addAttribute("pageInfo",appInfoService.queryAppInfo(pagenum,null,querySoftwareName,1L,queryFlatformId,queryCategoryLevel1,queryCategoryLevel2,queryCategoryLevel3));
        //处理状态与所属平台
        model.addAttribute("flatFormList",dataDictionaryService.queryDataList("APP_FLATFORM"));
        //处理一级分类
        model.addAttribute("categoryLevel1List",appCategoryService.queryByPid(null));
        //处理二级分类
        if(queryCategoryLevel1 !=null){
            model.addAttribute("categoryLevel2List",appCategoryService.queryByPid(queryCategoryLevel1));
        }
        //处理三级分类
        if (queryCategoryLevel2!=null){
            model.addAttribute("categoryLevel3List",appCategoryService.queryByPid(queryCategoryLevel2));
        }
        model.addAttribute("querySoftwareName",querySoftwareName);
        model.addAttribute("queryFlatformId",queryFlatformId);
        model.addAttribute("queryCategoryLevel1",queryCategoryLevel1);
        model.addAttribute("queryCategoryLevel2",queryCategoryLevel2);
        model.addAttribute("queryCategoryLevel3",queryCategoryLevel3);
        return "backend/applist";
    }
}
