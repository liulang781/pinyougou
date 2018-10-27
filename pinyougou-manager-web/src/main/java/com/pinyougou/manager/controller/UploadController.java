package com.pinyougou.manager.controller;


import Bean.Result;
import com.pinyougou.utils.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String file_server_url;

    @RequestMapping("/upload.do")
    public Result uploadFile(MultipartFile file) throws Exception {

        //获取上传文件的文件名
        String filename = file.getOriginalFilename();
        //获取文件的扩展名
        String extName = filename.substring(filename.lastIndexOf(".") + 1);

        try {
            //记载配置文件找到上传的服务端
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fdfs_client.conf");
            //返回服务端的路径
            String path = fastDFSClient.uploadFile(file.getBytes(), extName);
            //拼接返回的url和配置文件中ip拼接长完成的路径
            String url=file_server_url+ path;
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }


    }
}
