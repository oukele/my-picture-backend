package com.oukele.picturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;


/**
 * 批量导入图片请求
 *
 * @author oukele
 */
@Data
public class PictureUploadByBatchRequest implements Serializable {

    /**
     * 名称前缀
     */
    private String namePrefix;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 抓取数量
     */
    private Integer count = 10;

    private static final long serialVersionUID = 1L;
}

