package com.ylc.main;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author Yvan Jiang
 * @version 0.0.1
 * @Title DecryptMain
 * @note 命令行加解密
 * @note Copyright 2016 by Aratek . All rights reserved
 * @time 2017/8/24
 **/
public class DecryptMain {
    private static final String DEFAULT_SRC = "C:\\decrypt";
    //    private static final String DEFAULT_SRC = "E:\\DecryptTest\\decrypt";
    private static final String RESULT = "C:\\result";
    //    private static final String RESULT = "E:\\DecryptTest\\result";
    private static String SUFFIX = ".shit";
    private static final Logger LOG = LoggerFactory.getLogger(DecryptMain.class);
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    public static void main(String[] args) throws IOException {
        String srcRootDic = DEFAULT_SRC;
        String resRootDic = RESULT;
        if(args.length >= 2){
            srcRootDic = args[0];
            resRootDic = args[1];
        }
        if(args.length ==1 ){
            SUFFIX = args[0];
        }
        clearDic(resRootDic);
        
        //加密过程
        if (Files.isDirectory(Paths.get(srcRootDic)) && Files.exists(Paths.get(srcRootDic))) {
            String finalSrcRootDic = srcRootDic;
            String finalResRootDic = resRootDic;
            Files.walk(Paths.get(srcRootDic))
                    .forEach(file -> {
                        try {
                            if(!Files.isDirectory(Paths.get(file.toUri()) )) {
                                CompletableFuture.runAsync(()-> copyOrRewriteFile(finalSrcRootDic, finalResRootDic, file),
                                        executor);
                            }else {
                                String srcDir = file.toString().substring(finalSrcRootDic.length());
                                String desDir = finalResRootDic + srcDir;
                                FileUtils.forceMkdir(new File(desDir));
                            }
                        } catch (IOException e1) {
                            LOG.error("文件重写失败 :"+file,e1);
                        }
                    });
            LOG.info("解密完成");
        } else {
            LOG.error("目录不存在");
        }
    }
    
    private static void copyOrRewriteFile(String finalSrcRootDic, String finalResRootDic, Path file){
        try {
            String org = file.toString();
            String src = org.substring(finalSrcRootDic.length());
            LOG.debug(src);
            String des = finalResRootDic + src;
            LOG.info(file.toFile().getPath() + " ==> " + des);
            if (!isFileIgnore(file.toString())) {
//                                    CompletableFuture.runAsync(());
                //先加一个可识别后缀
                File newSrcFile = new File(des + ".java");
                file.toFile().renameTo(newSrcFile);
                //重写文件
                byte[] data = Files.readAllBytes(Paths.get(newSrcFile.toURI()));
                Files.write(Paths.get(des + SUFFIX), data);
                newSrcFile.renameTo(new File(org));
            }
//            else {
//                //重命名并复制
//                Files.copy(Paths.get(file.toUri()), Paths.get(des + SUFFIX));
//            }
        }catch (IOException ioe){
            LOG.error("文件处理错误 "+file,ioe);
        }
    }
    
    /**
     * 清空目标文件夹
     * @param resRootDic
     * @throws IOException
     */
    private static void clearDic(String resRootDic) throws IOException {
        if (Files.isDirectory(Paths.get(resRootDic)) && Files.exists(Paths.get(resRootDic))) {
            Files.walk(Paths.get(resRootDic))
                    .forEach(file -> {
                        if (!(Files.isDirectory(Paths.get(file.toUri())) || isFileIgnore(file.toString()))) {
                            try {
                                Files.deleteIfExists(Paths.get(file.toUri()));
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
        }
    }
    
    /**
     * 一般大的二进制文件，直接跳过
     * @param fileName
     * @return
     */
    private static boolean isFileIgnore(String fileName){
        return fileName.endsWith("exe") || fileName.endsWith("rar")
                || fileName.endsWith("zip") || fileName.endsWith("class")
                || fileName.endsWith("war");
    }
}
