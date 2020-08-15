package com.ly.export.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.AppendObjectRequest;
import com.aliyun.oss.model.AppendObjectResult;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * 阿里云OSS上传工具
 * @author xes
 */
@Slf4j
@Component
@Configuration
public class OSSUtil {

    @Value("${oss.config.endpoint}")
    private String endpoint;
    @Value("${oss.config.accessKeyId}")
    private String accessKeyId;
    @Value("${oss.config.accessKeySecret}")
    private String accessKeySecret;
    @Value("${oss.config.bucketName}")
    private String bucketName;
    @Value("${oss.config.env}")
    private String env;

    private OSS ossClient;


    @PostConstruct
    private void init(){
        try{
            ossClient =  new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        }catch (Exception e){
            log.error("OSS|初始化异常|{}",e);
        }
    }

    public String getDownUrlPrefix(){
        return "https://" + bucketName + "." + endpoint.replace("http://", "") + "/" + env;
    }

    /**
     * 上传
     *
     * @param inputStream
     * @param objectName
     * @return
     */
    public Boolean uploadByInputStream(InputStream inputStream, String objectName){
        try{
            log.info("OSS上传文件开始|objectName={}", env + objectName);
            ossClient.putObject(bucketName, env + objectName, inputStream);
            log.info("OSS上传文件成功|objectName={}", env + objectName);
            return true;
        }catch (Exception e){
            log.error("OSS上传文件异常|objectName={}", objectName, e);
        }
        return false;
    }


    /**
     * oss文件下载
     *
     * @param objectName
     * @param response
     */
    public void down(String objectName, HttpServletResponse response) throws IOException {

        try {
            OSSObject ossObject = ossClient.getObject(bucketName, env + objectName);
            InputStream inputStream = ossObject.getObjectContent();
            BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            String fileName = URLEncoder.encode(objectName, "UTF-8");
            response.setHeader("Content-Disposition","attachment;filename="+fileName);

            byte[] car = new byte[1024];
            int L;

            while ((L = inputStream.read(car)) != -1) {
                if (car.length != 0) {
                    outputStream.write(car, 0, L);
                }
            }

            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            log.error("【[报班收费明细导出查询]异常. objectName={}", objectName, e);
            throw e;
        }
    }


    public OSSAppend createAppend(String objectName, InputStream inputStream){
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("text/csv;charset=UTF-8");
        AppendObjectRequest request = new AppendObjectRequest(bucketName, env + objectName, inputStream ,meta);
        request.setPosition(0L);
        AppendObjectResult result = ossClient.appendObject(request);
        OSSAppend ossAppend = new OSSAppend(request, result);
        return ossAppend;
    }

    public OSSAppend append(OSSAppend ossAppend, InputStream inputStream){
        AppendObjectRequest request = ossAppend.getRequest();
        AppendObjectResult result = ossAppend.getResult();

        request.setPosition(result.getNextPosition());
        request.setInputStream(inputStream);

        // 返回最新的的AppendObjectResult，用与计算下次文件上传位置
        AppendObjectResult newResult = ossClient.appendObject(request);
        ossAppend.setRequest(request);
        ossAppend.setResult(newResult);
        return ossAppend;
    }

}
