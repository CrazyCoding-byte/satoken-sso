package yzx.samodel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"yzx.samodel", "yzx.sacommon"})
@MapperScan({"yzx.samodel.mapper", "yzx.sacommon.mapper"})
public class SaModelApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaModelApplication.class, args);
    }

}
