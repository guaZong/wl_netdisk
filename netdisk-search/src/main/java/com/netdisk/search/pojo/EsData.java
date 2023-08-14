package com.netdisk.search.pojo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author lsj
 * @description EsData
 * @createDate 2023/8/7 16:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "es_data")
public class EsData {
    @Id
    private Integer dataId;
    @Field(type = FieldType.Integer)
    private Integer parentDataId;
    @Field(type =FieldType.Text,index = true,analyzer = "ik_max_word",searchAnalyzer = "ik_smart")
    private String name;
    @Field(type = FieldType.Integer)
    private Integer type;
    @Field(type = FieldType.Date)
    private Date createTime;
    @Field(type = FieldType.Integer)
    private Integer createBy;
    @Field(type = FieldType.Integer)
    private Integer isDelete;
    @Field(type = FieldType.Keyword)
    private String link;
    @Field(type = FieldType.Keyword)
    private String size;
    @Field(type = FieldType.Integer)
    private Integer fileId;
}
