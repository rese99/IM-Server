package com.yun.im.controller;

import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

@Component
@RestController
public class APK {
    File file1 = new File("im.apk");
    @RequestMapping(value = "/GetApk", method = RequestMethod.POST)
    public void GetApk(@RequestPart("file") MultipartFile file) {
        try {
            file.transferTo(file1.getAbsoluteFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/PostApk", method = RequestMethod.GET)
    public void PostApk(@RequestParam("filename") String fileName, HttpServletResponse response) {
        try (FileInputStream fis = new FileInputStream(file1); BufferedInputStream bis = new BufferedInputStream(fis); OutputStream os = response.getOutputStream()) {
            byte[] bytes = new byte[bis.available()];
            bis.read(bytes);
            response.reset();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Length", String.valueOf(new FileInputStream(file1).available()));
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            os.write(bytes);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/Check", method = RequestMethod.GET)
    public String ApkUrl(@RequestParam("version") String version) {
        try (ApkFile apkFile = new ApkFile(file1)) {
            ApkMeta apkMeta = apkFile.getApkMeta();
            if (apkMeta.getVersionCode() > Integer.parseInt(version)) {
                return "http://110.41.132.226:1314/PostApk?filename=im.apk";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
