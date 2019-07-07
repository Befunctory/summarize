package com.hdfsDemo;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class TestMkDir {

    private static String HDFS_URL = "hdfs://192.168.237.111:9000";

    @Test
    public void testUpload() throws Exception {
        //指定当前的Hadoop用户
        System.setProperty("HADOOP_USER_NAME", "root");
        //配置参数：指定nameNode地址
        Configuration conf = new Configuration();

        conf.set("fs.defaultFS", HDFS_URL);

        //创建一个客户端
        FileSystem fileSystem = FileSystem.get(conf);
        //构造一个输入流，从本地读入数据
        InputStream inputStream = new FileInputStream("E:\\develop_ware\\temp\\PCSoftDownloader_v1.1_webnew_11488@.exe");
        //构造一个输出流 指向HDFS
        OutputStream outputStream = fileSystem.create(new Path("/folder111/PCSoftDownloader_v1.1_webnew_11488@.exe"));


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


        //创建目录
        fileSystem.mkdirs(new Path("/testDir111"));

        fileSystem.close();


    }


    @Test
    public void testMKdir() throws Exception {
        //指定当前的Hadoop用户
        System.setProperty("HADOOP_USER_NAME", "root");
        //配置参数：指定nameNode地址
        Configuration conf = new Configuration();

        conf.set("fs.defaultFS", HDFS_URL);

        //创建一个客户端
        FileSystem fileSystem = FileSystem.get(conf);
        //创建目录
        fileSystem.mkdirs(new Path("/testDir111"));

        fileSystem.close();


    }

    @Test
    public void testDeletedir() throws Exception {
        //指定当前的Hadoop用户
        System.setProperty("HADOOP_USER_NAME", "root");
        //配置参数：指定nameNode地址
        Configuration conf = new Configuration();

        conf.set("fs.defaultFS", HDFS_URL);

        //创建一个客户端
        FileSystem fileSystem = FileSystem.get(conf);
        //创建目录
        fileSystem.delete(new Path("/testDir111"));

        fileSystem.close();


    }

}
