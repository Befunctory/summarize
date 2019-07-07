package com.example.demo;

import com.DemoApplication;
import com.alibaba.fastjson.JSONObject;
import com.file.upload.entity.Files;
import com.file.upload.hdfs.HdfsUtils;
import com.file.upload.mapper.FilesMapper;
import com.file.upload.service.IFilesService;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.hadoop.fs.Hdfs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class DemoApplicationTests {

    @Autowired
    private IFilesService filesMapper;

    @Test
    public void add() {

        Files files = new Files();
        files.setFileLength(300L);
        files.setFileName("test");
        files.setFilePath("/xx/xx/xx");
        files.setFileType("none");

        String temp = DateFormatUtils.format(new Date(), "yyyy-MM-dd hh:dd:ss");
        files.setCreateTime(temp);

        filesMapper.save(files);
    }

    @Test
    public void testDelete() {
        JSONObject delete = HdfsUtils.delete("/files/2019-07-06/谷歌访问助手_v2.3.0.crx");
        System.out.println(delete);
    }
}
