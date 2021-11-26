package com.myworld.cauth.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;

import java.io.File;
import java.lang.reflect.Method;


@Slf4j
public class IPUtil {

    public static String getCityInfo(String ip){
        try {
            //db
            String dbPath = IPUtil.class.getResource("/ip2region.db").getPath();
            log.info("一开始的ip2region文件地址为" + dbPath);

            File file = new File(dbPath);
            if (!file.exists()) {
                log.info("未能找到一开始的ip2region.db文件");
                String tmpDir = System.getProperties().getProperty("java.io.tmpdir");
                dbPath = tmpDir + "/ip2region.db";
                log.info("调整后的ip2region文件路径为：" + dbPath);
                file = new File(dbPath);
                FileUtils.copyInputStreamToFile(IPUtil.class.getClassLoader().getResourceAsStream("classpath:ip2region.db"), file);
            }
            //查询算法
            int algorithm = DbSearcher.BTREE_ALGORITHM; //B-tree
            //DbSearcher.BINARY_ALGORITHM //Binary
            //DbSearcher.MEMORY_ALGORITYM //Memory
            try {
                DbConfig config = new DbConfig();
                DbSearcher searcher = new DbSearcher(config, dbPath);
                //define the method
                Method method = null;
                switch ( algorithm )
                {
                    case DbSearcher.BTREE_ALGORITHM:
                        method = searcher.getClass().getMethod("btreeSearch", String.class);
                        break;
                    case DbSearcher.BINARY_ALGORITHM:
                        method = searcher.getClass().getMethod("binarySearch", String.class);
                        break;
                    case DbSearcher.MEMORY_ALGORITYM:
                        method = searcher.getClass().getMethod("memorySearch", String.class);
                        break;
                }

                // 以下IP赋值仅为测试使用
                // ip = "117.136.67.80";

                DataBlock dataBlock;
                if (!Util.isIpAddress(ip)) {
                    log.info("错误: 无效的ip地址");
                }
                dataBlock  = (DataBlock) method.invoke(searcher, ip);
                return dataBlock.getRegion();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

}
