package com.microservice.archchatmessagingservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attachment {
    String id;
    String fileName;
    String contentType;
    Long size;
    String key;
    String url;
    Double duration;
}
