package com.ly.export.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.export.entity.Student;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportMapper extends BaseMapper<Student> {

    @Select("select count(1) from t_student")
    int count();

}
