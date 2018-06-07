package club.zhcs.thunder.controller;

import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import club.zhcs.captcha.ImageVerification;
import club.zhcs.thunder.BootNutzVueApplication;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
@Controller
@Api(value = "Index", tags = {"通用"})
public class IndexController {
    Log logger = Logs.get();

    @GetMapping("/captcha")
    @ApiOperation("验证码")
    public void captcha(@RequestParam(value = "length", required = false, defaultValue = "4") @ApiParam(value = "验证码长度", required = false, defaultValue = "4") int length,
                        @ApiIgnore HttpServletResponse resp,
                        @ApiIgnore HttpSession session)
            throws IOException {
        resp.setContentType("image/jpeg");
        resp.setHeader("Pragma", "No-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", 0);

        OutputStream out = resp.getOutputStream();
        // 输出图象到页面
        ImageVerification iv = new ImageVerification();

        if (length != 0) {
            iv.setIMAGE_VERIFICATION_LENGTH(length);
        }
        if (ImageIO.write(iv.creatImage(), "JPEG", out)) {
            logger.debug("写入输出流成功:" + iv.getVerifyCode() + ".");
        } else {
            logger.debug("写入输出流失败:" + iv.getVerifyCode() + ".");
        }
        session.setAttribute(BootNutzVueApplication.CAPTCHA_KEY, iv.getVerifyCode());
        // 以下关闭输入流！
        out.flush();
        out.close();
    }

}
