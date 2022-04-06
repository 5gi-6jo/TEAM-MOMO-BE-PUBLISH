package com.sparta.team6.momo.utils.amazonS3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@ConfigurationProperties(prefix = "cloud.aws.s3")
@Component
@Setter
public class S3Component {
    private String bucket;
}
