package ca.skipatrol.cnswap.controller;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CnswapErrorController implements ErrorController  {

    @RequestMapping("/error")
    public String handleError() {
        //do something like logging
        return "error";
    }
}