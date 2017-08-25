package com.ylc.main;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件名：
 * 作者：tree
 * 时间：2017/7/28
 * 描述：
 * 版权：亚略特
 */
public class CopyMain {

    public static void main(String[] args) {

        Map<String, Long> countRes = new HashMap<>();

        JFrame frame = new JFrame("破解小程序");
        JPanel rootPane = new JPanel();
        rootPane.setLayout(null);

        JLabel copyLable = new JLabel();
        copyLable.setBounds(20, 20, 100, 25);
        copyLable.setText("默认解密路径:");
        rootPane.add(copyLable);

        JTextField copySrc = new JTextField();
        copySrc.setText("C:\\copy");
        copySrc.setBounds(120, 20, 450, 25);
        rootPane.add(copySrc);

        JLabel changeLable = new JLabel();
        changeLable.setBounds(20, 60, 100, 25);
        changeLable.setText("默认转换路径:");
        rootPane.add(changeLable);

        JTextField changeSrc = new JTextField();
        changeSrc.setText("C:\\change");
        changeSrc.setBounds(120, 60, 450, 25);
        rootPane.add(changeSrc);

        JLabel resLable = new JLabel();
        resLable.setBounds(20, 100, 100, 25);
        resLable.setText("操作结果:");
        rootPane.add(resLable);

        JTextArea result = new JTextArea();
        result.setBounds(120, 100, 450, 200);
        rootPane.add(result);

        JButton copy = new JButton();
        copy.setText("解密");
        copy.setBounds(100, 320, 165, 25);
        rootPane.add(copy);

        JButton change = new JButton();
        change.setText("转换");
        change.setBounds(300, 320, 165, 25);
        rootPane.add(change);

        copy.addActionListener(e -> {
            if (Files.isDirectory(Paths.get(copySrc.getText())) && Files.exists(Paths.get(copySrc.getText()))) {
                try {
                    Files.walk(Paths.get(copySrc.getText()))
                            .filter(file -> !Files.isDirectory(Paths.get(file.toUri())) && isFieTypeHave(file.toString()))
                            .forEach(file -> {
                                try {
                                    List<String> lines = Files.readAllLines(Paths.get(file.toUri()), StandardCharsets.UTF_8);
                                    Files.write(Paths.get(file.toString() + "1"), lines);
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            });
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                result.setText("解密完成");
            } else {
                result.setText("指定的文件目录不存在/正确");
            }
        });
        change.addActionListener(e -> {
            if (Files.isDirectory(Paths.get(changeSrc.getText())) && Files.exists(Paths.get(changeSrc.getText()))) {
                try {
                    Files.walk(Paths.get(changeSrc.getText()))
                            .forEach(file -> {
                                if (!Files.isDirectory(Paths.get(file.toUri())) && isFieTypeHave(file.toString())) {
                                    try {
                                        Files.deleteIfExists(Paths.get(file.toUri()));
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                             });
                    Files.walk(Paths.get(changeSrc.getText())).forEach(file -> {
                        System.out.println(file.getFileName());
                        System.out.println(file.toFile().getPath());
                        if (!file.toFile().isDirectory() && isFieTypeEndHave(file.toString())) {
                            try {
                                Files.copy(file.toFile().toPath(),Paths.get(file.toFile().getAbsolutePath()
                                        .substring(0,file.toFile().getAbsolutePath().length()-1)));
                                file.toFile().delete();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                result.setText("转换完成");
            } else {
                result.setText("指定的文件目录不存在/正确");
            }
        });

        frame.setContentPane(rootPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(rootPane);//居中
        frame.setVisible(true);
    }

    public static boolean isFieTypeHave(String fileName){
        if(fileName.endsWith("java") || fileName.endsWith("pom") || fileName.endsWith("xml") || fileName.endsWith("txt")){
            return true;
        }else {
            return false;
        }
    }

    public static boolean isFieTypeEndHave(String fileName){
        if(fileName.endsWith("java1") || fileName.endsWith("pom1") || fileName.endsWith("xml1") || fileName.endsWith("txt1")){
            return true;
        }else {
            return false;
        }
    }
}
