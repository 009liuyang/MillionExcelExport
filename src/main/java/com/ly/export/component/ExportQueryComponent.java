package com.ly.export.component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.export.dao.StudentMapper;
import com.ly.export.entity.Student;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * 查询导出数据
 * @author xes
 */
public class ExportQueryComponent implements Callable<List<Student>> {
    private StudentMapper studentMapper;
    private int start;
    private int end;

    public ExportQueryComponent(StudentMapper studentMapper, int start, int end) {
        this.studentMapper = studentMapper;
        this.start = start;
        this.end = end;
    }

    @Override
    public List<Student> call() {

        IPage<Student> pages = studentMapper.selectPage(new Page(start, end), new QueryWrapper());
        return pages.getRecords();
    }

}
