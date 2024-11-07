package web.sy.basicimagebed.controller.man;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1")
@Tag(name = "Management API", description = "管理接口")

public class ManageAPIv1 {

    @GetMapping("/strategies")
    public String getStrategy() {
        return "strategies";
    }
}
