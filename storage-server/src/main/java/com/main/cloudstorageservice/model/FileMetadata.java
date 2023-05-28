package com.main.cloudstorageservice.model;

import com.main.cloudstorageservice.model.base.BaseEntity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FileMetadata extends BaseEntity<Long> {

    private String name;

    private String contentType;

    private long size;

    private String bucketName;

    private String objectName;

    private int version;

}