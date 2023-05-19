package com.qiaofang.jiagou.crawler.against;

import com.alibaba.druid.filter.config.ConfigTools;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author shihao.liu
 * @version 1.0
 * @date 2019-06-11 15:11
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class BaseTest {


    public static void main(String[] args) throws Exception {
        String publicKey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAI9gORPrIVWH0G498rlyComZlH/Fqcf6oXGPOqYxnK94xltH8qLDuGmyhwDFuHwvz0k++KLnMdykHEkD5bPe++kCAwEAAQ==";
        String privateKey = "MIIBUwIBADANBgkqhkiG9w0BAQEFAASCAT0wggE5AgEAAkEAj2A5E+shVYfQbj3yuXIKiZmUf8Wpx/qhcY86pjGcr3jGW0fyosO4abKHAMW4fC/PST74oucx3KQcSQPls9776QIDAQABAkAY/yu0X1Ame1FJhx9kWY+gwdf0FuAs73NqOxB8nTW2GdNDeJGA/3NozDrIZgmS3vYX92geKCzcPWiLKeroEtcpAiEA1EjKoFzuKU0LcQRccOf4zll3YILFmN+t1f6dAtCKkU8CIQCs5ra+/eSEN65mOg0yDL3zNDdCUvB2hRbKBPsSV5KhRwIgGTn9OklQ0/+f2HJYOeKXIo5nLUTdDmmmUsPLDMaTcOkCIAoTS7TngKh/wibs3RX8jhkdAtdXk/GkIcbV8XeJKN9DAiAFE/c9NSyd/PijTUrrE03uUJ7pviE+IDOleloRy/zadw==";
        System.out.println(ConfigTools.encrypt(privateKey, "J@j2*@jcf?viweI@!"));
    }
}
