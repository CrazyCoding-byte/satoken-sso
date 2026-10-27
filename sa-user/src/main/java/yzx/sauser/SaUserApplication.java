package yzx.sauser;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"yzx.sauser", "yzx.sacommon"})
@MapperScan({"yzx.sauser.mapper", "yzx.sacommon.mapper"})
public class SaUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaUserApplication.class, args);
    }

}
