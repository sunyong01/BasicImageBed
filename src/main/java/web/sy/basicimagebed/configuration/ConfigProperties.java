package web.sy.basicimagebed.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Getter
@Component
public class ConfigProperties {

    @Value("${image_bed.local_path}")
    private String imageLocalStoragePath;

    @Value("${image_bed.allow_size_kb}")
    private int allowSizeKb;

    @Value("#{'${image_bed.allow_suffix}'.split(',')}")
    private Set<String> allowSuffix;

    @Value("${image_bed.server_url}")
    private String serverBaseUrl;

    @Value("${image_bed.auth.allow-guest:false}")
    private boolean allowGuest;

    @Value("${image_bed.auth.allow-register:false}")
    private boolean allowRegister;

    @Value("${image_bed.debug_mode:false}")
    private boolean debug;

}