package com.zr.compent;

import com.zr.constant.RedissonConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

@Configuration
public class MyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        System.out.println(">>> MyImportSelector.selectImports() called");
        return new String[]{RedissonConfig.class.getName()};
    }

    public static void main(String[] args) {
        System.out.println("aaa");
    }
}
