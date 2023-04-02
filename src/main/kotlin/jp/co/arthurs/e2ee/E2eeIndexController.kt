package jp.co.arthurs.e2ee

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class E2eeIndexController {
    @RequestMapping("/")
    fun method1(): String? {
        return "/index.html"
    }
}