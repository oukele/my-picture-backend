package com.oukele.picturebackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用的删除实体类
 * @author oukele
 */
@Data
public class DeleteRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
