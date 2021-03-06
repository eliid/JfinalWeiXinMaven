package com.javen.weixin.template;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.kit.JsonKit;

public class TempToJson {
	public static String getTempJson(String touser,String template_id,String topcolor,String url,DataItem data){
		TempEntity entity=new TempEntity();
		entity.setTouser(touser);
		entity.setTemplate_id(template_id);
		entity.setTopcolor(topcolor);
		entity.setUrl(url);
		entity.setData(data);
		
		return JsonKit.toJson(entity);
	}
	
	public static void main(String[] args) {
		DataItem dataItem=new DataItem();
		dataItem.setFirst(new TempItem("您好,Javen,欢迎使用模版消息", "#743A3A"));
		dataItem.setProduct(new TempItem("微信公众平台测试", "#FF0000"));
		dataItem.setPrice(new TempItem("39.8元", "#c4c400"));
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日  HH时mm分ss秒");
		String time=sdf.format(new Date());
		dataItem.setTime(new TempItem(time, "#0000FF"));
		dataItem.setRemark(new TempItem("您的订单已提交，我们将尽快发货，祝生活愉快", "#008000"));
		
		String json=getTempJson("ofkJSuGtXgB8n23e-y0kqDjJLXxk", "2u8QNhtMQvp9tVbINJuwRs6pdMZmrADN3c2S347rLnM",
				"#743A3A", "http://www.cnblogs.com/zyw-205520/tag/%E5%BE%AE%E4%BF%A1/", dataItem);
		System.out.println(json);
	}
}
