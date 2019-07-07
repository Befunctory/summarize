package com.file.upload.hdfs;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class HdfsUtils {


    private static String HDFS_URL = "hdfs://192.168.237.111:9000";

    public static JSONObject upload(InputStream inputStream, String fileName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "上传失败");
        try {
            if (inputStream == null || StringUtils.isBlank(fileName)) {
                jsonObject.put("message", "inputStream 和 fileName 不能为空");
                return jsonObject;
            }

            //指定当前的Hadoop用户
            System.setProperty("HADOOP_USER_NAME", "root");
            //配置参数：指定nameNode地址
            Configuration conf = new Configuration();

            conf.set("fs.defaultFS", HDFS_URL);

            //创建一个客户端
            FileSystem fileSystem = FileSystem.get(conf);
            //构造一个输入流，从本地读入数据
            // InputStream inputStream = new FileInputStream("E:\\develop_ware\\temp\\PCSoftDownloader_v1.1_webnew_11488@.exe");
            //构造一个输出流 指向HDFS
            OutputStream outputStream = fileSystem.create(new Path(fileName));


            //构造一个缓冲区
            byte[] buffer = new byte[1024];

            //长度 0
            int len = 0;

            //读入数据
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }


            outputStream.flush();

            inputStream.close();
            outputStream.close();

            fileSystem.close();
            jsonObject.put("message", "上传成功");
        } catch (Exception e) {
            jsonObject.put("message", "后台异常");
        }


        return jsonObject;

    }

    public static JSONObject delete(String fileName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", fileName + "  删除失败");
        try {
            if (StringUtils.isBlank(fileName)) {
                jsonObject.put("message", "fileName 不能为空");
                return jsonObject;
            }

            //指定当前的Hadoop用户
            System.setProperty("HADOOP_USER_NAME", "root");
            //配置参数：指定nameNode地址
            Configuration conf = new Configuration();

            conf.set("fs.defaultFS", HDFS_URL);

            //创建一个客户端
            FileSystem fileSystem = FileSystem.get(conf);


            boolean flag = fileSystem.delete(new Path(fileName));


            fileSystem.close();
            if (flag) {
                jsonObject.put("message", fileName + "  删除成功");
            }

        } catch (Exception e) {
            jsonObject.put("message", "后台异常");
        }

        return jsonObject;

    }

    public static JSONObject download(String absoultePath, String fileName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", absoultePath + "  下载失败");
        try {
            if (StringUtils.isBlank(absoultePath) || StringUtils.isBlank(fileName)) {
                jsonObject.put("message", "fileName 和 fileName 不能为空");
                return jsonObject;
            }

            //指定当前的Hadoop用户
            System.setProperty("HADOOP_USER_NAME", "root");
            //配置参数：指定nameNode地址
            Configuration conf = new Configuration();

            conf.set("fs.defaultFS", HDFS_URL);

            //创建一个客户端
            FileSystem fileSystem = FileSystem.get(conf);

            Path path = new Path(absoultePath);

            FSDataInputStream fsDataInputStream = fileSystem.open(path);

            jsonObject.put("inputStream",fsDataInputStream);

            jsonObject.put("message", "下载成功");
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("message", "后台异常");
        }

        return jsonObject;

    }
}
