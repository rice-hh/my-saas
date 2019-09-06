package com.xxl.job.executor.service.business;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;

import com.ces.xarch.core.web.listener.XarchListener;
import com.ces.xmgl.business.service.ProjectDynamicService;
import com.cesgroup.core.util.StringUtils;
import com.xxl.job.core.util.DateUtil;

public class PmJobService {
	
	private void refreshReceiveData(String code, String tableName) {
		String currDate = DateUtil.getCurrentDate();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, +1);
		c.add(Calendar.DAY_OF_MONTH, -1);
		String endDate = sf.format(c.getTime());// 本期结束日期
		String csql = null;
		String sql = "drop table "+tableName;
		jdbcTemplate.execute(sql);//先删除表
		if((code.equals("actual_ontime") || code.equals("actual_overtime"))) {
			csql = ErpCommon.getService(ProjectDynamicService.class).queryPaymentActual(code, currDate, endDate, "","", "");
		} else {
			csql = ErpCommon.getService(ProjectDynamicService.class).queryPaymentSql(code, currDate, endDate, "", "", "");
		}
		sql = "create table "+tableName+" as select * from ("+csql+") t";
		jdbcTemplate.execute(sql);//重新创建表
		sql = "delete from "+tableName;
		jdbcTemplate.execute(sql);//删除表数据
		sql = "insert into "+tableName+" select t.* from ("+csql+") t ";
		jdbcTemplate.execute(sql);
	}
}
