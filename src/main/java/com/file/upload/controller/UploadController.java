package com.file.upload.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.file.upload.entity.Files;
import com.file.upload.hdfs.HdfsUtils;
import com.file.upload.service.IFilesService;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.hadoop.hdfs.client.HdfsDataInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/uploadController")
@Api(description = "文件上传")
public class UploadController {

    @Autowired
    private IFilesService filesMapper;

    private static String PATH = "http://192.168.237.111:9000/";

    @PostMapping(value = "/singleUpload", headers = "content-type=multipart/form-data")
    public JSONObject singleUpload(@RequestParam(name = "attach", value = "attach", required = false) MultipartFile file) {
        long startTime = System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();
        //0表示失败
        jsonObject.put("status", "上传失败");
        try {


            System.out.println("fileName：" + file.getOriginalFilename());

            String temp = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
            String url = "/files/" + temp + "/" + file.getOriginalFilename();


            Files files = new Files();
            files.setFileLength(file.getSize());
            files.setFileName(file.getOriginalFilename());
            files.setFilePath(url);
            String fileType = "";
            try {
                int lastIndex = file.getOriginalFilename().lastIndexOf(".");
                fileType = file.getOriginalFilename().substring(lastIndex, file.getOriginalFilename().length());
            } catch (Exception e) {
                e.printStackTrace();
            }


            files.setFileType(fileType);

            boolean flag = filesMapper.save(files);
            //入库成功才上传文件到HDFS
            if (flag) {
                //上传文件到HDFS
                JSONObject result = HdfsUtils.upload(file.getInputStream(), url);
                jsonObject.putAll(result);
            }



            /*
            上传到本地磁盘使用这个方法
            String path = "E:/cloudFiles/file_" + new Date().getTime() + file.getOriginalFilename();

            File newFile = new File(path);
            //通过CommonsMultipartFile的方法直接写文件（注意这个时候）
            file.transferTo(newFile);
            long endTime = System.currentTimeMillis();
            System.out.println("采用file.Transto的运行时间：" + String.valueOf(endTime - startTime) + "ms");*/

            //1表示成功
            jsonObject.put("status", "上传成功");


        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;

    }

    @PostMapping(value = "/mutiFilesUpload", headers = "content-type=multipart/form-data")
    public JSONObject mutiFilesUpload(HttpServletRequest request) {
        List<MultipartFile> filess = ((MultipartHttpServletRequest) request).getFiles("file");
        JSONObject jsonObject = new JSONObject();
        //0表示失败
        jsonObject.put("status", "上传失败");
        try {
            if (filess != null && filess.size() > 0)
                for (MultipartFile file : filess) {

                    System.out.println("fileName：" + file.getOriginalFilename());

                    String temp = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
                    String url = "/files/" + temp + "/" + file.getOriginalFilename();


                    Files files = new Files();
                    files.setFileLength(file.getSize());
                    files.setFileName(file.getOriginalFilename());
                    files.setFilePath(url);
                    String fileType = "";
                    try {
                        int lastIndex = file.getOriginalFilename().lastIndexOf(".");
                        fileType = file.getOriginalFilename().substring(lastIndex, file.getOriginalFilename().length());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    files.setFileType(fileType);

                    boolean flag = filesMapper.save(files);
                    //入库成功才上传文件到HDFS
                    if (flag) {
                        //上传文件到HDFS
                        JSONObject result = HdfsUtils.upload(file.getInputStream(), url);
                        jsonObject.putAll(result);
                    }
                    jsonObject.put("status", "上传成功");
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;

    }

    @GetMapping("/deleteFile")
    public JSONObject deleteFile(@RequestParam(name = "id", required = true) String id) {
        JSONObject jsonObject = new JSONObject();
        String filePath = "";
        QueryWrapper<Files> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        Files file = filesMapper.getOne(wrapper);
        if (file != null) {
            filePath = file.getFilePath();
        }
        if (StringUtils.isBlank(filePath)) {
            jsonObject.put("message", "filePath不能为空");
            return jsonObject;
        }
        JSONObject result = HdfsUtils.delete(filePath);

        jsonObject.putAll(result);

        return jsonObject;
    }

    @GetMapping("/downloadFile")
    public void downloadFile(@RequestParam(name = "id", required = true) String id, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        String absolutePath = "";
        String fileName = "";

        QueryWrapper<Files> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        Files file = filesMapper.getOne(wrapper);

        if (file != null) {
            absolutePath = file.getFilePath() + "";
            fileName = file.getFileName() + "";
        }


        if (StringUtils.isBlank(absolutePath) || StringUtils.isBlank(fileName)) {
            jsonObject.put("message", "absolutePath 或 fileName 不能为空");
            return;
        }
        try {
            response.setContentType("application/x-msdownload");
            //设置content-disposition响应头控制浏览器以下载的形式打开文件
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject result = HdfsUtils.download(absolutePath, fileName);

        if ("下载成功".equals(result.get("message"))) {
            HdfsDataInputStream in = (HdfsDataInputStream) result.get("inputStream");
            OutputStream out = null;
            try {
                int len = 0;
                //创建数据缓冲区
                byte[] buffer = new byte[1024];
                //通过response对象获取outputStream流
                out = response.getOutputStream();
                //将FileInputStream流写入到buffer缓冲区
                while ((len = in.read(buffer)) > 0) {
                    //使用OutputStream将缓冲区的数据输出到浏览器
                    out.write(buffer, 0, len);
                }
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    in.close();
                    out.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }


            }


        }

    }

    @GetMapping("/getAll")
    public JSONObject getAll() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("messgae", "未查询出数据");
        try {
            List<Files> list = filesMapper.list();
            if (list != null && list.size() > 0) {
                jsonObject.put("messgae", "查询成功");
                jsonObject.put("result", list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("messgae", "后台异常");
        }


        return jsonObject;
    }

}
