package org.caesar.controller;

import org.caesar.domain.common.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/image")
public class ImageController {

    @Autowired
    private ResourceLoader resourceLoader;
    //TODO: 改成 fastDFS
    //todo: 这个路径要改一下
    @Value("${upload.path}") // 从配置文件中获取上传文件的存储路径
    private String uploadPath;

    private final String baseDir = System.getProperty("user.dir");

    //上传图片
    @PostMapping("/upload")
    public Response<Void> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Response.error("上传失败：未附带文件或文件为空");
        }

        try {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 构建文件存储路径
            String filePath = Paths.get(baseDir + uploadPath, fileName).toString();

            // 将文件保存到指定路径
            Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

            return Response.ok();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.error("保存文件失败");
        }

    }

    @GetMapping("/{imageName}")
    public Resource getImage(@PathVariable String imageName) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:imgs/" + imageName);
        String classpath = ClassLoader.getSystemResource("").getPath();

        if (resource.exists()) {
            return resource;
        } else {
            // 处理文件不存在的情况
            throw new IOException("Image not found");
        }
    }
}
