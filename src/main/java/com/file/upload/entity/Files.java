package com.file.upload.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang.time.DateFormatUtils;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author lx
 * @since 2019-07-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Files implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String fileName;

    private String fileType;

    private Long fileLength;

    private String filePath;

    private String createTime;

    public Files() {
        String temp = DateFormatUtils.format(new Date(), "yyyy-MM-dd hh:dd:ss");
        this.createTime = temp;
    }


}
