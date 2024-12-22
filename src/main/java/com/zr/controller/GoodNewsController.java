package com.zr.controller;

import com.google.common.collect.Lists;
import com.zr.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
public class GoodNewsController {

    @RequestMapping("/getGoodNews")
    public void insertMarketData() throws IOException, InterruptedException {
        List<String> contentList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("喜报文案.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        contentList = contentList.stream().filter(f -> !StringUtils.isEmpty(f)).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(contentList) || contentList.size() % 5 != 0) {
            return;
        }

        Collections.reverse(contentList);
        contentList.forEach(System.out::println);


        List<List<String>> lists = Lists.partition(contentList, 5);
        int i = 1;
        for(List<String> item: lists) {
            try {
                // 读取输入图片
                BufferedImage image = ImageIO.read(new File("input.jpg"));

                // 创建一个Graphics2D对象来绘制
                Graphics2D g2d = image.createGraphics();

                // 设置抗锯齿以获得更好的文本质量
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                buttomArea(image, g2d, item);

                waistArea(image, g2d, item);

                // 释放Graphics2D对象
                g2d.dispose();

                // 保存输出图片
                ImageIO.write(image, "jpg", new File("output" + i +".jpg"));
                i++;
                System.out.println("Text added to image successfully!");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void waistArea(BufferedImage image, Graphics2D g2d, List<String> item) {
        // 设置字体和颜色
        Font font = new Font("Serif", Font.BOLD, 42);
        g2d.setFont(font);
        g2d.setColor(Color.WHITE);
        confirmXY(image, g2d, 10.5f, item.get(3));
        confirmXY(image, g2d, 11.5f, item.get(4));
    }

    private void buttomArea(BufferedImage image, Graphics2D g2d, List<String> item) {
        // 设置字体和颜色
        g2d.setFont(new Font("Serif", Font.BOLD, 70));
        g2d.setColor(Color.WHITE);
        confirmXY(image, g2d, 2, item.get(0));
        confirmXY(image, g2d, 3, item.get(1));
        confirmXY(image, g2d, 4, item.get(2));
    }

    private void confirmXY(BufferedImage image, Graphics2D g2d, float bottomTextLine, String word) {
        // 获取字体度量信息以确定文本位置
        FontMetrics fm = g2d.getFontMetrics();
        float x = (image.getWidth() - fm.stringWidth(word)) / 2;
        float y = image.getHeight() - fm.getHeight() * bottomTextLine;

        // 绘制文本
        g2d.drawString(word, x, y);
    }
}
