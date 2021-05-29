package com.mmkpark.controller;

import cn.hutool.core.util.IdUtil;
import com.mmkpark.common.lang.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
public class FileController {

    @Value("${prop.upload-folder}")
    private String UPLOAD_FOLDER;


    @PostMapping("/upload")
    public Result upload(@RequestParam(name = "file") MultipartFile file) {

        if (file == null) {
            return Result.fail("请选择要上传的图片");
        }
        if (file.getSize() > 1024 * 1024 * 5) {
            return Result.fail("文件大小不能大于5M");
        }
        //获取文件后缀
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1, file.getOriginalFilename().length());
        if (!"jpg,jpeg,gif,png".toUpperCase().contains(suffix.toUpperCase())) {
            return Result.fail("请选择jpg,jpeg,gif,png格式的图片");
        }
        String savePath = UPLOAD_FOLDER;
        File savePathFile = new File(savePath);
        if (!savePathFile.exists()) {
            //若不存在该目录，则创建目录
            savePathFile.mkdir();
        }
        //通过UUID生成唯一文件名
        String filename = IdUtil.randomUUID().replaceAll("-","") + "." + suffix;
        try {
            //将文件保存指定目录
            file.transferTo(new File(savePath + filename));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("保存文件异常");
        }
        //返回文件名称

        //return Result.succ(savePath+filename);
        return Result.succ(filename);
    }


}
