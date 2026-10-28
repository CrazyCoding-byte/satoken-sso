package yzx.saauth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"yzx.saauth", "yzx.sacommon"})
@MapperScan({ "yzx.sacommon.mapper"})
public class SaAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaAuthApplication.class, args);
    }

}
