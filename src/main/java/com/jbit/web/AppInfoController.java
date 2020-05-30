package com.jbit.web;

import com.jbit.entity.JsonResult;
import com.jbit.pojo.AppInfo;
import com.jbit.pojo.AppVersion;
import com.jbit.pojo.DevUser;
import com.jbit.service.AppCategoryService;
import com.jbit.service.AppInfoService;
import com.jbit.service.AppVersionService;
import com.jbit.service.DataDictionaryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;

@Controller
@RequestMapping("dev/app")
public class AppInfoController {

    @Resource
    private AppInfoService appInfoService;

    @Resource
    private AppCategoryService appCategoryService;

    @Resource
    private DataDictionaryService dataDictionaryService;

    @Resource
    private AppVersionService appVersionService;

    @PostMapping("addversionsave")
    public String addversionsave(AppVersion appVersion, MultipartFile a_downloadLink, HttpSession session){
        // 1.实现文件上传 (服务器 tomcat)
        String server_path = session.getServletContext().getRealPath("/statics/uploadfiles/");
        // 验证大小和图片规格 [略]
        try {
            a_downloadLink.transferTo(new File(server_path,a_downloadLink.getOriginalFilename()));
        } catch (IOException e) {
        }
        // 处理其余数据
        DevUser devUser = (DevUser) session.getAttribute("devuser");
        appVersion.setDownloadlink("/statics/uploadfiles/"+a_downloadLink.getOriginalFilename());
        appVersion.setCreatedby(devUser.getId());
        appVersion.setCreationdate(new  Date());
        appVersion.setModifydate(new Date());
        appVersion.setApkfilename(a_downloadLink.getOriginalFilename());
        appVersion.setApklocpath(server_path+a_downloadLink.getOriginalFilename());
        appVersionService.save(appVersion);
        // 更新版本号
        AppInfo appInfo = new AppInfo();
        appInfo.setId(appVersion.getAppid());
        appInfo.setVersionid(appVersion.getId());
        appInfoService.update(appInfo);
        return "redirect:/dev/app/appversionadd/"+appVersion.getAppid();
    }

    /**
     * 跳转添加版本信息
     * @param id
     * @param model
     * @return
     */
    @GetMapping("appversionadd/{id}")
    public String appversionadd(@PathVariable Long id,Model model){
        model.addAttribute("appVersionList",appVersionService.queryByAppId(id));
        model.addAttribute("appid",id);
        return "developer/appversionadd";
    }

    @GetMapping("delfile")
    @ResponseBody
    public JsonResult delfile(Long id,String flag){
        if (flag.equals("log")){ // 删除logo
            AppInfo appInfo = appInfoService.queryById(id);
            // 通过绝对路径删除图片
            try {
                File file = new File(appInfo.getLogolocpath());
                file.delete(); // 删除文件
                // 清空数据库存储值
                appInfo.setLogolocpath("");
                appInfo.setLogopicpath("");
                appInfoService.update(appInfo);
                return new JsonResult(true);
            }catch (Exception e){
                return new JsonResult(false);
            }
        }
        return new JsonResult(false);


    }

    /**
     * 修改查询
     * @param id
     * @return
     */
    @GetMapping("appinfomodify/{id}")
    public String appinfomodify(Model model,@PathVariable Long id){
        model.addAttribute("appInfo",appInfoService.queryById(id));
        return "developer/appinfomodify";
    }

    /**
     * 验证apk是否注册
     * @param apkname
     * @return
     */
    @GetMapping("/apkexist")
    @ResponseBody
    public JsonResult apkexist(String apkname){
        AppInfo appInfo = appInfoService.queryApkexist(apkname);
        if (appInfo==null){
            return new JsonResult(true);
        }
        return new JsonResult(false);
    }

    /**
     * APP 新增
     * @return
     */
    @PostMapping("/appinfoadd")
    public String appinfoadd(HttpSession session,AppInfo appInfo, MultipartFile a_logoPicPath) {
        // 1.实现文件上传 (服务器 tomcat)
        String server_path = session.getServletContext().getRealPath("/statics/uploadfiles/");
        // 验证大小和图片规格 [略]
        try {
            a_logoPicPath.transferTo(new File(server_path,a_logoPicPath.getOriginalFilename()));
        } catch (IOException e) {
        }
        // 2.app添加
        DevUser devuser = (DevUser) session.getAttribute("devuser");
        appInfo.setUpdatedate(new Date());
        appInfo.setDevid(devuser.getId());
        appInfo.setCreatedby(devuser.getId());
        appInfo.setCreationdate(new Date());
        appInfo.setLogopicpath("/statics/uploadfiles/"+a_logoPicPath.getOriginalFilename());
        appInfo.setLogolocpath(server_path+a_logoPicPath.getOriginalFilename());
        appInfoService.save(appInfo);
        return "redirect:/dev/app/list";
    }

    /**
     * APP 修改
     * @return
     */
    @PostMapping("/appinfomodify")
    public String appinfomodify(HttpSession session,AppInfo appInfo, MultipartFile attach) {
        if (!attach.isEmpty()) {
            // 1.实现文件上传 (服务器 tomcat)
            String server_path = session.getServletContext().getRealPath("/statics/uploadfiles");
            // 验证大小和图片规格 [略]
            try {
                attach.transferTo(new File(server_path,attach.getOriginalFilename()));
                appInfo.setLogopicpath("/statics/uploadfiles/"+attach.getOriginalFilename());
                appInfo.setLogolocpath(server_path+attach.getOriginalFilename());
            } catch (IOException e) {
            }
        }
        // 2.app修改
        DevUser devuser = (DevUser) session.getAttribute("devuser");
        appInfo.setUpdatedate(new Date());
        appInfo.setDevid(devuser.getId());
        appInfo.setModifyby(devuser.getId());
        appInfo.setModifydate(new Date());
        appInfoService.update(appInfo);
        return "redirect:/dev/app/list";
    }

    /**
     * App信息列表
     * @param session
     * @param model
     * @param pagenum
     * @param querySoftwareName
     * @param queryStatus
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
                       Long queryStatus,
                       Long queryFlatformId,
                       Long queryCategoryLevel1,
                       Long queryCategoryLevel2,
                       Long queryCategoryLevel3
    ){
        DevUser devuser = (DevUser) session.getAttribute("devuser");
        model.addAttribute("pageInfo",appInfoService.queryAppInfo(pagenum,devuser.getId(),querySoftwareName,queryStatus,queryFlatformId,queryCategoryLevel1,queryCategoryLevel2,queryCategoryLevel3));
        //处理状态与所属平台
        model.addAttribute("statusList",dataDictionaryService.queryDataList("APP_STATUS"));
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
        model.addAttribute("queryStatus",queryStatus);
        model.addAttribute("queryFlatformId",queryFlatformId);
        model.addAttribute("queryCategoryLevel1",queryCategoryLevel1);
        model.addAttribute("queryCategoryLevel2",queryCategoryLevel2);
        model.addAttribute("queryCategoryLevel3",queryCategoryLevel3);
        return "developer/appinfolist";
    }
}