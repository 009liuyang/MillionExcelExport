package com.ly.export.service.impl;

import com.google.common.base.Charsets;
import com.ly.export.component.ExportQueryComponent;
import com.ly.export.dao.ExportMapper;
import com.ly.export.dao.StudentMapper;
import com.ly.export.entity.Student;
import com.ly.export.service.ExportService;
import com.ly.export.utils.OSSAppend;
import com.ly.export.utils.OSSUtil;
import com.ly.export.utils.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@Slf4j
public class ExportServiceImpl implements ExportService {

    @Resource
    private ExportMapper exportMapper;
    @Resource
    private StudentMapper studentMapper;
    @Resource
    private OSSUtil ossUtil;

    // 阀值
    int threshold = 50;
    // 总数据量
    int totalSize = 0;

    @Override
    public void doExport() {
        try {

            totalSize = exportMapper.count();

            /**
             * 任务执行总次数(例总数据量为10000，一次任务处理50条，共需执行10000/50=200次)
             */
            int totalTimes = totalSize /threshold + 1;

            /**
             * 任务分割临界值(如totalTimes=200，需要按任务切割阀值100 切割成两部分执行，这样做的目的是为了oss续传以及更新上传进度)
             */
            int startTimes = 1;
            int endTimes = totalTimes>100 ? 100 : totalTimes;

            /**
             *  oss续传对象(每次上传完都需要返回上传结果对象，以便下次上传指定前一次上传的文件位置)
             */
            OSSAppend ossAppend = null;

            do {
                log.info("报班收费明细导出|批次查询|startTimes:{},endTimes:{}", startTimes, endTimes);
                List<Student> sumList = handlerPartData(startTimes, endTimes);
                if(startTimes == 1){
                    // 首次上传需要拼装csv表头 及 初始化ossAppend
                    ossAppend = ossUtil.createAppend("学生导出表格", generateExcelInputStream(sumList, true));
                }else {
                    ossAppend = ossUtil.append(ossAppend, generateExcelInputStream(sumList, false));
                }

                startTimes += 100;
                endTimes += 100;
            }while (totalTimes >= (startTimes - 1));

            log.info("报班收费明细导出成功,exportId:{}");
        }catch (Exception e){
            log.error("报班收费明细导出|异常. exportId={}", e);
        }
    }

    /**
     * 并行分页查询数据，共查询100次，每次查询50条
     *
     * @param vo 查询数据参数
     * @param startTimes 查询次数起始值
     * @param endTimes 查询次数结束值
     */
    private List<Student> handlerPartData(int startTimes, int endTimes) throws InterruptedException, ExecutionException {
        List<Future<List<Student>>> futureResult = new ArrayList<>();
        // 门闩 主线程等待其他线程执行
        CountDownLatch countDownLatch = new CountDownLatch(endTimes - startTimes);

        for (int i = startTimes; i <= endTimes; i++) {

            try {
                Future<List<Student>> callResult = ThreadPoolUtil.executeTask(new ExportQueryComponent(studentMapper, startTimes, endTimes));
                futureResult.add(callResult);
            }catch (Exception e){
                log.error("报班收费明细导出|并发查询执行异常", e);
                throw new RuntimeException("报班收费明细导出|并发查询执行异常");
            }finally {
                countDownLatch.countDown();
            }
        }

        countDownLatch.await();
        // 组装结果集
        List<Student> sumList = new ArrayList<>();
        for(Future<List<Student>> result : futureResult){
            sumList.addAll(result.get());
        }
        return sumList;
    }


    /**
     * 报班收费明细导出生成excel流
     *
     * @param sum
     * @return
     */

    private ByteArrayInputStream generateExcelInputStream(List<Student> sum, boolean first) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        if(first){
            // 防止中文乱码
            stringBuilder.append(new String(new byte[] { (byte) 0xEF, (byte) 0xBB,(byte) 0xBF }));
            stringBuilder.append("编号,学生姓名,学生班级 \r\n");
        }

        for (Student dto : sum){
            stringBuilder.append(generateValue(dto));
        }
        return new ByteArrayInputStream(stringBuilder.toString().getBytes(Charsets.UTF_8.toString()));
    }

    public <T> String generateValue(T t) {
        Class<?> _class = t.getClass();
        Field[] fields = _class.getDeclaredFields();

        StringBuffer builder = new StringBuffer();
        for(Field field : fields){
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(t);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            builder.append(value).append(",");
        }

        builder.append("\r\n");
        return builder.toString();
    }

}
