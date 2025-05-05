package org.wjx.zencoderweb.service;

import org.springframework.stereotype.Service;
import org.wjx.zencoderweb.zencoderkernel.PartitionerGenerator;
import org.wjx.zencoderweb.zencoderkernel.Zencoder;
import org.wjx.zencoderweb.zencoderkernel.partitioner.Partitioner;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EncoderService {
    Zencoder zencoder;
    Path directoryPath, dataPath;

    public void initialize(String pdir, String ddir) {
        // 初始化代码
        directoryPath = Paths.get(pdir);
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath); // 创建多级目录
                System.out.println("文件夹创建成功: " + directoryPath);
            } catch (Exception e) {
                System.err.println("文件夹创建失败: " + e.getMessage());
            }
        } else {
            System.out.println("文件夹已存在: " + directoryPath);
        }
        dataPath = Paths.get(ddir);
        zencoder = new Zencoder(null);
    }

    public boolean setZencoder(String selectedFile) {
        if (zencoder.getPartitionerName() != null && zencoder.getPartitionerName().equals(selectedFile))
            return true;
        System.out.println("正在加载新的划分器...");
        Path fullpath = directoryPath.resolve(selectedFile);
        Partitioner partitioner = PartitionerGenerator.loadPartitioner(fullpath.toString());
        if (partitioner == null) {
            return false;
        }
        zencoder.setPartitioner(partitioner);
        return true;
    }

    public String encode(String input) {
        return zencoder.encryptWithoutAES(input);
    }

    public String encodeAES(String input, SecretKey key) {
        try {
            return zencoder.encrypt("AES/CBC/PKCS5Padding", input, key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decode(String input) {
        String result = zencoder.decryptWithoutAES(input);
        if (result == null) {
            return "无效的字符串";
        } else {
            return result;
        }
    }

    public String decodeAES(String input, SecretKey key) {
        try {
            return zencoder.decrypt("AES/CBC/PKCS5Padding", input, key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String keygen() {
        try {
            SecretKey key = Zencoder.generateKey(256);
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getFiles() {
        List<String> fileNames = new ArrayList<>();
        System.out.println("开始生成划分器...");
        try {
            List<String> subfolderNames = Files.list(Paths.get(dataPath.toString()))
                    .filter(Files::isDirectory)
                    .map(Path::getFileName)  // 获取文件夹名称(不带路径)
                    .map(Path::toString)    // 转换为字符串
                    .collect(Collectors.toList());
            for (String subfolder : subfolderNames) {
                Partitioner partitioner = PartitionerGenerator.runGenerator(dataPath.resolve(subfolder).toString(), 12, subfolder + ".ser");
                PartitionerGenerator.savePartitioner(partitioner, directoryPath);
                fileNames.add(partitioner.getFileName());
            }

        } catch (IOException e) {
            return null;
        }

        return fileNames;
    }
}