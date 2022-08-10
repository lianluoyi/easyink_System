package com.easyink;

import com.dtflys.forest.springboot.annotation.ForestScan;
import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 类名:启动程序
 * @author: 1*+
 * @date: 2021-08-17 14:18
 */
@Slf4j
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, PageHelperAutoConfiguration.class})
@MapperScan("com.easyink.*.mapper")
@ForestScan(basePackages = {"com.easyink.wecom.client", "com.easyink.wecom.wxclient"})
@EnableAsync
public class EasyWeComApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyWeComApplication.class, args);
        log.info("\n" +
                "                     ,----,                                     ,----,                                                                                                  \n" +
                "                   ,/   .`|                                   ,/   .`|                                                                                                  \n" +
                "  .--.--.        ,`   .'  :    ,---,        ,-.----.        ,`   .'  :           .--.--.                     ,----..     ,----..       ,---,.   .--.--.      .--.--.    \n" +
                " /  /    '.    ;    ;     /   '  .' \\       \\    /  \\     ;    ;     /          /  /    '.           ,--,   /   /   \\   /   /   \\    ,'  .' |  /  /    '.   /  /    '.  \n" +
                "|  :  /`. /  .'___,/    ,'   /  ;    '.     ;   :    \\  .'___,/    ,'          |  :  /`. /         ,'_ /|  |   :     : |   :     : ,---.'   | |  :  /`. /  |  :  /`. /  \n" +
                ";  |  |--`   |    :     |   :  :       \\    |   | .\\ :  |    :     |           ;  |  |--`     .--. |  | :  .   |  ;. / .   |  ;. / |   |   .' ;  |  |--`   ;  |  |--`   \n" +
                "|  :  ;_     ;    |.';  ;   :  |   /\\   \\   .   : |: |  ;    |.';  ;           |  :  ;_     ,'_ /| :  . |  .   ; /--`  .   ; /--`  :   :  |-, |  :  ;_     |  :  ;_     \n" +
                " \\  \\    `.  `----'  |  |   |  :  ' ;.   :  |   |  \\ :  `----'  |  |            \\  \\    `.  |  ' | |  . .  ;   | ;     ;   | ;     :   |  ;/|  \\  \\    `.   \\  \\    `.  \n" +
                "  `----.   \\     '   :  ;   |  |  ;/  \\   \\ |   : .  /      '   :  ;             `----.   \\ |  | ' |  | |  |   : |     |   : |     |   :   .'   `----.   \\   `----.   \\ \n" +
                "  __ \\  \\  |     |   |  '   '  :  | \\  \\ ,' ;   | |  \\      |   |  '             __ \\  \\  | :  | | :  ' ;  .   | '___  .   | '___  |   |  |-,   __ \\  \\  |   __ \\  \\  | \n" +
                " /  /`--'  /     '   :  |   |  |  '  '--'   |   | ;\\  \\     '   :  |            /  /`--'  / |  ; ' |  | '  '   ; : .'| '   ; : .'| '   :  ;/|  /  /`--'  /  /  /`--'  / \n" +
                "'--'.     /      ;   |.'    |  :  :         :   ' | \\.'     ;   |.'            '--'.     /  :  | : ;  ; |  '   | '/  : '   | '/  : |   |    \\ '--'.     /  '--'.     /  \n" +
                "  `--'---'       '---'      |  | ,'         :   : :-'       '---'                `--'---'   '  :  `--'   \\ |   :    /  |   :    /  |   :   .'   `--'---'     `--'---'   \n" +
                "                            `--''           |   |.'                                         :  ,      .-./  \\   \\ .'    \\   \\ .'   |   | ,'                             \n" +
                "                                            `---'                                            `--`----'       `---`       `---`     `----'                               \n" +
                "                                                                                                                                                                        \n");
    }
}
